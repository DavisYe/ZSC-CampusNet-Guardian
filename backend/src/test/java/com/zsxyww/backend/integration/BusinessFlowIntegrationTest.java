package com.zsxyww.backend.integration;

import com.zsxyww.backend.config.FileConfig;
import com.zsxyww.backend.model.dto.CreateRepairOrderRequest;
import com.zsxyww.backend.model.dto.LoginRequest;
import com.zsxyww.backend.model.dto.UpdateOrderStatusRequest;
import com.zsxyww.backend.model.entity.KnowledgeArticle;
import com.zsxyww.backend.model.entity.KnowledgeCategory;
import com.zsxyww.backend.model.enums.RepairOrderStatus;
import com.zsxyww.backend.model.enums.RepairOrderType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = {
    "classpath:db/schema.sql",
    "classpath:db/data-h2.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BusinessFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FileConfig fileConfig;
    
    @Test
    void testCompleteRepairOrderFlow() throws Exception {
        // 确保文件上传目录存在
        Path uploadPath = Paths.get(fileConfig.getFullPath());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        try {
            // 1. 用户登录
            String userToken = getUserToken();
            String repairmanToken = getRepairmanToken();

            // 2. 创建工单
            CreateRepairOrderRequest orderRequest = new CreateRepairOrderRequest();
            orderRequest.setType(RepairOrderType.NETWORK);
            orderRequest.setDescription("网络故障");
            orderRequest.setLocation("教学楼A101");
            orderRequest.setContactPhone("13800138000");

            MvcResult createResult = mockMvc.perform(post("/repair-orders")
                    .header("Authorization", "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String orderId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                    .get("data").get("id").asText();

            // 3. 维修工接单
            UpdateOrderStatusRequest updateRequest = new UpdateOrderStatusRequest();
            updateRequest.setStatus(RepairOrderStatus.PROCESSING.getCode());
            updateRequest.setRemark("正在处理");

            mockMvc.perform(put("/repair-orders/" + orderId + "/status")
                    .header("Authorization", "Bearer " + repairmanToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value(RepairOrderStatus.PROCESSING.getCode()));

            // 4. 上传维修照片
            MockMultipartFile file = new MockMultipartFile(
                "file",
                "repair.jpg",
                "image/jpeg",
                "test image content".getBytes()
            );

            mockMvc.perform(multipart("/files/upload")
                    .file(file)
                    .header("Authorization", "Bearer " + repairmanToken))
                    .andExpect(status().isOk());

            // 5. 完成维修
            updateRequest.setStatus(RepairOrderStatus.COMPLETED.getCode());
            updateRequest.setRemark("维修完成");

            mockMvc.perform(put("/repair-orders/" + orderId + "/status")
                    .header("Authorization", "Bearer " + repairmanToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value(RepairOrderStatus.COMPLETED.getCode()));

            // 6. 验证最终状态
            mockMvc.perform(get("/repair-orders/" + orderId)
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value(RepairOrderStatus.COMPLETED.getCode()));
        } finally {
            // 清理上传的文件
            if (Files.exists(uploadPath)) {
                Files.walk(uploadPath)
                    .sorted((a, b) -> b.compareTo(a)) // 倒序，先删除子文件
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // 忽略删除失败
                        }
                    });
            }
        }
    }

    @Test
    void testKnowledgeBaseFlow() throws Exception {
        // 确保文件上传目录存在
        Path uploadPath = Paths.get(fileConfig.getFullPath());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try {
            String adminToken = getAdminToken();

            // 1. 创建知识分类
            KnowledgeCategory category = new KnowledgeCategory();
            category.setName("网络故障");
            category.setDescription("常见网络故障解决方案");

            MvcResult categoryResult = mockMvc.perform(post("/knowledge/categories")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(category)))
                    .andExpect(status().isOk())
                    .andReturn();

            Long categoryId = objectMapper.readTree(categoryResult.getResponse().getContentAsString())
                    .get("data").get("id").asLong();

            // 2. 创建知识文章
            KnowledgeArticle article = new KnowledgeArticle();
            article.setTitle("网络故障排查指南");
            article.setContent("详细的排查步骤...");
            article.setCategoryId(categoryId);

            MvcResult articleResult = mockMvc.perform(post("/knowledge/articles")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(article)))
                    .andExpect(status().isOk())
                    .andReturn();

            Long articleId = objectMapper.readTree(articleResult.getResponse().getContentAsString())
                    .get("data").get("id").asLong();

            // 3. 上传文章附件
            MockMultipartFile attachment = new MockMultipartFile(
                "file",
                "guide.pdf",
                "application/pdf",
                "pdf content".getBytes()
            );

            mockMvc.perform(multipart("/files/upload")
                    .file(attachment)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk());

            // 4. 验证文章访问权限
            String userToken = getUserToken();
            mockMvc.perform(get("/knowledge/articles/" + articleId)
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk());

            // 5. 验证分类树结构
            mockMvc.perform(get("/knowledge/categories/tree")
                    .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].id").value(categoryId));
        } finally {
            // 清理上传的文件
            if (Files.exists(uploadPath)) {
                Files.walk(uploadPath)
                    .sorted((a, b) -> b.compareTo(a)) // 倒序，先删除子文件
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // 忽略删除失败
                        }
                    });
            }
        }
    }

    @Test
    void testDataConsistency() throws Exception {
        String adminToken = getAdminToken();
        String userToken = getUserToken();

        // 1. 创建工单并验证数据一致性
        CreateRepairOrderRequest orderRequest = new CreateRepairOrderRequest();
        orderRequest.setType(RepairOrderType.NETWORK);
        orderRequest.setDescription("测试工单");
        orderRequest.setLocation("测试位置");
        orderRequest.setContactPhone("13800138000");

        MvcResult createResult = mockMvc.perform(post("/repair-orders")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String orderId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        // 2. 并发更新测试
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        UpdateOrderStatusRequest updateRequest = new UpdateOrderStatusRequest();
        updateRequest.setStatus(RepairOrderStatus.PROCESSING.getCode());
        updateRequest.setRemark("并发测试");

        try {
            executor.submit(() -> {
                try {
                    mockMvc.perform(put("/repair-orders/" + orderId + "/status")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)));
                } catch (Exception e) {
                    // 预期可能失败
                } finally {
                    latch.countDown();
                }
            });

            executor.submit(() -> {
                try {
                    mockMvc.perform(put("/repair-orders/" + orderId + "/status")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)));
                } catch (Exception e) {
                    // 预期可能失败
                } finally {
                    latch.countDown();
                }
            });

            latch.await(5, TimeUnit.SECONDS);

            // 3. 验证最终状态一致性
            mockMvc.perform(get("/repair-orders/" + orderId)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value(RepairOrderStatus.PROCESSING.getCode()));
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void testTransactionRollback() throws Exception {
        String adminToken = getAdminToken();

        // 1. 测试创建分类回滚
        KnowledgeCategory category = new KnowledgeCategory();
        category.setName(""); // 无效名称，应触发回滚

        mockMvc.perform(post("/knowledge/categories")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isBadRequest());

        // 验证分类未被创建
        mockMvc.perform(get("/knowledge/categories/tree")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.name=='')]").doesNotExist());

        // 2. 测试工单状态更新回滚
        CreateRepairOrderRequest orderRequest = new CreateRepairOrderRequest();
        orderRequest.setType(RepairOrderType.NETWORK);
        orderRequest.setDescription("回滚测试");
        orderRequest.setLocation("测试位置");
        orderRequest.setContactPhone("13800138000");

        MvcResult createResult = mockMvc.perform(post("/repair-orders")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String orderId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asText();

        UpdateOrderStatusRequest updateRequest = new UpdateOrderStatusRequest();
        updateRequest.setStatus(999); // 无效状态，应触发回滚
        updateRequest.setRemark("测试回滚");

        mockMvc.perform(put("/repair-orders/" + orderId + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());

        // 验证状态未被更新
        mockMvc.perform(get("/repair-orders/" + orderId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(RepairOrderStatus.PENDING.getCode()));
    }

    private String getUserToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("test1234");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("data").get("token").asText();
    }

    private String getRepairmanToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("teststaff");
        loginRequest.setPassword("test1234");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("data").get("token").asText();
    }

    private String getAdminToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("data").get("token").asText();
    }
}
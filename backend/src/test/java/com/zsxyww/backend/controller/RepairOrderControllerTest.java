package com.zsxyww.backend.controller;

import com.zsxyww.backend.common.Result;
import com.zsxyww.backend.model.dto.CreateRepairOrderRequest;
import com.zsxyww.backend.model.dto.RepairOrderResponse;
import com.zsxyww.backend.model.dto.UpdateOrderStatusRequest;
import com.zsxyww.backend.model.entity.RepairOrder;
import com.zsxyww.backend.model.entity.User;
import com.zsxyww.backend.model.enums.RepairOrderStatus;
import com.zsxyww.backend.model.enums.RepairOrderType;
import com.zsxyww.backend.service.RepairOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairOrderControllerTest {

    @Mock
    private RepairOrderService repairOrderService;

    @InjectMocks
    private RepairOrderController repairOrderController;

    @Mock
    private SecurityContext securityContext;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        testUser = new User();
        testUser.setId(1L);  // 设置用户ID
        testUser.setUsername("testuser");  // 设置用户名

        // 设置用户权限
        Collection<? extends GrantedAuthority> authorities =
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        
        // 模拟用户认证
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(testUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void createOrder_ValidRequest_ReturnsSuccess() {
        // Arrange - 准备测试数据
        CreateRepairOrderRequest request = new CreateRepairOrderRequest();
        request.setType(RepairOrderType.NETWORK);  // 设置报修类型为网络故障
        request.setDescription("网络故障");        // 设置问题描述
        request.setLocation("教学楼A101");        // 设置故障地点
        request.setContactPhone("13800138000");   // 设置联系电话
        request.setImages("image1.jpg,image2.jpg"); // 设置故障图片

        // 模拟service层返回的响应
        when(repairOrderService.createOrder(any(RepairOrder.class)))
            .thenReturn(new RepairOrder());

        // Act - 调用被测试方法
        Result<RepairOrderResponse> result = repairOrderController.createOrder(request, testUser);

        // Assert - 验证结果
        assertTrue(result.getSuccess(), "创建报修单应该成功");
        // 验证service方法被正确调用
        verify(repairOrderService, times(1)).createOrder(any(RepairOrder.class));
    }

    @Test
    void updateOrderStatus_ValidRequest_ReturnsSuccess() {
        // Arrange - 准备测试数据
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(RepairOrderStatus.PROCESSING.getCode()); // 设置新状态为处理中
        request.setRemark("开始处理"); // 设置状态更新备注

        // 模拟service层返回的响应
        when(repairOrderService.updateOrderStatus(anyLong(), anyInt(), anyString()))
            .thenReturn(new RepairOrder());

        // Act - 调用被测试方法
        Result<RepairOrderResponse> result = repairOrderController.updateOrderStatus(1L, request);

        // Assert - 验证结果
        assertTrue(result.getSuccess(), "更新报修单状态应该成功");
        // 验证service方法被正确调用
        verify(repairOrderService, times(1))
            .updateOrderStatus(1L, RepairOrderStatus.PROCESSING.getCode(), "开始处理");
    }
}
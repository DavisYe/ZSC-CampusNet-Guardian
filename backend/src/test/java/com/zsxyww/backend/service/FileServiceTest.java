package com.zsxyww.backend.service;

import com.zsxyww.backend.config.FileConfig;
import com.zsxyww.backend.exception.BusinessException;
import com.zsxyww.backend.service.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * FileService的单元测试类
 *
 * <p>测试范围：
 * <ul>
 *   <li>测试文件上传功能</li>
 *   <li>测试文件删除功能</li>
 *   <li>验证文件类型和大小的校验</li>
 *   <li>测试并发场景下的文件操作</li>
 * </ul>
 *
 * <p>使用的测试框架：
 * <ul>
 *   <li>JUnit 5 - 用于编写测试用例</li>
 *   <li>Mockito - 用于模拟依赖对象</li>
 *   <li>TempDir - 用于创建临时目录</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    /**
     * 文件配置的Mock对象
     * 用于模拟FileConfig的行为
     */
    @Mock
    private FileConfig fileConfig;

    /**
     * 被测试的FileService实现类
     * 使用@InjectMocks自动注入Mock对象
     */
    @InjectMocks
    private FileServiceImpl fileService;

    /**
     * 临时目录路径
     * 使用JUnit 5的@TempDir注解自动创建和清理
     */
    @TempDir
    Path tempDir;

    // 文件大小限制
    private static final String MAX_FILE_SIZE = "10MB";
    // 允许的文件类型
    private static final String[] ALLOWED_TYPES = {
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/bmp",
        "image/webp"
    };

    /**
     * 测试准备方法
     * 在每个测试方法执行前运行
     * 初始化Mock对象的返回值
     */
    @BeforeEach
    void setUp() {
        // 初始化FileConfig的模拟返回值
        when(fileConfig.getPath()).thenReturn(tempDir.toString());  // 设置文件存储路径
        when(fileConfig.getMaxSize()).thenReturn(MAX_FILE_SIZE);  // 设置最大文件大小
        when(fileConfig.getAllowedTypes()).thenReturn(ALLOWED_TYPES);  // 设置允许的文件类型
        when(fileConfig.getUrlPrefix()).thenReturn("/uploads/");  // 设置URL前缀
        when(fileConfig.getFullPath()).thenReturn(tempDir.toString());  // 设置完整路径
    }

    /**
     * 测试上传有效图片文件
     * 场景：上传一个符合要求的JPEG图片文件
     * 预期：返回正确的文件URL，文件被保存到指定目录
     */
    @Test
    void uploadFile_ValidImage_ReturnsFileUrl() throws IOException {
        // Arrange - 准备测试数据
        // 创建一个有效的图片文件
        MockMultipartFile file = new MockMultipartFile(
            "file",  // 参数名
            "test.jpg",  // 文件名
            "image/jpeg",  // 文件类型
            "test content".getBytes()  // 文件内容
        );

        // Act - 调用被测试方法
        String result = fileService.uploadFile(file);

        // Assert - 验证结果
        assertNotNull(result, "返回的文件URL不应该为空");
        assertTrue(result.startsWith("/uploads/"), "文件URL应该以/uploads/开头");
        assertTrue(result.endsWith("test.jpg"), "文件URL应该以原文件名结尾");
        // 验证文件是否实际保存到磁盘
        assertTrue(Files.exists(tempDir.resolve("test.jpg")), "文件应该被保存到指定目录");
    }

    /**
     * 测试上传不允许的文件类型
     * 场景：上传一个exe可执行文件
     * 预期：抛出BusinessException异常
     */
    @Test
    void uploadFile_InvalidType_ThrowsException() {
        // Arrange - 准备测试数据
        // 创建一个不允许的文件类型（exe文件）
        MockMultipartFile file = new MockMultipartFile(
            "file",  // 参数名
            "test.exe",  // 文件名
            "application/x-msdownload",  // 不允许的文件类型
            "test content".getBytes()  // 文件内容
        );

        // Act & Assert - 调用被测试方法并验证异常
        assertThrows(BusinessException.class, () -> {
            fileService.uploadFile(file);
        }, "上传不允许的文件类型应该抛出BusinessException");
    }

    /**
     * 测试上传超过大小限制的文件
     * 场景：上传一个11MB的文件（超过10MB限制）
     * 预期：抛出BusinessException异常
     */
    @Test
    void uploadFile_ExceedSizeLimit_ThrowsException() {
        // Arrange - 准备测试数据
        // 创建一个超过大小限制的文件（11MB，超过10MB限制）
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile file = new MockMultipartFile(
            "file",  // 参数名
            "large.jpg",  // 文件名
            "image/jpeg",  // 文件类型
            largeContent  // 文件内容
        );

        // Act & Assert - 调用被测试方法并验证异常
        assertThrows(BusinessException.class, () -> {
            fileService.uploadFile(file);
        }, "上传超过大小限制的文件应该抛出BusinessException");
    }

    /**
     * 测试上传空文件
     * 场景：上传一个0字节的文件
     * 预期：抛出BusinessException异常
     */
    @Test
    void uploadFile_EmptyFile_ThrowsException() {
        // Arrange - 准备测试数据
        // 创建一个空文件
        MockMultipartFile file = new MockMultipartFile(
            "file",  // 参数名
            "empty.jpg",  // 文件名
            "image/jpeg",  // 文件类型
            new byte[0]  // 空文件内容
        );

        // Act & Assert - 调用被测试方法并验证异常
        assertThrows(BusinessException.class, () -> {
            fileService.uploadFile(file);
        }, "上传空文件应该抛出BusinessException");
    }

    /**
     * 测试批量上传有效文件
     * 场景：上传两个有效的图片文件
     * 预期：返回两个文件的URL，文件被保存到指定目录
     */
    @Test
    void uploadFiles_ValidFiles_ReturnsFileUrls() throws IOException {
        // Arrange - 准备测试数据
        // 创建两个有效的图片文件
        MockMultipartFile file1 = new MockMultipartFile(
            "file1",  // 参数名
            "test1.jpg",  // 文件名
            "image/jpeg",  // 文件类型
            "test content 1".getBytes()  // 文件内容
        );
        MockMultipartFile file2 = new MockMultipartFile(
            "file2",  // 参数名
            "test2.jpg",  // 文件名
            "image/jpeg",  // 文件类型
            "test content 2".getBytes()  // 文件内容
        );

        // Act - 调用被测试方法
        List<String> results = fileService.uploadFiles(Arrays.asList(file1, file2));

        // Assert - 验证结果
        assertEquals(2, results.size(), "应该返回两个文件的URL");
        assertTrue(results.stream().allMatch(url -> url.startsWith("/uploads/")),
            "所有文件URL都应该以/uploads/开头");
        assertTrue(results.stream().allMatch(url -> url.endsWith(".jpg")),
            "所有文件URL都应该以.jpg结尾");
        // 验证文件是否实际保存到磁盘
        assertTrue(Files.exists(tempDir.resolve("test1.jpg")), "第一个文件应该被保存");
        assertTrue(Files.exists(tempDir.resolve("test2.jpg")), "第二个文件应该被保存");
    }

    /**
     * 测试批量上传部分无效文件
     * 场景：上传一个有效文件和一个无效文件
     * 预期：抛出BusinessException异常
     */
    @Test
    void uploadFiles_PartialInvalid_ThrowsException() {
        // Arrange
        MockMultipartFile validFile = new MockMultipartFile(
            "file1",
            "test.jpg",
            "image/jpeg",
            "test content".getBytes()
        );
        MockMultipartFile invalidFile = new MockMultipartFile(
            "file2",
            "test.exe",
            "application/x-msdownload",
            "test content".getBytes()
        );

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            fileService.uploadFiles(Arrays.asList(validFile, invalidFile));
        });
    }

    /**
     * 测试并发上传文件
     * 场景：使用5个线程同时上传同一个文件
     * 预期：正确处理并发请求，部分上传可能失败
     */
    @Test
    void uploadFile_ConcurrentUploads_HandlesRaceCondition() throws InterruptedException {
        // Arrange
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "concurrent.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        try {
            // Act
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        fileService.uploadFile(file);
                    } catch (Exception e) {
                        // 预期部分上传可能失败
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // Assert
            assertTrue(latch.await(5, TimeUnit.SECONDS));
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 测试删除存在的文件
     * 场景：删除一个已存在的文件
     * 预期：文件被成功删除
     */
    @Test
    void deleteFile_ExistingFile_DeletesFile() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve("todelete.jpg");
        Files.write(testFile, "test content".getBytes());
        String fileUrl = "/uploads/todelete.jpg";

        // Act
        fileService.deleteFile(fileUrl);

        // Assert
        assertFalse(Files.exists(testFile));
    }

    /**
     * 测试删除不存在的文件
     * 场景：删除一个不存在的文件
     * 预期：抛出BusinessException异常
     */
    @Test
    void deleteFile_NonExistentFile_ThrowsException() {
        // Arrange
        String nonExistentFile = "/uploads/nonexistent.jpg";

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            fileService.deleteFile(nonExistentFile);
        });
    }

    /**
     * 测试批量删除文件
     * 场景：删除两个存在的文件
     * 预期：两个文件都被成功删除
     */
    @Test
    void deleteFiles_ValidFileUrls_DeletesAllFiles() throws IOException {
        // Arrange
        Path file1 = tempDir.resolve("todelete1.jpg");
        Path file2 = tempDir.resolve("todelete2.jpg");
        Files.write(file1, "test content 1".getBytes());
        Files.write(file2, "test content 2".getBytes());

        List<String> fileUrls = Arrays.asList(
            "/uploads/todelete1.jpg",
            "/uploads/todelete2.jpg"
        );

        // Act
        fileService.deleteFiles(fileUrls);

        // Assert
        assertFalse(Files.exists(file1));
        assertFalse(Files.exists(file2));
    }

    /**
     * 测试批量删除部分不存在的文件
     * 场景：删除一个存在的文件和一个不存在的文件
     * 预期：抛出BusinessException异常，已存在的文件未被删除
     */
    @Test
    void deleteFiles_PartialNonExistent_ThrowsException() throws IOException {
        // Arrange
        Path existingFile = tempDir.resolve("existing.jpg");
        Files.write(existingFile, "test content".getBytes());

        List<String> fileUrls = Arrays.asList(
            "/uploads/existing.jpg",
            "/uploads/nonexistent.jpg"
        );

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            fileService.deleteFiles(fileUrls);
        });
        // 验证已存在的文件未被删除
        assertTrue(Files.exists(existingFile));
    }

    /**
     * 测试并发删除文件
     * 场景：使用5个线程同时删除同一个文件
     * 预期：正确处理并发请求，文件最终被删除
     */
    @Test
    void deleteFile_ConcurrentDeletes_HandlesRaceCondition() throws IOException, InterruptedException {
        // Arrange
        Path testFile = tempDir.resolve("concurrent_delete.jpg");
        Files.write(testFile, "test content".getBytes());
        String fileUrl = "/uploads/concurrent_delete.jpg";

        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            // Act
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        fileService.deleteFile(fileUrl);
                    } catch (Exception e) {
                        // 预期部分删除可能失败
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // Assert
            assertTrue(latch.await(5, TimeUnit.SECONDS));
            assertFalse(Files.exists(testFile));
        } finally {
            executor.shutdown();
        }
    }
}
package com.zsxyww.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zsxyww.backend.exception.BusinessException;
import com.zsxyww.backend.mapper.RepairOrderMapper;
import com.zsxyww.backend.mapper.UserMapper;
import com.zsxyww.backend.model.entity.RepairOrder;
import com.zsxyww.backend.model.entity.User;
import com.zsxyww.backend.model.enums.RepairOrderStatus;
import com.zsxyww.backend.model.enums.RepairOrderType;
import com.zsxyww.backend.service.impl.RepairOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairOrderServiceTest {

    @Mock
    private RepairOrderMapper repairOrderMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private RepairOrderServiceImpl repairOrderService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(testUser.getUsername());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
    }

    @Test
    void createOrder_ValidRequest_ReturnsRepairOrder() {
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setType(RepairOrderType.NETWORK);
        repairOrder.setDescription("网络故障");
        repairOrder.setLocation("教学楼A101");
        repairOrder.setContactPhone("13800138000");

        when(repairOrderMapper.generateOrderNo()).thenReturn("202301010001");
        when(repairOrderMapper.insert(any(RepairOrder.class))).thenReturn(1);

        RepairOrder result = repairOrderService.createOrder(repairOrder);

        assertNotNull(result);
        assertEquals(RepairOrderStatus.PENDING, result.getStatus());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        assertEquals(testUser.getId(), result.getUserId());
        verify(repairOrderMapper, times(1)).insert(repairOrder);
    }

    @Test
    void createOrder_InvalidPhone_ThrowsException() {
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setType(RepairOrderType.NETWORK);
        repairOrder.setDescription("网络故障");
        repairOrder.setLocation("教学楼A101");
        repairOrder.setContactPhone("invalid-phone");

        assertThrows(IllegalArgumentException.class, () -> {
            repairOrderService.createOrder(repairOrder);
        });
    }

    @Test
    void createOrder_EmptyDescription_ThrowsException() {
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setType(RepairOrderType.NETWORK);
        repairOrder.setDescription("");
        repairOrder.setLocation("教学楼A101");
        repairOrder.setContactPhone("13800138000");

        assertThrows(IllegalArgumentException.class, () -> {
            repairOrderService.createOrder(repairOrder);
        });
    }

    @Test
    void createOrder_ConcurrentOrderCreation_GeneratesUniqueOrderNo() throws InterruptedException, ExecutionException {
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setType(RepairOrderType.NETWORK);
        repairOrder.setDescription("网络故障");
        repairOrder.setLocation("教学楼A101");
        repairOrder.setContactPhone("13800138000");

        when(repairOrderMapper.generateOrderNo())
            .thenReturn("202301010001")
            .thenReturn("202301010002");
        when(repairOrderMapper.insert(any(RepairOrder.class))).thenReturn(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<RepairOrder> future1 = executor.submit(() -> repairOrderService.createOrder(repairOrder));
            Future<RepairOrder> future2 = executor.submit(() -> repairOrderService.createOrder(repairOrder));

            assertNotEquals(future1.get().getOrderNo(), future2.get().getOrderNo());
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void updateOrderStatus_ValidRequest_ReturnsUpdatedOrder() {
        Long orderId = 1L;
        Integer status = RepairOrderStatus.PROCESSING.getCode();
        String remark = "开始处理";

        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setId(orderId);
        repairOrder.setStatus(RepairOrderStatus.PENDING);

        when(repairOrderMapper.selectById(orderId)).thenReturn(repairOrder);
        when(repairOrderMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);
        when(repairOrderMapper.selectById(orderId)).thenReturn(repairOrder);

        RepairOrder result = repairOrderService.updateOrderStatus(orderId, status, remark);

        assertNotNull(result);
        verify(repairOrderMapper, times(2)).selectById(orderId);
        verify(repairOrderMapper, times(1)).update(any(), any(LambdaUpdateWrapper.class));
    }

    @Test
    void updateOrderStatus_InvalidStatusTransition_ThrowsException() {
        Long orderId = 1L;
        Integer status = RepairOrderStatus.PENDING.getCode();
        String remark = "重新设为待处理";

        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setId(orderId);
        repairOrder.setStatus(RepairOrderStatus.COMPLETED);

        when(repairOrderMapper.selectById(orderId)).thenReturn(repairOrder);

        assertThrows(BusinessException.class, () -> {
            repairOrderService.updateOrderStatus(orderId, status, remark);
        });
    }

    @Test
    void updateOrderStatus_ConcurrentUpdate_HandlesRaceCondition() throws InterruptedException {
        Long orderId = 1L;
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setId(orderId);
        repairOrder.setStatus(RepairOrderStatus.PENDING);

        when(repairOrderMapper.selectById(orderId)).thenReturn(repairOrder);
        when(repairOrderMapper.update(any(), any(LambdaUpdateWrapper.class)))
            .thenReturn(1)
            .thenReturn(0);

        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        try {
            executor.submit(() -> {
                try {
                    repairOrderService.updateOrderStatus(orderId, RepairOrderStatus.PROCESSING.getCode(), "处理中");
                } catch (Exception e) {
                    // 预期第二个线程会抛出异常
                } finally {
                    latch.countDown();
                }
            });

            executor.submit(() -> {
                try {
                    repairOrderService.updateOrderStatus(orderId, RepairOrderStatus.PROCESSING.getCode(), "处理中");
                } catch (Exception e) {
                    // 预期第二个线程会抛出异常
                } finally {
                    latch.countDown();
                }
            });

            latch.await(5, TimeUnit.SECONDS);
            verify(repairOrderMapper, atLeast(2)).selectById(orderId);
            verify(repairOrderMapper, times(2)).update(any(), any(LambdaUpdateWrapper.class));
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void updateOrderStatus_WithoutPermission_ThrowsAccessDeniedException() {
        Long orderId = 1L;
        Integer status = RepairOrderStatus.PROCESSING.getCode();
        
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(AccessDeniedException.class, () -> {
            repairOrderService.updateOrderStatus(orderId, status, "处理中");
        });
    }

    @Test
    void processOrder_CompleteWorkflow_Success() {
        Long orderId = 1L;
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setId(orderId);
        repairOrder.setStatus(RepairOrderStatus.PENDING);

        when(repairOrderMapper.selectById(orderId)).thenReturn(repairOrder);
        when(repairOrderMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);

        repairOrderService.updateOrderStatus(orderId, RepairOrderStatus.PROCESSING.getCode(), "开始处理");
        repairOrderService.updateOrderStatus(orderId, RepairOrderStatus.COMPLETED.getCode(), "维修完成");

        verify(repairOrderMapper, atLeast(2)).update(any(), any(LambdaUpdateWrapper.class));
    }

    @Test
    void processOrder_Deadlock_Prevention() throws InterruptedException {
        int threadCount = 3;
        Long[] orderIds = {1L, 2L, 3L};
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (Long orderId : orderIds) {
            RepairOrder order = new RepairOrder();
            order.setId(orderId);
            order.setStatus(RepairOrderStatus.PENDING);
            when(repairOrderMapper.selectById(orderId)).thenReturn(order);
            when(repairOrderMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = Arrays.stream(orderIds)
            .map(orderId -> executor.submit(() -> {
                try {
                    startLatch.await();
                    repairOrderService.updateOrderStatus(orderId, RepairOrderStatus.PROCESSING.getCode(), "处理中");
                    return null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    endLatch.countDown();
                }
            }))
            .collect(Collectors.toList());

        startLatch.countDown();

        try {
            endLatch.await(5, TimeUnit.SECONDS);
            futures.forEach(future -> {
                try {
                    future.get(1, TimeUnit.SECONDS);
                } catch (Exception e) {
                    fail("Deadlock detected: " + e.getMessage());
                }
            });
        } finally {
            executor.shutdown();
        }
    }
}
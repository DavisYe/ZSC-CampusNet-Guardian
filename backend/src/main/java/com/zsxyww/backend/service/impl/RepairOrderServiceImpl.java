package com.zsxyww.backend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsxyww.backend.exception.BusinessException;
import com.zsxyww.backend.mapper.RepairOrderMapper;
import com.zsxyww.backend.mapper.UserMapper;
import com.zsxyww.backend.model.entity.RepairOrder;
import com.zsxyww.backend.model.entity.User;
import com.zsxyww.backend.model.enums.RepairOrderStatus;
import com.zsxyww.backend.service.RepairOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 报修工单服务实现类
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements RepairOrderService {

    private final RepairOrderMapper repairOrderMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public RepairOrder createOrder(RepairOrder repairOrder) {
        // 生成工单编号
        String orderNo = repairOrderMapper.generateOrderNo();
        repairOrder.setOrderNo(orderNo);
        
        // 设置初始状态
        repairOrder.setStatus(RepairOrderStatus.PENDING);
        
        // 设置创建人
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.findByUsername(username);
        repairOrder.setUserId(user.getId());
        
        // 保存工单
        repairOrderMapper.insert(repairOrder);
        return repairOrder;
    }

    @Override
    @Transactional
    public RepairOrder updateOrderStatus(Long orderId, Integer status, String remark) {
        RepairOrder order = checkOrderExists(orderId);
        
        // 获取新状态的枚举值
        RepairOrderStatus newStatus = RepairOrderStatus.fromCode(status);
        if (newStatus == null) {
            throw new BusinessException("无效的工单状态");
        }
        
        // 验证状态转换的合法性
        validateStatusTransition(order.getStatus(), newStatus);
        
        // 更新状态
        int rows = repairOrderMapper.updateOrderStatus(orderId, status, remark);
        if (rows == 0) {
            throw new BusinessException("更新工单状态失败");
        }
        
        return getById(orderId);
    }

    @Override
    @Transactional
    public RepairOrder assignOrder(Long orderId, Long handlerId) {
        // 验证工单存在
        RepairOrder order = checkOrderExists(orderId);
        
        // 验证处理人存在
        User handler = userMapper.selectById(handlerId);
        if (handler == null) {
            throw new BusinessException("处理人不存在");
        }
        
        // 验证工单状态
        if (order.getStatus() != RepairOrderStatus.PENDING) {
            throw new BusinessException("只能分配待处理的工单");
        }
        
        // 分配处理人
        int rows = repairOrderMapper.assignHandler(orderId, handlerId);
        if (rows == 0) {
            throw new BusinessException("分配工单失败");
        }
        
        return getById(orderId);
    }

    @Override
    @Transactional
    public RepairOrder evaluateOrder(Long orderId, Integer rating, String evaluation) {
        RepairOrder order = checkOrderExists(orderId);
        
        // 验证工单状态
        if (order.getStatus() != RepairOrderStatus.COMPLETED) {
            throw new BusinessException("只能评价已完成的工单");
        }
        
        // 验证评分范围
        if (rating < 1 || rating > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }
        
        // 保存评价
        int rows = repairOrderMapper.evaluateOrder(orderId, rating, evaluation);
        if (rows == 0) {
            throw new BusinessException("评价工单失败");
        }
        
        return getById(orderId);
    }

    @Override
    public IPage<RepairOrder> getUserOrders(Long userId, Integer page, Integer size) {
        return repairOrderMapper.selectUserOrders(new Page<>(page, size), userId);
    }

    @Override
    public IPage<RepairOrder> getHandlerOrders(Long handlerId, Integer page, Integer size) {
        return repairOrderMapper.selectHandlerOrders(new Page<>(page, size), handlerId);
    }

    @Override
    public IPage<RepairOrder> getAllOrders(Integer status, Integer type, Integer page, Integer size) {
        return repairOrderMapper.selectAllOrders(new Page<>(page, size), status, type);
    }

    @Override
    @Transactional
    public RepairOrder reportOrder(Long orderId, String reason) {
        RepairOrder order = checkOrderExists(orderId);
        
        // 验证工单状态
        if (order.getStatus() != RepairOrderStatus.PROCESSING) {
            throw new BusinessException("只能上报处理中的工单");
        }
        
        // 上报工单
        int rows = repairOrderMapper.reportOrder(orderId, reason);
        if (rows == 0) {
            throw new BusinessException("上报工单失败");
        }
        
        return getById(orderId);
    }

    @Override
    public RepairOrder getOrderDetail(Long orderId) {
        return checkOrderExists(orderId);
    }

    /**
     * 验证工单是否存在
     */
    private RepairOrder checkOrderExists(Long orderId) {
        RepairOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        return order;
    }

    /**
     * 验证工单状态转换的合法性
     */
    private void validateStatusTransition(RepairOrderStatus currentStatus, RepairOrderStatus newStatus) {
        // 待处理 -> 处理中
        if (currentStatus == RepairOrderStatus.PENDING) {
            if (newStatus != RepairOrderStatus.PROCESSING) {
                throw new BusinessException("待处理的工单只能转为处理中状态");
            }
        }
        // 处理中 -> 已完成/已上报
        else if (currentStatus == RepairOrderStatus.PROCESSING) {
            if (newStatus != RepairOrderStatus.COMPLETED && newStatus != RepairOrderStatus.REPORTED) {
                throw new BusinessException("处理中的工单只能转为已完成或已上报状态");
            }
        }
        // 其他状态不允许修改
        else {
            throw new BusinessException("当前状态不允许修改");
        }
    }
}
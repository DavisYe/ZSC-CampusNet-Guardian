package com.zsxyww.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zsxyww.backend.model.entity.RepairOrder;

/**
 * 报修工单服务接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
public interface RepairOrderService extends IService<RepairOrder> {
    
    /**
     * 创建工单
     *
     * @param repairOrder 工单信息
     * @return 创建后的工单
     */
    RepairOrder createOrder(RepairOrder repairOrder);
    
    /**
     * 更新工单状态
     *
     * @param orderId 工单ID
     * @param status 新状态
     * @param remark 备注信息
     * @return 更新后的工单
     */
    RepairOrder updateOrderStatus(Long orderId, Integer status, String remark);
    
    /**
     * 分配工单
     *
     * @param orderId 工单ID
     * @param handlerId 处理人ID
     * @return 更新后的工单
     */
    RepairOrder assignOrder(Long orderId, Long handlerId);
    
    /**
     * 评价工单
     *
     * @param orderId 工单ID
     * @param rating 评分
     * @param evaluation 评价内容
     * @return 更新后的工单
     */
    RepairOrder evaluateOrder(Long orderId, Integer rating, String evaluation);
    
    /**
     * 获取用户的工单列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 工单分页列表
     */
    IPage<RepairOrder> getUserOrders(Long userId, Integer page, Integer size);
    
    /**
     * 获取处理人的工单列表
     *
     * @param handlerId 处理人ID
     * @param page 页码
     * @param size 每页大小
     * @return 工单分页列表
     */
    IPage<RepairOrder> getHandlerOrders(Long handlerId, Integer page, Integer size);
    
    /**
     * 获取所有工单列表（管理员使用）
     *
     * @param status 状态过滤
     * @param type 类型过滤
     * @param page 页码
     * @param size 每页大小
     * @return 工单分页列表
     */
    IPage<RepairOrder> getAllOrders(Integer status, Integer type, Integer page, Integer size);
    
    /**
     * 上报工单
     *
     * @param orderId 工单ID
     * @param reason 上报原因
     * @return 更新后的工单
     */
    RepairOrder reportOrder(Long orderId, String reason);
    
    /**
     * 获取工单详情
     *
     * @param orderId 工单ID
     * @return 工单详情
     */
    RepairOrder getOrderDetail(Long orderId);
}
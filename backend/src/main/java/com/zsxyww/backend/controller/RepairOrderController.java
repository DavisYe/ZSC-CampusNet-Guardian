package com.zsxyww.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zsxyww.backend.common.Result;
import com.zsxyww.backend.model.dto.CreateRepairOrderRequest;
import com.zsxyww.backend.model.dto.RepairOrderResponse;
import com.zsxyww.backend.model.dto.UpdateOrderStatusRequest;
import com.zsxyww.backend.model.entity.RepairOrder;
import com.zsxyww.backend.model.entity.User;
import com.zsxyww.backend.service.RepairOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 报修工单控制器
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/api/repair-orders")
@RequiredArgsConstructor
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    /**
     * 创建工单
     */
    @PostMapping
    public Result<RepairOrderResponse> createOrder(@RequestBody @Valid CreateRepairOrderRequest request,
                                                 @AuthenticationPrincipal User user) {
        RepairOrder order = new RepairOrder();
        order.setType(request.getType());
        order.setDescription(request.getDescription());
        order.setLocation(request.getLocation());
        order.setContactPhone(request.getContactPhone());
        order.setImages(request.getImages());
        order.setUserId(user.getId());

        return Result.success(convertToResponse(repairOrderService.createOrder(order)));
    }

    /**
     * 更新工单状态
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public Result<RepairOrderResponse> updateOrderStatus(@PathVariable Long orderId,
                                                       @RequestBody @Valid UpdateOrderStatusRequest request) {
        RepairOrder order = repairOrderService.updateOrderStatus(orderId, request.getStatus(), request.getRemark());
        return Result.success(convertToResponse(order));
    }

    /**
     * 分配工单
     */
    @PutMapping("/{orderId}/assign/{handlerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<RepairOrderResponse> assignOrder(@PathVariable Long orderId,
                                                 @PathVariable Long handlerId) {
        RepairOrder order = repairOrderService.assignOrder(orderId, handlerId);
        return Result.success(convertToResponse(order));
    }

    /**
     * 评价工单
     */
    @PutMapping("/{orderId}/evaluate")
    public Result<RepairOrderResponse> evaluateOrder(@PathVariable Long orderId,
                                                   @RequestParam @Min(1) @Max(5) Integer rating,
                                                   @RequestParam(required = false) String evaluation) {
        RepairOrder order = repairOrderService.evaluateOrder(orderId, rating, evaluation);
        return Result.success(convertToResponse(order));
    }

    /**
     * 获取用户的工单列表
     */
    @GetMapping("/my")
    public Result<IPage<RepairOrderResponse>> getUserOrders(@RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size,
                                                          @AuthenticationPrincipal User user) {
        IPage<RepairOrder> orders = repairOrderService.getUserOrders(user.getId(), page, size);
        return Result.success(orders.convert(this::convertToResponse));
    }

    /**
     * 获取处理人的工单列表
     */
    @GetMapping("/handler/{handlerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public Result<IPage<RepairOrderResponse>> getHandlerOrders(@PathVariable Long handlerId,
                                                             @RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size) {
        IPage<RepairOrder> orders = repairOrderService.getHandlerOrders(handlerId, page, size);
        return Result.success(orders.convert(this::convertToResponse));
    }

    /**
     * 获取所有工单列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<RepairOrderResponse>> getAllOrders(@RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "10") Integer size,
                                                         @RequestParam(required = false) Integer status,
                                                         @RequestParam(required = false) Integer type) {
        IPage<RepairOrder> orders = repairOrderService.getAllOrders(status, type, page, size);
        return Result.success(orders.convert(this::convertToResponse));
    }

    /**
     * 获取工单详情
     */
    @GetMapping("/{orderId}")
    public Result<RepairOrderResponse> getOrderDetail(@PathVariable @NotNull Long orderId) {
        RepairOrder order = repairOrderService.getOrderDetail(orderId);
        return Result.success(convertToResponse(order));
    }

    /**
     * 上报工单
     */
    @PutMapping("/{orderId}/report")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public Result<RepairOrderResponse> reportOrder(@PathVariable Long orderId,
                                                 @RequestParam @NotNull String reason) {
        RepairOrder order = repairOrderService.reportOrder(orderId, reason);
        return Result.success(convertToResponse(order));
    }

    /**
     * 将实体转换为响应DTO
     */
    private RepairOrderResponse convertToResponse(RepairOrder order) {
        if (order == null) {
            return null;
        }

        RepairOrderResponse response = new RepairOrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setUserId(order.getUserId());
        response.setType(order.getType());
        response.setStatus(order.getStatus());
        response.setDescription(order.getDescription());
        response.setLocation(order.getLocation());
        response.setContactPhone(order.getContactPhone());
        response.setImages(order.getImages());
        response.setHandlerId(order.getHandlerId());
        response.setHandleStartTime(order.getHandleStartTime());
        response.setHandleEndTime(order.getHandleEndTime());
        response.setHandleResult(order.getHandleResult());
        response.setHandleRemark(order.getHandleRemark());
        response.setRating(order.getRating());
        response.setEvaluation(order.getEvaluation());
        response.setEvaluationTime(order.getEvaluationTime());
        response.setNeedReport(order.getNeedReport());
        response.setReportReason(order.getReportReason());
        response.setPriority(order.getPriority());
        response.setCreateTime(order.getCreateTime());
        response.setUpdateTime(order.getUpdateTime());

        return response;
    }
}
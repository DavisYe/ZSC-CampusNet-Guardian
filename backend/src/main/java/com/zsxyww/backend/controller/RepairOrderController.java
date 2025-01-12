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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 报修工单控制器
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Tag(name = "报修工单管理", description = "报修工单相关接口")
@Validated
@RestController
@RequestMapping("/api/repair-orders")
@RequiredArgsConstructor
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    @Operation(summary = "创建报修工单", description = "用户创建新的报修工单")
    @PostMapping
    public Result<RepairOrderResponse> createOrder(
            @Parameter(description = "工单创建请求参数") @RequestBody @Valid CreateRepairOrderRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        RepairOrder order = new RepairOrder();
        order.setType(request.getType());
        order.setDescription(request.getDescription());
        order.setLocation(request.getLocation());
        order.setContactPhone(request.getContactPhone());
        order.setImages(request.getImages());
        order.setUserId(user.getId());

        return Result.success(convertToResponse(repairOrderService.createOrder(order)));
    }

    @Operation(summary = "更新工单状态", description = "管理员或维修人员更新工单的处理状态")
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public Result<RepairOrderResponse> updateOrderStatus(
            @Parameter(description = "工单ID") @PathVariable Long orderId,
            @Parameter(description = "状态更新请求参数") @RequestBody @Valid UpdateOrderStatusRequest request) {
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

    @Operation(summary = "评价工单", description = "用户对已完成的工单进行评价")
    @PutMapping("/{orderId}/evaluate")
    public Result<RepairOrderResponse> evaluateOrder(
            @Parameter(description = "工单ID") @PathVariable Long orderId,
            @Parameter(description = "评分(1-5)") @RequestParam @Min(1) @Max(5) Integer rating,
            @Parameter(description = "评价内容") @RequestParam(required = false) String evaluation) {
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

    @Operation(summary = "获取所有工单列表", description = "管理员获取所有工单的列表，支持分页和状态、类型筛选")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<RepairOrderResponse>> getAllOrders(
            @Parameter(description = "页码，默认1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小，默认10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "工单状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "工单类型") @RequestParam(required = false) Integer type) {
        IPage<RepairOrder> orders = repairOrderService.getAllOrders(status, type, page, size);
        return Result.success(orders.convert(this::convertToResponse));
    }

    @Operation(summary = "获取工单详情", description = "根据工单ID获取工单的详细信息")
    @GetMapping("/{orderId}")
    public Result<RepairOrderResponse> getOrderDetail(
            @Parameter(description = "工单ID") @PathVariable @NotNull Long orderId) {
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
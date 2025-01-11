package com.zsxyww.backend.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新工单状态请求DTO
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
public class UpdateOrderStatusRequest {
    
    /**
     * 工单状态
     * 0: 待处理
     * 1: 处理中
     * 2: 已上报
     * 3: 已完成
     * 4: 已取消
     */
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "无效的状态值")
    @Max(value = 4, message = "无效的状态值")
    private Integer status;
    
    /**
     * 备注信息
     */
    private String remark;
    
    /**
     * 处理结果描述（当状态为已完成时必填）
     */
    private String handleResult;
    
    /**
     * 上报原因（当状态为已上报时必填）
     */
    private String reportReason;
}
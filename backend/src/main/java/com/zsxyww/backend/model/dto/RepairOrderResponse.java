package com.zsxyww.backend.model.dto;

import com.zsxyww.backend.model.enums.RepairOrderStatus;
import com.zsxyww.backend.model.enums.RepairOrderType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单响应DTO
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
public class RepairOrderResponse {
    
    /**
     * 工单ID
     */
    private Long id;
    
    /**
     * 工单编号
     */
    private String orderNo;
    
    /**
     * 报修用户ID
     */
    private Long userId;
    
    /**
     * 报修用户名
     */
    private String username;
    
    /**
     * 报修用户学号
     */
    private String studentId;
    
    /**
     * 工单类型
     */
    private RepairOrderType type;
    
    /**
     * 工单状态
     */
    private RepairOrderStatus status;
    
    /**
     * 故障描述
     */
    private String description;
    
    /**
     * 故障地点
     */
    private String location;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 图片URL列表
     */
    private String images;
    
    /**
     * 处理人ID
     */
    private Long handlerId;
    
    /**
     * 处理人姓名
     */
    private String handlerName;
    
    /**
     * 处理开始时间
     */
    private LocalDateTime handleStartTime;
    
    /**
     * 处理完成时间
     */
    private LocalDateTime handleEndTime;
    
    /**
     * 处理结果描述
     */
    private String handleResult;
    
    /**
     * 处理备注
     */
    private String handleRemark;
    
    /**
     * 评分
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String evaluation;
    
    /**
     * 评价时间
     */
    private LocalDateTime evaluationTime;
    
    /**
     * 是否需要上报
     */
    private Boolean needReport;
    
    /**
     * 上报原因
     */
    private String reportReason;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
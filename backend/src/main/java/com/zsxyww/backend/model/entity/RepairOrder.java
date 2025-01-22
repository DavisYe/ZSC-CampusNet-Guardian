package com.zsxyww.backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zsxyww.backend.model.base.BaseEntity;
import com.zsxyww.backend.model.enums.RepairOrderStatus;
import com.zsxyww.backend.model.enums.RepairOrderType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 报修工单实体类
 * 用于记录用户提交的报修信息
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
@TableName("ww_repair_order")
@EqualsAndHashCode(callSuper = true)
public class RepairOrder extends BaseEntity {
    
    /**
     * 工单编号
     * 系统自动生成的唯一编号
     */
    private String orderNo;
    
    /**
     * 报修用户ID
     */
    private Long userId;
    
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
     * 多个URL用逗号分隔
     */
    private String images;
    
    /**
     * 处理人ID
     */
    private Long handlerId;
    
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
     * 评分（1-5）
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
     * 优先级（1-5，数字越大优先级越高）
     */
    private Integer priority;
}
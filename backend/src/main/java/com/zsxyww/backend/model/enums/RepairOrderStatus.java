package com.zsxyww.backend.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 报修工单状态枚举
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Getter
public enum RepairOrderStatus {
    
    /**
     * 待处理
     */
    PENDING(0, "待处理"),
    
    /**
     * 处理中
     */
    PROCESSING(1, "处理中"),
    
    /**
     * 已上报（无法处理）
     */
    REPORTED(2, "已上报"),
    
    /**
     * 已完成
     */
    COMPLETED(3, "已完成"),
    
    /**
     * 已取消
     */
    CANCELLED(4, "已取消");
    
    /**
     * 状态码
     */
    @EnumValue
    private final int code;
    
    /**
     * 状态描述
     */
    private final String description;
    
    RepairOrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
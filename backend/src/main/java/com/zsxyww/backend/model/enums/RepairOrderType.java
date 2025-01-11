package com.zsxyww.backend.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 报修工单类型枚举
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Getter
public enum RepairOrderType {
    
    /**
     * 网络故障
     */
    NETWORK(0, "网络故障"),
    
    /**
     * 硬件故障
     */
    HARDWARE(1, "硬件故障"),
    
    /**
     * 软件故障
     */
    SOFTWARE(2, "软件故障"),
    
    /**
     * 账号问题
     */
    ACCOUNT(3, "账号问题"),
    
    /**
     * 其他问题
     */
    OTHER(4, "其他问题");
    
    /**
     * 类型码
     */
    @EnumValue
    private final int code;
    
    /**
     * 类型描述
     */
    private final String description;
    
    RepairOrderType(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
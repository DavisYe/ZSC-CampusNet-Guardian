package com.zsxyww.backend.model.dto;

import com.zsxyww.backend.model.enums.RepairOrderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 创建工单请求DTO
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Data
public class CreateRepairOrderRequest {
    
    /**
     * 故障类型
     */
    @NotNull(message = "故障类型不能为空")
    private RepairOrderType type;
    
    /**
     * 故障描述
     */
    @NotBlank(message = "故障描述不能为空")
    private String description;
    
    /**
     * 故障地点
     */
    @NotBlank(message = "故障地点不能为空")
    private String location;
    
    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    private String contactPhone;
    
    /**
     * 图片URL列表，多个URL用逗号分隔
     */
    private String images;
}
package com.zsxyww.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsxyww.backend.model.entity.RepairOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 报修工单Mapper接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Mapper
public interface RepairOrderMapper extends BaseMapper<RepairOrder> {

    /**
     * 生成工单编号
     * 格式：年月日+4位序号，如：202501110001
     */
    @Select("SELECT CONCAT(DATE_FORMAT(NOW(), '%Y%m%d'), " +
            "LPAD(COALESCE(MAX(SUBSTR(order_no, -4)) + 1, 1), 4, '0')) " +
            "FROM repair_order " +
            "WHERE order_no LIKE CONCAT(DATE_FORMAT(NOW(), '%Y%m%d'), '%')")
    String generateOrderNo();
}
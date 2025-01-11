package com.zsxyww.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsxyww.backend.model.entity.RepairOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    /**
     * 获取用户的工单列表
     */
    @Select("SELECT * FROM repair_order " +
            "WHERE user_id = #{userId} AND deleted = 0 " +
            "ORDER BY create_time DESC")
    IPage<RepairOrder> selectUserOrders(Page<RepairOrder> page, @Param("userId") Long userId);

    /**
     * 获取处理人的工单列表
     */
    @Select("SELECT * FROM repair_order " +
            "WHERE handler_id = #{handlerId} AND deleted = 0 " +
            "ORDER BY create_time DESC")
    IPage<RepairOrder> selectHandlerOrders(Page<RepairOrder> page, @Param("handlerId") Long handlerId);

    /**
     * 获取所有工单列表（支持状态和类型过滤）
     */
    @Select("<script>" +
            "SELECT * FROM repair_order WHERE deleted = 0 " +
            "<if test='status != null'> AND status = #{status} </if>" +
            "<if test='type != null'> AND type = #{type} </if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<RepairOrder> selectAllOrders(Page<RepairOrder> page,
                                     @Param("status") Integer status,
                                     @Param("type") Integer type);

    /**
     * 更新工单状态
     */
    @Update("UPDATE repair_order SET status = #{status}, " +
            "handle_remark = #{remark}, " +
            "<if test='status == 1'>handle_start_time = NOW(),</if>" +
            "<if test='status == 3'>handle_end_time = NOW(),</if>" +
            "update_time = NOW() " +
            "WHERE id = #{orderId} AND deleted = 0")
    int updateOrderStatus(@Param("orderId") Long orderId,
                         @Param("status") Integer status,
                         @Param("remark") String remark);

    /**
     * 分配工单处理人
     */
    @Update("UPDATE repair_order SET handler_id = #{handlerId}, " +
            "status = 1, " +
            "handle_start_time = NOW(), " +
            "update_time = NOW() " +
            "WHERE id = #{orderId} AND deleted = 0")
    int assignHandler(@Param("orderId") Long orderId,
                     @Param("handlerId") Long handlerId);

    /**
     * 评价工单
     */
    @Update("UPDATE repair_order SET rating = #{rating}, " +
            "evaluation = #{evaluation}, " +
            "evaluation_time = NOW(), " +
            "update_time = NOW() " +
            "WHERE id = #{orderId} AND deleted = 0")
    int evaluateOrder(@Param("orderId") Long orderId,
                     @Param("rating") Integer rating,
                     @Param("evaluation") String evaluation);

    /**
     * 上报工单
     */
    @Update("UPDATE repair_order SET need_report = 1, " +
            "report_reason = #{reason}, " +
            "status = 2, " +
            "update_time = NOW() " +
            "WHERE id = #{orderId} AND deleted = 0")
    int reportOrder(@Param("orderId") Long orderId,
                   @Param("reason") String reason);
}
package com.itheima.dao;

import com.itheima.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map; /**
 * 预约设置接口
 */
public interface OrderSettingDao {
    /**
     * 先查询当前日期是否存在
     * @param orderDate
     * @return
     */
    int findCountByOrderDate(Date orderDate);

    /**
     * 执行更新预约设置
     * @param orderSetting
     */
    void editNumberByOrderDate(OrderSetting orderSetting);

    /**
     * 保存到预约设置表
     * @param orderSetting
     */
    void add(OrderSetting orderSetting);

    /**
     * 获取年月对应预约设置数据
     * @param map
     * @return
     */
    List<OrderSetting> getOrderSettingByMonth(Map map);

    /**
     * 根据预约日期查询预约信息
     * @param newOrderDate
     * @return
     */
    OrderSetting findByOrderDate(Date newOrderDate);

    /**
     * 根据预约日期修改已经预约人数+1
     * @param newOrderDate
     */
    void editReservationsByOrderDate(Date newOrderDate);
}

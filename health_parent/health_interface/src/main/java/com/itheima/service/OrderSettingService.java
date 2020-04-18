package com.itheima.service;

import com.itheima.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

/**
 *
 * 预约设置
 *   批量预约设置
 *   单个预约设置
 */
public interface OrderSettingService {
    /**
     * 批量预约设置
     * @param listOrderSetting
     */
    void add(List<OrderSetting> listOrderSetting);
    /**
     * 获取年月对应预约设置数据
     */
    List<Map> getOrderSettingByMonth(String date);

    /**
     * 单个预约设置
     * @param orderSetting
     */
    void editNumberByDate(OrderSetting orderSetting);
}

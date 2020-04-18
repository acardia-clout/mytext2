package com.itheima.service;

import com.itheima.entity.Result;

import java.util.Map;

public interface OrderMobileService {
    /**
     * 体检预约功能
     * @param map
     * @return
     */
    Result submitOrder(Map map) throws Exception;
    /**
     * 预约成功页面展示
     */
    Map findById(Integer id) throws Exception;
}

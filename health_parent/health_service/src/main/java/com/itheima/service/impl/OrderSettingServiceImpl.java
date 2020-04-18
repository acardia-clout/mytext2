package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约设置实现类
 */
@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Override
    public void add(List<OrderSetting> listOrderSetting) {
        if(listOrderSetting != null && listOrderSetting.size()>0){
            for (OrderSetting orderSetting : listOrderSetting) {
                //先查询当前日期是否存在
                int count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                //如果存在，执行更新预约设置
                if(count>0){
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }else {
                    //如果不存在，保存到预约设置表
                    orderSettingDao.add(orderSetting);
                }

            }
        }
    }
    /**
     * 获取年月对应预约设置数据 date:2020-04
     */
    @Override
    public List<Map> getOrderSettingByMonth(String date) {
        //方式一：select * from t_ordersetting where orderDate BETWEEN '2020-04-01' and '2020-04-31';
        String dateBegin = date+"-01";
        String dateEnd = date + "-31";
        Map map = new HashMap();
        map.put("dateBegin",dateBegin);
        map.put("dateEnd",dateEnd);
        List<OrderSetting> orderSettingList = orderSettingDao.getOrderSettingByMonth(map);
        //返回的数据处理，对orderDate处理获取几号，最终返回页面List<Map> (map: date \ number \reservations
        //定义一个返回的List<Map>
        List<Map> rsMap = new ArrayList<>();
        for (OrderSetting orderSetting : orderSettingList) {
            //定义一个map
            Map orderSettingMap = new HashMap();
            orderSettingMap.put("date",orderSetting.getOrderDate().getDate());//获取几号
            orderSettingMap.put("number",orderSetting.getNumber());//可预约人数
            orderSettingMap.put("reservations",orderSetting.getReservations());//已经预约人数
            rsMap.add(orderSettingMap);
        }
        return rsMap;
    }
    /**
     * 单个预约设置
     * @param orderSetting
     */
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        //1.先根据年月日参数查询预约记录是否存在
        int count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        //2.存在，则update t_ordersetting set number = 100 where orderDate = ‘2020-04-09’
        if(count>0){
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else {
            //3.不存在，插入单个预约记录
            orderSettingDao.add(orderSetting);
        }
    }

    //抽取一个方法（实际项目中需要抽取的）
}

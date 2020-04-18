package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderMobileService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 体检预约业务逻辑层
 */
@Service(interfaceClass = OrderMobileService.class)
@Transactional
public class OrderMobileServiceImpl implements OrderMobileService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    /**
     * 体检预约
     */
    @Override
    public Result submitOrder(Map map) throws Exception {
        String orderDate = (String)map.get("orderDate");//预约日期
        String telephone = (String)map.get("telephone");//手机号码
        String setmealId = (String)map.get("setmealId");//套餐id
        String name = (String)map.get("name");//姓名
        String sex = (String)map.get("sex");//性别
        String idCard = (String)map.get("idCard");//身份证
        String orderType = (String)map.get("orderType");//预约方式
        //将string 转date
        Date newOrderDate = DateUtils.parseString2Date(orderDate);
        //1.查询预约日期是否存在
        OrderSetting orderSetting =  orderSettingDao.findByOrderDate(newOrderDate);
        //2.不存在 直接返回错误提示
        if(orderSetting == null){
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //3.存在，则查询是否约满
        int reservations = orderSetting.getReservations();//已经预约人数
        int number = orderSetting.getNumber();//可预约人数
        //4.已经约满，直接返回错误提示
        if(reservations>=number){
            return new Result(false, MessageConstant.ORDER_FULL);
        }
        //5.未约满，查询是否会员 根据手机号码
        Member member = memberDao.findByTelephone(telephone);
        //6.是会员，判断是否重复预约
        if(member != null){
            //条件：同一个人 同一个套餐  同一个时间
            //封装查询条件
            Order order = new Order();
            order.setMemberId(member.getId());//会员id
            order.setSetmealId(Integer.parseInt(setmealId));//套餐id
            order.setOrderDate(newOrderDate);//预约日期
            List<Order> listOrder = orderDao.findByCondition(order);
            //7.如果重复预约，直接返回错误提示
            if(listOrder != null && listOrder.size()>0){
                return new Result(false,MessageConstant.HAS_ORDERED);
            }
        }
        //8.不是会员，自动注册会员
        if(member == null){
            member = new Member();
            member.setName(name);//会员名称
            member.setSex(sex);//性别
            member.setIdCard(idCard);//身份证
            member.setPhoneNumber(telephone);//手机号码
            member.setRegTime(new Date());//注册时间
            memberDao.add(member);
        }

        //9.插入体检预约表
        Order order = new Order(member.getId(),newOrderDate,orderType,Order.ORDERSTATUS_NO,Integer.parseInt(setmealId));
        orderDao.add(order);
        //10.更新预约设置表 预约人数+1
        orderSettingDao.editReservationsByOrderDate(newOrderDate);
        //order:需要返回页面（预约页面跟预约id获取预约信息）
        return new Result(true,MessageConstant.ORDER_SUCCESS,order);// order对象返回 页面需要用体检预约id 关联获取体检预约信息
    }
    /**
     * 预约成功页面展示
     */
    @Override
    public Map findById(Integer id) throws Exception {
        Map map = orderDao.findById4Detail(id);
        if(map!=null){
            //将日期对象处理string
            Date orderDate = (Date)map.get("orderDate");
            map.put("orderDate",DateUtils.parseDate2String(orderDate));
        }
        return map;
    }


}

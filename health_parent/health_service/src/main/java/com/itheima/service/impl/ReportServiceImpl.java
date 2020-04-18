package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.service.ReportService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营统计数据服务接口
 */
@Service(interfaceClass = ReportService.class)
@Transactional
public class ReportServiceImpl implements ReportService
{

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    /**
     * 运营统计数据服务接口
     */
    @Override
    public Map<String, Object> getBusinessReportData() throws Exception {
        //1.获取当前日期
        String reportDate = DateUtils.parseDate2String(DateUtils.getToday());
        //获取本周一的时间
        String thisWeekMonday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
        //获取本月第一天的时间
        String firstDay4ThisMonth = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());
        //获取本周日时间
        String thisWeekSunday = DateUtils.parseDate2String(DateUtils.getSundayOfThisWeek());
        //获取本月最后一天
        String  lastDay4ThisMonth= DateUtils.parseDate2String(DateUtils.getLastDay4ThisMonth());

        //2.获取今天新增会员的数量
        Integer todayNewMember = memberDao.findMemberCountByDate(reportDate);
        //3.总的会员数量
        Integer totalMember = memberDao.findMemberTotalCount();
        //4.本周新增会员 >=4月13号
        Integer thisWeekNewMember = memberDao.findMemberCountAfterDate(thisWeekMonday);
        //5.本月新增会员 >= 4月1号
        Integer thisMonthNewMember = memberDao.findMemberCountAfterDate(firstDay4ThisMonth);
        //6.今日预约数
        Integer todayOrderNumber = orderDao.findOrderCountByDate(reportDate);
        //7.本周预约数  >= 4月13 and <= 4月19号
        Map<String,Object> weekMap = new HashMap<String,Object>();
        weekMap.put("begin",thisWeekMonday);
        weekMap.put("end",thisWeekSunday);
        Integer thisWeekOrderNumber = orderDao.findOrderCountBetweenDate(weekMap);
        //8.本月预约数 >= 4月1  and <= 4月31号
        Map<String,Object> monthMap = new HashMap<String,Object>();
        monthMap.put("begin",firstDay4ThisMonth);
        monthMap.put("end",lastDay4ThisMonth);
        Integer thisMonthOrderNumber = orderDao.findOrderCountBetweenDate(monthMap);
        //9.今日到诊数
        Integer todayVisitsNumber = orderDao.findVisitsCountByDate(reportDate);
        //10.本周到诊数
        Map<String,Object> weekMap2 = new HashMap<String,Object>();
        weekMap2.put("begin",thisWeekMonday);
        weekMap2.put("end",thisWeekSunday);
        Integer thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(weekMap2);
        //11.本月到诊数
        Map<String,Object> monthMap2 = new HashMap<String,Object>();
        monthMap2.put("begin",firstDay4ThisMonth);
        monthMap2.put("end",lastDay4ThisMonth);
        Integer thisMonthVisitsNumber = orderDao.findVisitsCountAfterDate(monthMap2);

        //12.获取热门套餐
        List<Map> hotSetmeal = orderDao.findHotSetmeal();
        //定义一个返回结果
        Map<String,Object> rsMap = new HashMap<>();
        rsMap.put("reportDate",reportDate);
        rsMap.put("todayNewMember",todayNewMember);
        rsMap.put("totalMember",totalMember);
        rsMap.put("thisWeekNewMember",thisWeekNewMember);
        rsMap.put("thisMonthNewMember",thisMonthNewMember);
        rsMap.put("todayOrderNumber",todayOrderNumber);
        rsMap.put("todayVisitsNumber",todayVisitsNumber);
        rsMap.put("thisWeekOrderNumber",thisWeekOrderNumber);
        rsMap.put("thisWeekVisitsNumber",thisWeekVisitsNumber);
        rsMap.put("thisMonthOrderNumber",thisMonthOrderNumber);
        rsMap.put("thisMonthVisitsNumber",thisMonthVisitsNumber);
        rsMap.put("hotSetmeal",hotSetmeal);
        return rsMap;
    }
}

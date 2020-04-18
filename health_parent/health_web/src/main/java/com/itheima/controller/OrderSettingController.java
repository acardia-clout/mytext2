package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import com.itheima.utils.POIUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约设置
 *  批量预约设置（为了提高用户效率）
 *  单个预约设置
 */
@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {

    @Reference
    private OrderSettingService orderSettingService;

    /**
     * 批量导入预约设置
     */
    @RequestMapping("/upload")
    public Result upload(@RequestParam("excelFile")MultipartFile excelFile){

        try {
            System.out.println("************"+excelFile.getOriginalFilename());
            //1.获取Excel文件数据 （工具类调用获取List<String[]）
            List<String[]> list = POIUtils.readExcel(excelFile);//将excel数据解析成List<String[]>
            //2.将List<String[]>  转成 List<OrderSetting>
            if(list !=null && list.size()>0){
                List<OrderSetting> listOrderSetting = new ArrayList<>();
                //str:每一行数据（预约日期+可预约人数）
                for (String[] str : list) {
                    OrderSetting orderSetting = new OrderSetting();
                    orderSetting.setOrderDate(new Date(str[0]));//预约日期
                    orderSetting.setNumber(Integer.parseInt(str[1]));//可预约人数
                    orderSetting.setReservations(0);//已经预约人数 默认0
                    listOrderSetting.add(orderSetting);
                }
                //3.调用服务保存预约设置数据
                orderSettingService.add(listOrderSetting);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new Result(true, MessageConstant.UPLOAD_SUCCESS);
    }

    /**
     * 获取年月对应预约设置数据
     */
    @RequestMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String date){

        try {
            List<Map> list = orderSettingService.getOrderSettingByMonth(date);
            return new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS,list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_ORDERSETTING_FAIL);
        }
    }

    /**
     * 单个预约设置
     */
    @RequestMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){

        try {
            orderSettingService.editNumberByDate(orderSetting);
            return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDERSETTING_FAIL);
        }
    }

}

package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.CheckItem;
import com.itheima.pojo.Order;
import com.itheima.pojo.Setmeal;
import com.itheima.service.OrderMobileService;
import com.itheima.service.SetmealService;
import com.itheima.utils.SMSUtils;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * 体检预约控制层
 */
@RestController
@RequestMapping("/order")
public class OrderMobileController {
    //
//    @Autowired
//    private JedisPool jedisPool;
    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private OrderMobileService orderMobileService;

    @Reference
    private SetmealService setmealService;

    /**
     * 体检预约
     */
    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map) {
        //1.验证用户输入 的验证码和 Redis的验证码 是否一致
        String validateCode = (String) map.get("validateCode");
        String telephone = (String) map.get("telephone");
        String redisCode = (String)redisTemplate.opsForValue().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        //String redisCode = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        if (StringUtils.isEmpty(validateCode) || StringUtils.isEmpty(redisCode) || !validateCode.equals(redisCode)) {
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        //2.调用体检预约业务逻辑
        Result result = null;
        try {
            //预约类型
            map.put("orderType", Order.ORDERTYPE_WEIXIN);
            result = orderMobileService.submitOrder(map);
        } catch (Exception e) {
            e.printStackTrace();
            //返回false e.getMessage()  可以定义异常信息 系统异常，请联系管理员
            return new Result(false, MessageConstant.ORDER_FAIL, e.getMessage());
        }
        //3.根据成功结果发送短信给用户
        if (result != null && result.isFlag()) {
            String orderDate = (String) map.get("orderDate");
            try {
                if (false) {
                    SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE, telephone, orderDate);
                }
                System.out.println("发送短信通知成功了。。。");
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
        //4.返回结果给页面
        return result;
    }

    /**
     * 预约成功页面展示
     */
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        try {
            Map map = orderMobileService.findById(id);
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS, map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
        }
    }


    /**
     * 导出成功预约信息PDF
     */
    @RequestMapping("/exportSetmealInfo")
    public Result exportSetmealInfo(Integer id, HttpServletRequest request, HttpServletResponse response) {
        try {
            //1.获取预约数据+检查组 检查项
            Map map = orderMobileService.findById(id);
            Integer setmealId = (Integer) map.get("setmealId");//套餐id
            //根据套餐id查询检查组检查项数据
            Setmeal setmeal = setmealService.findById(setmealId);

            //设置以文件附件形式导出
            response.setHeader("content-Disposition", "attachment;filename=exportPDF.pdf");

            //2.使用IText创建PDF
            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();


            // 设置表格字体(后续jasperReport就不需要通过代码设置字体样式等了-----可以通过工具来设计模板)
            BaseFont cn = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
            Font font = new Font(cn, 10, Font.NORMAL, Color.BLUE);
            //设置预约成功数据
            document.add(new Paragraph("体检人：" + (String) map.get("member"), font));
            document.add(new Paragraph("体检套餐：" + (String) map.get("setmeal"), font));
            document.add(new Paragraph("体检日期：" + (String) map.get("orderDate").toString(), font));
            document.add(new Paragraph("预约类型：" + (String) map.get("orderType"), font));

            //检查组 检查项数据
            // 向document 生成pdf表格
            // 向document 生成pdf表格
            Table table = new Table(3);//创建3列的表格
            table.setWidth(80); // 宽度
            table.setBorder(1); // 边框
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); //水平对齐方式
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP); // 垂直对齐方式
            /*设置表格属性*/
            table.setBorderColor(new Color(0, 0, 255)); //将边框的颜色设置为蓝色
            table.setPadding(5);//设置表格与字体间的间距
            //table.setSpacing(5);//设置表格上下的间距
            table.setAlignment(Element.ALIGN_CENTER);//设置字体显示居中样式

            // 写表头
            table.addCell(buildCell("项目名称", font));
            table.addCell(buildCell("项目内容", font));
            table.addCell(buildCell("项目解读", font));

            //写数据
            List<CheckGroup> checkGroups = setmeal.getCheckGroups();//获取检查组数据
            if (checkGroups != null && checkGroups.size() > 0) {
                for (CheckGroup checkGroup : checkGroups) {
                    //往pdf中table中添加数据
                    table.addCell(buildCell(checkGroup.getName(), font));
                    //将所有的检查项名称 拼接
                    StringBuffer stringBuffer = new StringBuffer();
                    List<CheckItem> checkItems = checkGroup.getCheckItems();
                    if (checkItems != null && checkItems.size() > 0) {
                        for (CheckItem checkItem : checkItems) {
                            stringBuffer.append(checkItem.getName() + " ");
                        }
                    }
                    //设置表格中的检查项
                    table.addCell(buildCell(stringBuffer.toString(), font));
                    table.addCell(buildCell(checkGroup.getRemark(), font));
                }
            }
            //将Table表格加入到document文档中
            document.add(table);
            document.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
        }
    }

    // 传递内容和字体样式，生成单元格
    private Cell buildCell(String content, Font font)
            throws BadElementException {
        Phrase phrase = new Phrase(content, font);
        return new Cell(phrase);
    }

}

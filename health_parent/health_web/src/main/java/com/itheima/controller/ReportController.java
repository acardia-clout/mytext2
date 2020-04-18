package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import com.itheima.service.ReportService;
import com.itheima.service.SetmealService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 报表模块
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;

    /**
     * 获取会员数量折线图
     */
    @RequestMapping("/getMemberReport")
    public Result getMemberReport() {
        try {
            //最终返回结果
            Map<String, Object> rsMap = new HashMap<>();
            //获取当前时间
            Calendar calendar = Calendar.getInstance();
            //获取当前日期之前的12个月的日期
            calendar.add(Calendar.MONTH, -12);
            //定义年月List<String>
            List<String> months = new ArrayList<>();

            for (int i = 0; i < 12; i++) {
                calendar.add(Calendar.MONTH, 1);
                months.add(new SimpleDateFormat("yyyy-MM").format(calendar.getTime()));
            }
            //年月List<String>
            rsMap.put("months", months);
            //根据年月获取累计总会员数量
            List<Integer> memberCount = memberService.findMemberCountByMonth(months);
            //每一个年月对应的会员数量
            rsMap.put("memberCount", memberCount);
            return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, rsMap);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL);
        }
    }

    /**
     * 套餐预约占比饼图
     */
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport() {
        try {

            //定义一个Map返回结果
            Map rsMap = new HashMap();
            // “setmealNames”:[“套餐1”……]
            // “setmealCount”: [{name:”套餐1”,value:10},{….}]
            List<Map<String, Object>> setmealCount = setmealService.getSetmealCount();
            rsMap.put("setmealCount", setmealCount);//套餐名称和套餐预约数量

            //定义一个List<String>返回套餐名称列表
            List<String> setmealNames = new ArrayList<>();
            //从setmealCount获取出套餐列表返回页面
            if (setmealCount != null && setmealCount.size() > 0) {
                for (Map<String, Object> m : setmealCount) {
                    String name = (String) m.get("name");///套餐名称
                    setmealNames.add(name);
                }
            }
            rsMap.put("setmealNames", setmealNames);//套餐名称列表
            return new Result(true, MessageConstant.GET_SETMEAL_REPORT_SUCCESS, rsMap);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_SETMEAL_REPORT_FAIL);
        }
    }

    /**
     * 运营数据统计报表
     */
    @RequestMapping(value = "/getBusinessReportData")
    public Result getBusinessReportData() {
        try {
            Map<String, Object> rsMap = reportService.getBusinessReportData();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, rsMap);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }


    /**
     * （方式一）导出运营数据统计报表
     */
    /*@RequestMapping(value = "/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response) {
        try {
            //1.获取excel数据
            Map<String, Object> rsMap = reportService.getBusinessReportData();
            //获取日期
            String reportDate = (String)rsMap.get("reportDate");
            Integer todayNewMember = (Integer) rsMap.get("todayNewMember");
            Integer totalMember = (Integer) rsMap.get("totalMember");
            Integer thisWeekNewMember = (Integer) rsMap.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) rsMap.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) rsMap.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) rsMap.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) rsMap.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) rsMap.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) rsMap.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) rsMap.get("thisMonthVisitsNumber");

            //获取热门套餐
            List<Map> hotSetmeal = (List<Map>)rsMap.get("hotSetmeal");

            //2.获取Excel模板  template\report_template.xlsx
            String templateRealPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "report_template.xlsx";
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(new File(templateRealPath)));
            //获取第一个工作表sheet
            XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
            //3.为模板填充数据
            //填充会员数据
            XSSFRow row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);//日期

            //填充预约数据

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数（本日）
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数


            //填充热门套餐数据
            int rowNum = 12;//热门套餐从13行开始填充
            for (Map map : hotSetmeal) {
                //获取数据
                String name = (String)map.get("name");
                Long setmeal_count = (Long)map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal)map.get("proportion");
                String remark = (String)map.get("remark");

                //map每一行数据
                row = sheet.getRow(rowNum);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
                row.getCell(7).setCellValue(remark);//套餐名称
                rowNum++;
            }



            //4通过输出流方式下载本地磁盘
            ServletOutputStream outputStream = response.getOutputStream();
            //设置内容类型
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            //设置头信息  filename;文件名称
            response.setHeader("content-Disposition","attachment;filename=report.xlsx");
            //通过poi的write方法写入输出流
            xssfWorkbook.write(outputStream);
            //5.释放资源
            outputStream.flush();
            outputStream.close();
            xssfWorkbook.close();


            //如果成功 没有必要再返回result对象了 （因为数据是输出流的形式返回的）
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }*/


    /**
     * （方式二）导出运营数据统计报表
     * jxl ：针对poi技术诞生，模板技术
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response) {
        try {
            //1.获取excel数据
            Map<String, Object> rsMap = reportService.getBusinessReportData();
            //2.获取Excel模板  template\report_template.xlsx
            String templateRealPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "report_template.xlsx";
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(new File(templateRealPath)));
            //jxl填充模板数据
            XLSTransformer transformer = new XLSTransformer();
            transformer.transformWorkbook(xssfWorkbook, rsMap);
            //4通过输出流方式下载本地磁盘
            ServletOutputStream outputStream = response.getOutputStream();
            //设置内容类型
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            //设置头信息  filename;文件名称
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");
            //通过poi的write方法写入输出流
            xssfWorkbook.write(outputStream);
            //5.释放资源
            outputStream.flush();
            outputStream.close();
            xssfWorkbook.close();
            //如果成功 没有必要再返回result对象了 （因为数据是输出流的形式返回的）
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    /**
     * 导出PDF运营数据统计报表
     */
    @RequestMapping(value = "/exportBusinessReportPDF")
    public Result exportBusinessReportPDF(HttpServletRequest request, HttpServletResponse response) {
        try {
            //1.获取excel数据
            Map<String, Object> rsMap = reportService.getBusinessReportData();

            List<Map> hotSetmeal = (List<Map>)rsMap.get("hotSetmeal");

            //获取模板路径
            String jrxmlPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jrxml";

            //指定编译后的模板路径
            String jasperPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jasper";

            //2.编译模板
            JasperCompileManager.compileReportToFile(jrxmlPath,jasperPath);

            // 填充模板中的参数数据 +  套餐列表数据javaBean
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, rsMap, new JRBeanCollectionDataSource(hotSetmeal));

            //输出流方式导出浏览器下载本地

            ServletOutputStream outputStream = response.getOutputStream();
            //设置文件类型 以及文件名称
            //设置内容类型
            response.setContentType("application/pdf");
            //设置头信息  filename;文件名称
            response.setHeader("content-Disposition", "attachment;filename=report.pdf");
            //JasperExportManager.exportReportToPdfFile("c://xxxx.pdf")
            //JasperPrint OutPutStream
            JasperExportManager.exportReportToPdfStream(jasperPrint,outputStream);

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }
}

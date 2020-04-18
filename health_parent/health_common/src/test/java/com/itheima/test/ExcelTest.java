package com.itheima.test;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * POI入门案例
 */
public class ExcelTest {
    /**
     * 方式一：读取Excel
     */
    //@Test
    public void readExcel01() throws IOException {
        //1.获取Excel对象（工作薄）
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook("C:\\Users\\Administrator\\Desktop\\read.xlsx");
        //2.获取工作表
        XSSFSheet sheetAt = xssfWorkbook.getSheetAt(0);//从0开始
      //3.获取行
        for (Row row : sheetAt) {
            //遍历获取行
            for (Cell cell : row) {
                //遍历获取列
                //4.获取列 输出
                System.out.println(cell.getStringCellValue());
            }
            System.out.println("***************************************");
        }
        //释放资源
        xssfWorkbook.close();
    }

    /**
     * 方式二：读取Excel
     */
    //@Test
    public void readExcel02() throws IOException {
        //1.获取Excel对象（工作薄）
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook("C:\\Users\\Administrator\\Desktop\\read.xlsx");
        //2.获取工作表
        XSSFSheet sheetAt = xssfWorkbook.getSheetAt(0);//从0开始
        //3.获取sheet最后一行的行号
        int lastRowNum = sheetAt.getLastRowNum();
        System.out.println("最后一行的行号"+lastRowNum);
        //4.指定从哪行开始读取数据
        for (int i=1;i<=lastRowNum;i++){
            XSSFRow row = sheetAt.getRow(i);
            //获取最后一列列号
            short lastCellNum = row.getLastCellNum();
            System.out.println("最后一列的列号"+lastCellNum);
            for (short j = 0;j<lastCellNum;j++){
                System.out.println(row.getCell(j).getStringCellValue());
            }
            System.out.println("*****************************************");
        }
        xssfWorkbook.close();

    }

    /**
     * 创建excel
     */
    //@Test
    public void CreateExcel() throws IOException {
        //1.创建Excel对象
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = xssfWorkbook.createSheet("传智播客");
        //3.创建标题行
        XSSFRow titleRow = sheet.createRow(0);
        //4.创建标题的列
        titleRow.createCell(0).setCellValue("编号");
        titleRow.createCell(1).setCellValue("姓名");
        titleRow.createCell(2).setCellValue("年龄");

        //数据库中查询（list<T>） for
        //3.创建标题行
        XSSFRow dataRow = sheet.createRow(1);
        //4.创建标题的列
        dataRow.createCell(0).setCellValue("1");
        dataRow.createCell(1).setCellValue("老王");
        dataRow.createCell(2).setCellValue("18");
        //5.通过输出流方式将workbook下载磁盘中
        OutputStream outputStream = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\create.xlsx");
        xssfWorkbook.write(outputStream);
        //6.资源释放
        outputStream.flush();//刷新
        outputStream.close();//关闭流
        xssfWorkbook.close();//资源资源

    }
}

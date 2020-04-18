package com.itheima.test;




import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * IText PDF测试
 */
public class ITextPdfTest {

    public static void main(String[] args) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document,new FileOutputStream("C:\\working\\itext.pdf"));
        document.open();
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        document.add(new Paragraph("测试黑马程序员",new Font(bfChinese)));
        document.close();
    }
}

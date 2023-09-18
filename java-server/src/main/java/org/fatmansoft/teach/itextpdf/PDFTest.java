package org.fatmansoft.teach.itextpdf;



import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;




public class PDFTest {
    public static void main(String args[]){
        try{
            Document document = new Document(PageSize.A4,30,30,40,40);
            try {
                // 中文文本
                BaseFont bfChinese= BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                Font subjectFont=new Font(bfChinese,20,Font.BOLD);
                Font titleFont=new Font(bfChinese,15,Font.BOLD);
                Font keyFont=new Font(bfChinese,10,Font.BOLD);
                Font textFont=new Font(bfChinese,10,Font.NORMAL);
                //获取桌面路径
                File desktopDir = FileSystemView.getFileSystemView() .getHomeDirectory();
                String desktopPath = desktopDir.getAbsolutePath();
                System.out.println("您的桌面路径："+desktopPath);
                String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+"_试卷.pdf";
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(desktopPath+"/"+fileName));
                System.out.println("您生成的pdf文件路径在："+desktopPath+"下，文件名为："+fileName);
                //添加水印和页码
                //如果要打印页面，则还需要 增加 PDFBuilder 工具类
                writer.setPageEvent( new PDFBuilder());
                document.open();


                Paragraph titlePara = new Paragraph("主标题",subjectFont);
                titlePara.setAlignment(Element.ALIGN_CENTER);
                document.add(new Paragraph(titlePara));


                //第一个小标题
                document.add(new Paragraph("机电",titleFont));
                document.add(new Paragraph("\r\n")); // 换行
                //生成表格内容
                PdfPTable table = createTable(writer, keyFont, textFont);
                document.add(table);


                //第二个小标题
                document.add(new Paragraph("BAS",titleFont));
                document.add(new Paragraph("\r\n")); // 换行
                //生成表格内容
                PdfPTable table2 = createTable(writer, keyFont, textFont);
                document.add(table2);
                System.out.println("生成pdf完成。。。。。。");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                document.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    public static PdfPTable createTable(PdfWriter writer, Font keyfont,Font textfont) throws DocumentException, IOException {
        PdfPTable table = new PdfPTable(1);//生成一个两列的表格
        PdfPCell cell;
        int size = 50;
        cell = new PdfPCell(new Paragraph("正义永远不会缺席",textfont));
        cell.setFixedHeight(size);//设置高度
        cell.setBorder(0);//表格无边框
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席正义永远不会缺席",textfont));
        cell.setFixedHeight(size);
        table.addCell(cell);
        cell.setBorder(0);
        cell = new PdfPCell(new Paragraph("正义永远不会缺席",textfont));
        cell.setFixedHeight(size);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("正义永远不会缺席",textfont));
        cell.setFixedHeight(size);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("正义永远不会缺席",textfont));
        cell.setFixedHeight(size);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("正义永远不会缺席",textfont));
        cell.setFixedHeight(size);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("正义永远不会缺席",textfont));
        cell.setFixedHeight(size);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("正义永远不会缺席",textfont));
        cell.setFixedHeight(size);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("正义永远不会缺席",textfont));
        cell.setFixedHeight(size);
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("正义永远不会缺席",textfont));
        cell.setFixedHeight(size);
        table.addCell(cell);
        return table;
    }


}

package org.fatmansoft.teach.itextpdf;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* 生成PDF的过程 编辑Word 另存PDF 打开PDF 选择更多工具， 选择“准备表单”， 更改文件 点击开始 设置表单域名字 保存生成 PDF表单， 转换PDF生成

 */


public class TestPDF {
    public static void main(String args[]) throws Exception{
        generateTempPDF();
    }
    public static void generateTempPDF() throws Exception {
        PdfReader reader = null;
        PdfStamper ps = null;
        OutputStream fos = null;
        ByteArrayOutputStream bos = null;
        try {
            String fileName = "d:/test/form.pdf";//模板绝对路径
            reader = new PdfReader(fileName);
            bos = new ByteArrayOutputStream();
            ps = new PdfStamper(reader, bos);

            // 使用中文字体
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            ArrayList<BaseFont> fontList = new ArrayList<BaseFont>();
            fontList.add(bf);

            AcroFields fields = ps.getAcroFields();
            fields.setSubstitutionFonts(fontList);
            fillData(fields, data());//渲染

            //必须要调用这个，否则文档不会生成的
            ps.setFormFlattening(true);
            if(ps != null){
                ps.close();
            }
            //生成pdf路径存放的路径
            fos = new FileOutputStream("d:/test/formout.pdf");
            fos.write(bos.toByteArray());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(fos!=null){
                fos.flush();
                fos.close();
            }
            if (bos != null){
                bos.close();
            }
            if(reader != null){
                reader.close();
            }
        }
    }

    /**
     * 填充模板中的数据
     */
    public static void fillData(AcroFields fields, Map<String, String> data) {
        try {
            for (String key : data.keySet()) {
                String value = data.get(key);
                // 为字段赋值,注意字段名称是区分大小写的
                fields.setField(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 填充数据源
     * 其中data存放的key值与pdf模板中的文本域值相对应
     */
    public static Map<String, String> data() {
        Map<String, String> data = new HashMap<String, String>();
        //key要与模板中的别名一一对应
        data.put("num", "2001");
        data.put("name", "张三");
        data.put("gender", "男");
        data.put("dept", "软件学院");
        return data;
    }

}

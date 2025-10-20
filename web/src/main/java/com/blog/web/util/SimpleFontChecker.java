package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;

public class SimpleFontChecker {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== 字体嵌入测试 ===");
            
            // 测试不同字体的嵌入情况
            testFont("STSong-Light", "UniGB-UCS2-H");
            testFont("Helvetica", BaseFont.CP1252);
            
            // 如果有字体文件，测试文件字体
            testFontFile();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testFont(String fontName, String encoding) {
        System.out.println("\n--- 测试字体: " + fontName + " ---");
        
        try {
            // 测试不嵌入
            BaseFont font1 = BaseFont.createFont(fontName, encoding, BaseFont.NOT_EMBEDDED);
            String pdf1 = "test_" + fontName.replace("-", "_") + "_not_embedded.pdf";
            createPdf(pdf1, font1, "不嵌入");
            
            // 测试嵌入
            BaseFont font2 = BaseFont.createFont(fontName, encoding, BaseFont.EMBEDDED);
            String pdf2 = "test_" + fontName.replace("-", "_") + "_embedded.pdf";
            createPdf(pdf2, font2, "嵌入");
            
            // 比较文件大小
            File f1 = new File(pdf1);
            File f2 = new File(pdf2);
            
            System.out.println("不嵌入文件大小: " + f1.length() + " bytes");
            System.out.println("嵌入文件大小: " + f2.length() + " bytes");
            System.out.println("差异: " + (f2.length() - f1.length()) + " bytes");
            
            if (f2.length() > f1.length() + 1000) {
                System.out.println("✅ 字体成功嵌入！");
            } else {
                System.out.println("❌ 字体可能没有嵌入");
            }
            
        } catch (Exception e) {
            System.out.println("字体测试失败: " + e.getMessage());
        }
    }
    
    private static void testFontFile() {
        System.out.println("\n--- 测试字体文件 ---");
        
        // 检查常见字体文件位置
        String[] fontPaths = {
            "src/main/resources/fonts/NotoSerifCJK-Regular.ttc",
            "src/main/resources/fonts/SimSun.ttf",
            "/System/Library/Fonts/STSong-Light.ttc",
            "/System/Library/Fonts/Helvetica.ttc"
        };
        
        for (String fontPath : fontPaths) {
            File fontFile = new File(fontPath);
            if (fontFile.exists()) {
                System.out.println("找到字体文件: " + fontPath);
                try {
                    BaseFont font = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    String pdfName = "test_file_font_" + fontFile.getName().replace(".", "_") + ".pdf";
                    createPdf(pdfName, font, "文件字体嵌入");
                    
                    File pdfFile = new File(pdfName);
                    System.out.println("文件字体PDF大小: " + pdfFile.length() + " bytes");
                    
                } catch (Exception e) {
                    System.out.println("文件字体测试失败: " + e.getMessage());
                }
            }
        }
    }
    
    private static void createPdf(String filename, BaseFont baseFont, String description) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        
        Font font = new Font(baseFont, 12);
        document.add(new Paragraph("测试中文字体：你好世界！", font));
        document.add(new Paragraph("Test English Font: Hello World!", font));
        document.add(new Paragraph("字体描述: " + description, font));
        document.add(new Paragraph("字体名称: " + baseFont.getPostscriptFontName(), font));
        
        document.close();
    }
}
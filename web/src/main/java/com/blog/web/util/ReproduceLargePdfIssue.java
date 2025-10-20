package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;

public class ReproduceLargePdfIssue {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== 重现37MB PDF问题 ===");
            
            // 测试可能导致大文件的场景
            testScenarios();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testScenarios() {
        System.out.println("\n--- 测试可能导致37MB的场景 ---");
        
        // 场景1：错误地使用 EMBEDDED 参数
        testWrongEmbeddedUsage();
        
        // 场景2：使用字体文件路径 + EMBEDDED
        testFontFileWithEmbedded();
        
        // 场景3：多个字体实例
        testMultipleFontInstances();
        
        // 场景4：大量字符导致字体子集膨胀
        testMassiveCharacterSet();
    }
    
    private static void testWrongEmbeddedUsage() {
        System.out.println("\n1. 测试错误的EMBEDDED用法:");
        
        String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
        
        try {
            // 这可能是问题所在：使用字体文件 + EMBEDDED
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("wrong_embedded_usage.pdf"));
            document.open();
            
            Font font = new Font(baseFont, 12);
            document.add(new Paragraph("错误的嵌入用法测试", font));
            document.add(new Paragraph("这可能导致整个24MB字体文件被嵌入", font));
            
            // 添加一些中文内容
            document.add(new Paragraph("中文测试：你好世界！这是一个测试文档。", font));
            
            document.close();
            
            File file = new File("wrong_embedded_usage.pdf");
            long sizeInMB = file.length() / (1024 * 1024);
            System.out.println("   文件大小: " + file.length() + " bytes (" + sizeInMB + " MB)");
            
            if (sizeInMB > 10) {
                System.out.println("   ❌ 发现问题：文件过大！这就是37MB问题的原因");
                System.out.println("   原因：使用字体文件路径 + BaseFont.EMBEDDED 会嵌入整个字体");
                System.out.println("   解决方案：改为 BaseFont.NOT_EMBEDDED");
            } else {
                System.out.println("   ✅ 文件大小正常");
            }
            
        } catch (Exception e) {
            System.out.println("   测试失败: " + e.getMessage());
        }
    }
    
    private static void testFontFileWithEmbedded() {
        System.out.println("\n2. 测试字体文件路径 + EMBEDDED:");
        
        String[] fontFiles = {
            "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
            "src/main/resources/fonts/NotoSerifCJKsc-Bold.otf"
        };
        
        for (String fontPath : fontFiles) {
            try {
                File fontFile = new File(fontPath);
                if (!fontFile.exists()) {
                    System.out.println("   字体文件不存在: " + fontPath);
                    continue;
                }
                
                System.out.println("   测试字体: " + fontPath);
                System.out.println("   字体文件大小: " + (fontFile.length() / 1024 / 1024) + " MB");
                
                // 强制嵌入整个字体文件
                BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                
                Document document = new Document();
                String pdfName = "embedded_" + fontFile.getName().replace(".otf", ".pdf");
                PdfWriter.getInstance(document, new FileOutputStream(pdfName));
                document.open();
                
                Font font = new Font(baseFont, 12);
                document.add(new Paragraph("嵌入字体测试", font));
                document.add(new Paragraph("字体文件: " + fontPath, font));
                document.add(new Paragraph("测试中文：你好世界！", font));
                
                document.close();
                
                File pdfFile = new File(pdfName);
                long pdfSizeInMB = pdfFile.length() / (1024 * 1024);
                System.out.println("   PDF大小: " + pdfFile.length() + " bytes (" + pdfSizeInMB + " MB)");
                
                if (pdfSizeInMB > 20) {
                    System.out.println("   ❌ 确认问题：PDF文件巨大！");
                    System.out.println("   这就是你遇到的37MB问题");
                } else {
                    System.out.println("   ✅ PDF大小正常");
                }
                
            } catch (Exception e) {
                System.out.println("   测试失败: " + e.getMessage());
            }
        }
    }
    
    private static void testMultipleFontInstances() {
        System.out.println("\n3. 测试多个字体实例:");
        
        String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
        
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("multiple_font_instances.pdf"));
            document.open();
            
            // 错误做法：创建多个相同字体的实例
            for (int i = 0; i < 5; i++) {
                BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font font = new Font(baseFont, 12);
                document.add(new Paragraph("字体实例 " + (i+1) + "：测试文本", font));
            }
            
            document.close();
            
            File file = new File("multiple_font_instances.pdf");
            long sizeInMB = file.length() / (1024 * 1024);
            System.out.println("   文件大小: " + file.length() + " bytes (" + sizeInMB + " MB)");
            
            if (sizeInMB > 50) {
                System.out.println("   ❌ 发现问题：多次嵌入同一字体导致文件巨大");
            } else {
                System.out.println("   ✅ 文件大小正常（iText可能优化了重复字体）");
            }
            
        } catch (Exception e) {
            System.out.println("   测试失败: " + e.getMessage());
        }
    }
    
    private static void testMassiveCharacterSet() {
        System.out.println("\n4. 测试大量字符集:");
        
        String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
        
        try {
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("massive_charset.pdf"));
            document.open();
            
            Font font = new Font(baseFont, 12);
            
            // 使用大量不同的Unicode字符
            StringBuilder massiveText = new StringBuilder();
            
            // 添加大量中文字符
            for (int i = 0x4E00; i <= 0x9FFF && massiveText.length() < 10000; i++) {
                massiveText.append((char) i);
                if (massiveText.length() % 100 == 0) {
                    massiveText.append("\n");
                }
            }
            
            document.add(new Paragraph("大量字符测试:", font));
            document.add(new Paragraph(massiveText.toString(), font));
            
            document.close();
            
            File file = new File("massive_charset.pdf");
            long sizeInMB = file.length() / (1024 * 1024);
            System.out.println("   文件大小: " + file.length() + " bytes (" + sizeInMB + " MB)");
            
            if (sizeInMB > 20) {
                System.out.println("   ❌ 发现问题：大量字符导致字体子集过大");
            } else {
                System.out.println("   ✅ 文件大小正常");
            }
            
        } catch (Exception e) {
            System.out.println("   测试失败: " + e.getMessage());
        }
    }
}
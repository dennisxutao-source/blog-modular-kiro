package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;

public class NotoSerifNotEmbeddedTester {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Noto Serif CJK 不嵌入测试 ===");
            
            testNotoSerifNotEmbedded();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testNotoSerifNotEmbedded() {
        System.out.println("\n--- 测试 Noto Serif CJK（不嵌入） ---");
        
        String[] fontFiles = {
            "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
            "src/main/resources/fonts/NotoSerifCJKsc-Bold.otf",
            "src/main/resources/fonts/NotoSerifCJKsc-Light.otf"
        };
        
        String testText = "这是Noto Serif CJK测试：你好世界！\n" +
                         "This is Noto Serif CJK test: Hello World!\n" +
                         "数字测试：1234567890\n" +
                         "标点符号：，。！？；：\"\"''（）【】\n" +
                         "特殊字符：©®™€¥$£¢";
        
        for (String fontPath : fontFiles) {
            File fontFile = new File(fontPath);
            if (fontFile.exists()) {
                System.out.println("\n测试字体文件: " + fontPath);
                
                try {
                    // 方案1：使用字体文件但不嵌入
                    BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                    String pdfName = "noto_not_embedded_" + fontFile.getName().replace(".otf", ".pdf");
                    
                    createTestPdf(pdfName, baseFont, testText, fontPath, "NOT_EMBEDDED");
                    
                    File pdfFileResult = new File(pdfName);
                    System.out.println("✅ 成功创建不嵌入PDF: " + pdfName);
                    System.out.println("   文件大小: " + pdfFileResult.length() + " bytes");
                    System.out.println("   字体PostScript名称: " + baseFont.getPostscriptFontName());
                    
                    // 方案2：对比嵌入版本的大小
                    BaseFont embeddedFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    String embeddedPdfName = "noto_embedded_" + fontFile.getName().replace(".otf", ".pdf");
                    
                    createTestPdf(embeddedPdfName, embeddedFont, testText, fontPath, "EMBEDDED");
                    
                    File embeddedPdfFile = new File(embeddedPdfName);
                    System.out.println("✅ 对比嵌入PDF: " + embeddedPdfName);
                    System.out.println("   文件大小: " + embeddedPdfFile.length() + " bytes");
                    System.out.println("   大小差异: " + (embeddedPdfFile.length() - pdfFileResult.length()) + " bytes");
                    
                    if (embeddedPdfFile.length() > pdfFileResult.length() + 10000) {
                        System.out.println("   ✅ 确认：嵌入版本明显更大，不嵌入版本有效");
                    } else {
                        System.out.println("   ⚠️  警告：两个版本大小相近，可能都没有真正嵌入");
                    }
                    
                } catch (Exception e) {
                    System.out.println("❌ 字体测试失败: " + e.getMessage());
                }
            } else {
                System.out.println("字体文件不存在: " + fontPath);
            }
        }
        
        // 测试系统字体回退
        testSystemFontFallback();
    }
    
    private static void testSystemFontFallback() {
        System.out.println("\n--- 测试系统字体回退方案 ---");
        
        String[] systemFonts = {
            "PingFang SC",           // macOS 默认中文字体
            "Hiragino Sans GB",      // macOS 中文字体
            "Microsoft YaHei",       // Windows 中文字体
            "SimSun",               // Windows 宋体
            "Arial Unicode MS"       // 通用 Unicode 字体
        };
        
        String testText = "系统字体回退测试：中文显示效果\nSystem font fallback test";
        
        for (String fontName : systemFonts) {
            System.out.println("\n尝试系统字体: " + fontName);
            
            try {
                BaseFont baseFont = BaseFont.createFont(fontName, "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                String pdfName = "system_font_" + fontName.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
                
                createTestPdf(pdfName, baseFont, testText, fontName, "系统字体");
                
                File pdfFile = new File(pdfName);
                System.out.println("✅ 成功创建PDF: " + pdfName);
                System.out.println("   文件大小: " + pdfFile.length() + " bytes");
                System.out.println("   字体PostScript名称: " + baseFont.getPostscriptFontName());
                
            } catch (Exception e) {
                System.out.println("❌ 系统字体失败: " + e.getMessage());
            }
        }
    }
    
    private static void createTestPdf(String filename, BaseFont baseFont, String text, String fontInfo, String embedType) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        
        Font font = new Font(baseFont, 12);
        Font titleFont = new Font(baseFont, 16, Font.BOLD);
        
        document.add(new Paragraph("字体测试文档", titleFont));
        document.add(new Paragraph("Font Test Document", titleFont));
        document.add(new Paragraph(" "));
        
        document.add(new Paragraph("字体信息: " + fontInfo, font));
        document.add(new Paragraph("字体PostScript名称: " + baseFont.getPostscriptFontName(), font));
        document.add(new Paragraph("嵌入类型: " + embedType, font));
        document.add(new Paragraph(" "));
        
        document.add(new Paragraph("测试内容:", font));
        document.add(new Paragraph(text, font));
        
        document.close();
    }
}
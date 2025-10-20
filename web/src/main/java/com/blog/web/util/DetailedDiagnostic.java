package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;

/**
 * 详细诊断工具 - 请将这个代码集成到你的实际PDF生成代码中
 */
public class DetailedDiagnostic {
    
    public static BaseFont createDiagnosticFont(String fontPath) {
        try {
            System.out.println("\n=== 详细字体诊断 ===");
            System.out.println("请求的字体路径: " + fontPath);
            System.out.println("当前工作目录: " + System.getProperty("user.dir"));
            System.out.println("Java版本: " + System.getProperty("java.version"));
            System.out.println("操作系统: " + System.getProperty("os.name"));
            
            // 检查字体文件
            File fontFile = new File(fontPath);
            System.out.println("字体文件存在: " + fontFile.exists());
            if (fontFile.exists()) {
                System.out.println("字体文件大小: " + (fontFile.length() / 1024 / 1024) + " MB");
                System.out.println("字体文件绝对路径: " + fontFile.getAbsolutePath());
            }
            
            // 创建字体
            BaseFont font = BaseFont.createFont(
                fontPath, 
                BaseFont.IDENTITY_H, 
                BaseFont.NOT_EMBEDDED
            );
            
            System.out.println("✅ 字体创建成功");
            System.out.println("字体PostScript名称: " + font.getPostscriptFontName());
            System.out.println("字体全名: " + font.getFullFontName());
            System.out.println("字体族名: " + font.getFamilyFontName());
            System.out.println("==================");
            
            return font;
            
        } catch (Exception e) {
            System.out.println("❌ 字体创建失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("字体创建失败", e);
        }
    }
    
    public static void createDiagnosticPdf(String outputPath, String content) {
        try {
            System.out.println("\n=== PDF创建诊断 ===");
            System.out.println("输出路径: " + outputPath);
            
            // 创建字体
            BaseFont baseFont = createDiagnosticFont("src/main/resources/fonts/NotoSerifCJKsc-Regular.otf");
            
            // 创建PDF
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            
            System.out.println("PDF Writer创建成功");
            
            document.open();
            System.out.println("PDF文档打开成功");
            
            Font font = new Font(baseFont, 12);
            
            // 添加诊断信息
            document.add(new Paragraph("=== PDF诊断信息 ===", font));
            document.add(new Paragraph("字体PostScript名称: " + baseFont.getPostscriptFontName(), font));
            document.add(new Paragraph("创建时间: " + new java.util.Date(), font));
            document.add(new Paragraph("Java版本: " + System.getProperty("java.version"), font));
            document.add(new Paragraph(" ", font));
            
            // 添加实际内容
            if (content != null && !content.isEmpty()) {
                document.add(new Paragraph("实际内容:", font));
                document.add(new Paragraph(content, font));
                System.out.println("内容长度: " + content.length() + " 字符");
            }
            
            document.close();
            System.out.println("PDF文档关闭成功");
            
            // 检查文件大小
            File pdfFile = new File(outputPath);
            long sizeBytes = pdfFile.length();
            long sizeKB = sizeBytes / 1024;
            long sizeMB = sizeBytes / (1024 * 1024);
            
            System.out.println("\n=== PDF大小诊断 ===");
            System.out.println("PDF文件大小: " + sizeBytes + " bytes");
            System.out.println("PDF文件大小: " + sizeKB + " KB");
            System.out.println("PDF文件大小: " + sizeMB + " MB");
            
            if (sizeMB > 10) {
                System.out.println("❌ 发现问题：PDF文件过大 (" + sizeMB + " MB)");
                System.out.println("这确实重现了37MB问题！");
                
                // 进一步分析
                analyzeWhyLarge(outputPath, content);
                
            } else if (sizeKB > 500) {
                System.out.println("⚠️  PDF文件较大但可接受 (" + sizeKB + " KB)");
            } else {
                System.out.println("✅ PDF文件大小正常 (" + sizeKB + " KB)");
            }
            
            System.out.println("==================");
            
        } catch (Exception e) {
            System.out.println("❌ PDF创建失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void analyzeWhyLarge(String pdfPath, String content) {
        System.out.println("\n=== 大文件原因分析 ===");
        
        try {
            // 分析内容
            if (content != null) {
                System.out.println("内容分析:");
                System.out.println("  内容长度: " + content.length() + " 字符");
                System.out.println("  内容字节数: " + content.getBytes("UTF-8").length + " bytes");
                
                // 统计不同字符
                java.util.Set<Character> uniqueChars = new java.util.HashSet<>();
                for (char c : content.toCharArray()) {
                    uniqueChars.add(c);
                }
                System.out.println("  唯一字符数: " + uniqueChars.size());
                
                if (uniqueChars.size() > 1000) {
                    System.out.println("  ❌ 可能原因：大量不同字符导致字体子集过大");
                }
            }
            
            // 分析PDF文件
            File pdfFile = new File(pdfPath);
            System.out.println("PDF文件分析:");
            System.out.println("  文件路径: " + pdfFile.getAbsolutePath());
            System.out.println("  文件大小: " + (pdfFile.length() / 1024 / 1024) + " MB");
            
        } catch (Exception e) {
            System.out.println("分析失败: " + e.getMessage());
        }
    }
    
    // 示例用法
    public static void main(String[] args) {
        // 使用示例
        String testContent = "测试内容：你好世界！测韦欣 eg.10004730";
        createDiagnosticPdf("diagnostic_test.pdf", testContent);
    }
}
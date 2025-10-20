package com.blog.web.util;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * PDF文件大小诊断工具
 * 帮助找出为什么PDF文件这么大
 */
public class PdfSizeDiagnostic {
    
    /**
     * 诊断PDF文件大小问题
     */
    public static void diagnosePdfSizeIssue(String templatePath, String outputPath) {
        System.out.println("=== PDF文件大小诊断开始 ===");
        
        try {
            // 1. 检查原始模板大小
            File templateFile = new File(templatePath);
            if (templateFile.exists()) {
                long templateSize = templateFile.length();
                System.out.println("原始模板大小: " + formatSize(templateSize));
            } else {
                System.out.println("❌ 模板文件不存在: " + templatePath);
                return;
            }
            
            // 2. 测试最简单的处理
            testMinimalProcessing(templatePath, outputPath + "_minimal.pdf");
            
            // 3. 测试不同的字体方案
            testDifferentFonts(templatePath, outputPath);
            
            // 4. 测试压缩设置
            testCompressionSettings(templatePath, outputPath);
            
            // 5. 分析PDF内容
            analyzePdfContent(templatePath);
            
        } catch (Exception e) {
            System.out.println("诊断过程出错: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== PDF文件大小诊断结束 ===");
    }
    
    /**
     * 测试最简单的处理 - 只复制不做任何修改
     */
    private static void testMinimalProcessing(String templatePath, String outputPath) {
        System.out.println("\n--- 测试1: 最简单处理 ---");
        
        try {
            PdfReader reader = new PdfReader(templatePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, baos);
            
            // 不做任何修改，直接关闭
            stamper.close();
            reader.close();
            
            // 写入文件
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(baos.toByteArray());
            }
            
            File outputFile = new File(outputPath);
            System.out.println("最简单处理结果: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("最简单处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试不同字体方案
     */
    private static void testDifferentFonts(String templatePath, String outputPath) {
        System.out.println("\n--- 测试2: 不同字体方案 ---");
        
        Map<String, String> testData = new HashMap<>();
        testData.put("testField", "测试文字");
        
        // 方案1: 不使用任何字体
        testFontOption(templatePath, outputPath + "_no_font.pdf", null, "不使用字体");
        
        // 方案2: 系统字体不嵌入
        try {
            BaseFont font1 = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            testFontOption(templatePath, outputPath + "_helvetica.pdf", font1, "Helvetica不嵌入");
        } catch (Exception e) {
            System.out.println("Helvetica字体测试失败: " + e.getMessage());
        }
        
        // 方案3: 中文系统字体不嵌入
        try {
            BaseFont font2 = BaseFont.createFont("SimSun", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            testFontOption(templatePath, outputPath + "_simsun.pdf", font2, "SimSun不嵌入");
        } catch (Exception e) {
            System.out.println("SimSun字体测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试单个字体选项
     */
    private static void testFontOption(String templatePath, String outputPath, BaseFont font, String description) {
        try {
            PdfReader reader = new PdfReader(templatePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, baos);
            
            // 启用压缩
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 如果有字体，设置一个测试字段
            if (font != null && form.getFields().size() > 0) {
                String firstField = form.getFields().keySet().iterator().next();
                form.setFieldProperty(firstField, "textfont", font, null);
                form.setField(firstField, "测试");
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            // 写入文件
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(baos.toByteArray());
            }
            
            File outputFile = new File(outputPath);
            System.out.println(description + ": " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println(description + " 测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试压缩设置
     */
    private static void testCompressionSettings(String templatePath, String outputPath) {
        System.out.println("\n--- 测试3: 压缩设置 ---");
        
        // 测试1: 无压缩
        testCompression(templatePath, outputPath + "_no_compression.pdf", false, "无压缩");
        
        // 测试2: 基本压缩
        testCompression(templatePath, outputPath + "_basic_compression.pdf", true, "基本压缩");
    }
    
    /**
     * 测试压缩选项
     */
    private static void testCompression(String templatePath, String outputPath, boolean enableCompression, String description) {
        try {
            PdfReader reader = new PdfReader(templatePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, baos);
            
            if (enableCompression) {
                PdfWriter writer = stamper.getWriter();
                writer.setCompressionLevel(9);
                writer.setPdfVersion(PdfWriter.VERSION_1_5);
                stamper.setFullCompression();
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            // 写入文件
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(baos.toByteArray());
            }
            
            File outputFile = new File(outputPath);
            System.out.println(description + ": " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println(description + " 测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 分析PDF内容
     */
    private static void analyzePdfContent(String templatePath) {
        System.out.println("\n--- 测试4: PDF内容分析 ---");
        
        try {
            PdfReader reader = new PdfReader(templatePath);
            
            System.out.println("PDF版本: " + reader.getPdfVersion());
            System.out.println("页数: " + reader.getNumberOfPages());
            System.out.println("是否加密: " + reader.isEncrypted());
            
            // 检查表单字段
            AcroFields fields = reader.getAcroFields();
            if (fields != null) {
                System.out.println("表单字段数量: " + fields.getFields().size());
                if (fields.getFields().size() > 0) {
                    System.out.println("前5个字段名:");
                    int count = 0;
                    for (String fieldName : fields.getFields().keySet()) {
                        System.out.println("  - " + fieldName);
                        if (++count >= 5) break;
                    }
                }
            }
            
            // 检查每页大小
            for (int i = 1; i <= Math.min(reader.getNumberOfPages(), 3); i++) {
                byte[] pageContent = reader.getPageContent(i);
                if (pageContent != null) {
                    System.out.println("第" + i + "页内容大小: " + formatSize(pageContent.length));
                }
            }
            
            reader.close();
            
        } catch (Exception e) {
            System.out.println("PDF内容分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 格式化文件大小
     */
    private static String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        }
    }
    
    /**
     * 主测试方法
     */
    public static void main(String[] args) {
        // 使用示例
        String templatePath = "template.pdf";  // 替换为你的模板路径
        String outputPath = "diagnostic_output";
        
        diagnosePdfSizeIssue(templatePath, outputPath);
    }
    
    /**
     * 快速诊断方法 - 只检查关键信息
     */
    public static void quickDiagnosis(String templatePath) {
        System.out.println("=== 快速诊断 ===");
        
        try {
            File templateFile = new File(templatePath);
            System.out.println("模板文件大小: " + formatSize(templateFile.length()));
            
            PdfReader reader = new PdfReader(templatePath);
            System.out.println("PDF页数: " + reader.getNumberOfPages());
            
            AcroFields fields = reader.getAcroFields();
            if (fields != null) {
                System.out.println("表单字段数: " + fields.getFields().size());
            }
            
            reader.close();
            
        } catch (Exception e) {
            System.out.println("快速诊断失败: " + e.getMessage());
        }
    }
}
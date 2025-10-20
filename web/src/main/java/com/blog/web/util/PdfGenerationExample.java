package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * PDF生成示例
 * 展示如何正确使用压缩功能创建PDF
 */
public class PdfGenerationExample {
    
    /**
     * 创建一个简单的压缩PDF文档
     */
    public static byte[] createSimpleCompressedPdf(Map<String, String> data) 
            throws DocumentException, IOException {
        
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // 创建PdfWriter并启用压缩
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setCompressionLevel(9); // 最高压缩级别
        writer.setPdfVersion(PdfWriter.VERSION_1_5);
        writer.setFullCompression();
        
        document.open();
        
        // 使用压缩字体
        BaseFont baseFont = EffectivePdfCompressor.createSubsetFont();
        Font font = new Font(baseFont, 12, Font.NORMAL);
        
        // 添加内容
        document.add(new Paragraph("PDF压缩测试文档", font));
        document.add(new Paragraph(" ", font)); // 空行
        
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String line = entry.getKey() + ": " + entry.getValue();
            document.add(new Paragraph(line, font));
        }
        
        document.close();
        
        System.out.println("生成压缩PDF完成，大小: " + formatSize(baos.size()));
        return baos.toByteArray();
    }
    
    /**
     * 创建最小文件大小的PDF文档
     */
    public static byte[] createMinimalPdf(Map<String, String> data) 
            throws DocumentException, IOException {
        
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setCompressionLevel(9); // 最高压缩级别
        writer.setPdfVersion(PdfWriter.VERSION_1_5);
        writer.setFullCompression();
        
        document.open();
        
        // 使用不嵌入字体
        BaseFont baseFont = EffectivePdfCompressor.createNotEmbeddedFont();
        Font font = new Font(baseFont, 12, Font.NORMAL);
        
        document.add(new Paragraph("最小PDF测试文档", font));
        document.add(new Paragraph(" ", font));
        
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String line = entry.getKey() + ": " + entry.getValue();
            document.add(new Paragraph(line, font));
        }
        
        document.close();
        
        System.out.println("生成最小PDF完成，大小: " + formatSize(baos.size()));
        return baos.toByteArray();
    }
    
    /**
     * 对比不同压缩方法的效果
     */
    public static void compareCompressionMethods(Map<String, String> data) {
        System.out.println("=== PDF压缩方法对比 ===");
        
        try {
            // 方法1: 子集字体
            byte[] subsetPdf = createSimpleCompressedPdf(data);
            System.out.println("子集字体PDF: " + formatSize(subsetPdf.length));
            
            // 方法2: 最小文件
            byte[] minimalPdf = createMinimalPdf(data);
            System.out.println("最小文件PDF: " + formatSize(minimalPdf.length));
            
            // 计算压缩比
            if (subsetPdf.length > 0 && minimalPdf.length > 0) {
                double ratio = (double) subsetPdf.length / minimalPdf.length;
                System.out.println("子集字体是最小文件的 " + String.format("%.1f", ratio) + " 倍");
                
                long saved = subsetPdf.length - minimalPdf.length;
                System.out.println("使用最小文件方法节省: " + formatSize(saved));
            }
            
        } catch (Exception e) {
            System.out.println("对比测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("========================");
    }
    
    /**
     * 验证压缩是否真正生效
     */
    public static void validateCompression() {
        System.out.println("=== 压缩验证测试 ===");
        
        try {
            // 创建测试数据
            Map<String, String> testData = java.util.Map.of(
                "姓名", "张三",
                "公司", "测试公司有限责任公司",
                "日期", "2024年8月15日",
                "金额", "￥10,000.00",
                "描述", "这是一个测试描述，包含中文字符用于验证字体压缩效果。"
            );
            
            // 测试1: 创建未压缩的PDF（作为基准）
            byte[] uncompressedPdf = createUncompressedPdf(testData);
            System.out.println("未压缩PDF: " + formatSize(uncompressedPdf.length));
            
            // 测试2: 创建压缩的PDF
            byte[] compressedPdf = createSimpleCompressedPdf(testData);
            System.out.println("压缩PDF: " + formatSize(compressedPdf.length));
            
            // 测试3: 创建最小PDF
            byte[] minimalPdf = createMinimalPdf(testData);
            System.out.println("最小PDF: " + formatSize(minimalPdf.length));
            
            // 分析压缩效果
            if (uncompressedPdf.length > 0) {
                double compressedRatio = (double) compressedPdf.length / uncompressedPdf.length;
                double minimalRatio = (double) minimalPdf.length / uncompressedPdf.length;
                
                System.out.println("压缩PDF是原始的 " + String.format("%.1f%%", compressedRatio * 100));
                System.out.println("最小PDF是原始的 " + String.format("%.1f%%", minimalRatio * 100));
                
                if (compressedRatio < 0.8) {
                    System.out.println("✓ 压缩效果显著");
                } else {
                    System.out.println("! 压缩效果不明显");
                }
                
                if (minimalRatio < 0.3) {
                    System.out.println("✓ 最小文件效果显著");
                } else {
                    System.out.println("! 最小文件效果不明显");
                }
            }
            
        } catch (Exception e) {
            System.out.println("验证测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("==================");
    }
    
    /**
     * 创建未压缩的PDF（作为对比基准）
     */
    private static byte[] createUncompressedPdf(Map<String, String> data) 
            throws DocumentException, IOException {
        
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // 不启用压缩
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        // writer.setCompressionLevel(PdfWriter.NO_COMPRESSION); // 不压缩
        
        document.open();
        
        // 使用完整嵌入字体
        BaseFont baseFont = EffectivePdfCompressor.createSubsetFont();
        baseFont.setSubset(false); // 不使用子集
        Font font = new Font(baseFont, 12, Font.NORMAL);
        
        document.add(new Paragraph("未压缩PDF测试文档", font));
        document.add(new Paragraph(" ", font));
        
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String line = entry.getKey() + ": " + entry.getValue();
            document.add(new Paragraph(line, font));
        }
        
        document.close();
        return baos.toByteArray();
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
        System.out.println("PDF生成和压缩测试开始...");
        
        // 创建测试数据
        Map<String, String> testData = java.util.Map.of(
            "姓名", "张三",
            "公司", "测试公司有限责任公司",
            "日期", "2024年8月15日",
            "金额", "￥10,000.00",
            "描述", "这是一个测试描述，包含中文字符。"
        );
        
        // 运行对比测试
        compareCompressionMethods(testData);
        
        // 运行验证测试
        validateCompression();
        
        System.out.println("测试完成。");
    }
}
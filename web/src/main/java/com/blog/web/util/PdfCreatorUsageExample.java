package com.blog.web.util;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * PDF创建工具使用示例
 * 展示如何替换原始的createPDF方法
 */
public class PdfCreatorUsageExample {
    
    /**
     * 原始方法的替换示例
     */
    public static void main(String[] args) {
        // 准备测试数据
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("name", "张三");
        dataMap.put("company", "测试保险公司");
        dataMap.put("date", "2024-08-15");
        dataMap.put("amount", "￥10,000.00");
        
        String mouldPath = "template.pdf";  // PDF模板路径
        String outPutPath = "output.pdf";   // 输出PDF路径
        String signatureImgPath = "signature.png"; // 签名图片路径
        
        try {
            System.out.println("=== 原始方法对比 ===");
            
            // 方法1：最小文件大小（推荐，保险公司应该接受）
            System.out.println("生成最小文件PDF...");
            OptimizedPdfCreator.createMinimalPDF(dataMap, mouldPath, "output_minimal.pdf", signatureImgPath);
            
            // 方法2：子集化字体（如果需要更好的兼容性）
            System.out.println("生成子集化字体PDF...");
            OptimizedPdfCreator.createCompressedPDF(dataMap, mouldPath, "output_compressed.pdf", signatureImgPath);
            
            System.out.println("PDF生成完成！");
            
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 你的原始方法应该这样替换：
     */
    public static void createPDF_OLD_VERSION(Map<String, String> dataMap, String mouldPath,
                                           String outPutPath, String signatureImgPath) 
            throws UnsupportedEncodingException, FileNotFoundException {
        
        // 原始代码：
        // CompactFontUtil.clearCache();
        // bfChinese = CompactFontUtil.getCompressedBaseFont();
        
        // 新的替换方案：
        // 方案A：最小文件（推荐）
        OptimizedPdfCreator.createMinimalPDF(dataMap, mouldPath, outPutPath, signatureImgPath);
        
        // 或者方案B：子集化字体
        // OptimizedPdfCreator.createCompressedPDF(dataMap, mouldPath, outPutPath, signatureImgPath);
    }
    
    /**
     * 批量处理示例
     */
    public static void batchProcessPDFs() {
        System.out.println("=== 批量PDF处理示例 ===");
        
        // 模拟多个PDF生成任务
        for (int i = 1; i <= 3; i++) {
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("name", "客户" + i);
            dataMap.put("company", "保险公司" + i);
            dataMap.put("policyNumber", "POLICY" + String.format("%03d", i));
            
            try {
                String outputPath = "batch_output_" + i + ".pdf";
                OptimizedPdfCreator.createMinimalPDF(dataMap, "template.pdf", outputPath, "signature.png");
                System.out.println("批量处理 " + i + "/3 完成");
            } catch (Exception e) {
                System.out.println("批量处理 " + i + " 失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 文件大小对比测试
     */
    public static void comparePdfSizes(Map<String, String> dataMap, String mouldPath, String signatureImgPath) {
        System.out.println("=== PDF文件大小对比 ===");
        
        try {
            // 生成最小文件
            OptimizedPdfCreator.createMinimalPDF(dataMap, mouldPath, "compare_minimal.pdf", signatureImgPath);
            
            // 生成子集字体文件
            OptimizedPdfCreator.createCompressedPDF(dataMap, mouldPath, "compare_compressed.pdf", signatureImgPath);
            
            // 输出文件大小对比
            java.io.File minimalFile = new java.io.File("compare_minimal.pdf");
            java.io.File compressedFile = new java.io.File("compare_compressed.pdf");
            
            if (minimalFile.exists() && compressedFile.exists()) {
                System.out.println("最小文件大小: " + formatSize(minimalFile.length()));
                System.out.println("子集字体大小: " + formatSize(compressedFile.length()));
                
                double ratio = (double) compressedFile.length() / minimalFile.length();
                System.out.println("子集字体是最小文件的 " + String.format("%.1f", ratio) + " 倍");
            }
            
        } catch (Exception e) {
            System.out.println("对比测试失败: " + e.getMessage());
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
}
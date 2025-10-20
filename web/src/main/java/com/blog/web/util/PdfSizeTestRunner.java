package com.blog.web.util;

import java.util.HashMap;
import java.util.Map;

/**
 * PDF大小测试运行器
 * 用于测试不同的PDF创建方法
 */
public class PdfSizeTestRunner {
    
    public static void main(String[] args) {
        // 准备测试数据
        Map<String, String> testData = new HashMap<>();
        testData.put("name", "张三");
        testData.put("company", "测试保险公司");
        testData.put("date", "2024-08-15");
        testData.put("amount", "￥10,000.00");
        
        String templatePath = "template.pdf";  // 替换为你的模板路径
        String outputPath = "test_output.pdf";
        String signaturePath = "signature.png"; // 替换为你的签名图片路径
        
        System.out.println("=== PDF大小测试开始 ===");
        System.out.println("模板路径: " + templatePath);
        System.out.println("输出路径: " + outputPath);
        System.out.println();
        
        // 测试1：真正优化的版本
        System.out.println("测试1：真正优化版本");
        TrulyOptimizedPdfCreator.createTrulyOptimizedPDF(testData, templatePath, "output_optimized.pdf", signaturePath);
        System.out.println();
        
        // 测试2：超级简化版本
        System.out.println("测试2：超级简化版本");
        TrulyOptimizedPdfCreator.createSuperSimplePDF(testData, templatePath, "output_simple.pdf");
        System.out.println();
        
        // 测试3：诊断版本
        System.out.println("测试3：诊断版本");
        TrulyOptimizedPdfCreator.createDiagnosticPDF(testData, templatePath, "output_diagnostic");
        System.out.println();
        
        System.out.println("=== 测试完成 ===");
        System.out.println("请检查生成的PDF文件大小");
    }
    
    /**
     * 快速测试方法 - 只测试最关键的
     */
    public static void quickTest(String templatePath, String outputPath, Map<String, String> data) {
        System.out.println("快速测试开始...");
        
        // 只运行超级简化版本
        TrulyOptimizedPdfCreator.createSuperSimplePDF(data, templatePath, outputPath);
        
        System.out.println("快速测试完成");
    }
}
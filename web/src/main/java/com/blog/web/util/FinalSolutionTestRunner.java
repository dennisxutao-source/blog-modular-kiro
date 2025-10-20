package com.blog.web.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 终极解决方案测试运行器
 * 解决两个核心问题：
 * 1. 系统字体中文不显示
 * 2. 思源宋体文件太大
 */
public class FinalSolutionTestRunner {
    
    public static void main(String[] args) {
        // 准备测试数据
        Map<String, String> testData = new HashMap<>();
        testData.put("name", "张三");
        testData.put("company", "中国人寿保险股份有限公司");
        testData.put("date", "2024年8月15日");
        testData.put("amount", "￥100,000.00");
        testData.put("policyNumber", "POLICY-2024-001");
        
        String templatePath = "template.pdf";  // 替换为你的模板路径
        String outputDir = "final_solution_test";
        String signaturePath = "signature.png"; // 替换为你的签名图片路径
        
        System.out.println("=== 终极解决方案测试 ===");
        System.out.println("问题1：系统字体中文不显示");
        System.out.println("问题2：思源宋体文件18.7MB太大");
        System.out.println("目标：找到既能显示中文又文件小的方案");
        System.out.println();
        
        // 运行所有解决方案
        FinalSolutionPdfCreator.testAllSolutions(testData, templatePath, outputDir, signaturePath);
        
        System.out.println("\n=== 方案选择指南 ===");
        System.out.println("1. 检查solution1_builtin.pdf：");
        System.out.println("   - 如果中文显示正常且文件小 -> 使用方案1");
        System.out.println("2. 检查solution2_default.pdf：");
        System.out.println("   - 如果中文显示正常且文件小 -> 使用方案2");
        System.out.println("3. 检查diagnostic目录：");
        System.out.println("   - test3_fill_no_font.pdf 如果显示正常 -> 最佳方案");
        System.out.println("4. 如果所有方案都有问题 -> 可能需要更换PDF模板");
    }
    
    /**
     * 快速测试内置字体方案
     */
    public static void quickTestBuiltinFont(String templatePath, String outputPath, Map<String, String> data) {
        System.out.println("快速测试内置字体方案...");
        FinalSolutionPdfCreator.createWithBuiltinChineseFont(data, templatePath, outputPath, null);
    }
    
    /**
     * 快速测试默认字体方案
     */
    public static void quickTestDefaultFont(String templatePath, String outputPath, Map<String, String> data) {
        System.out.println("快速测试默认字体方案...");
        FinalSolutionPdfCreator.createWithDefaultFont(data, templatePath, outputPath, null);
    }
    
    /**
     * 推荐方案测试
     */
    public static void testRecommendedSolutions(String templatePath, String outputDir, Map<String, String> data) {
        System.out.println("=== 推荐方案测试 ===");
        
        new java.io.File(outputDir).mkdirs();
        
        // 推荐方案1：默认字体（最可能成功）
        System.out.println("推荐方案1：默认字体（保持模板原有字体）");
        FinalSolutionPdfCreator.createWithDefaultFont(data, templatePath, outputDir + "/recommended_default.pdf", null);
        
        // 推荐方案2：内置字体（平衡方案）
        System.out.println("推荐方案2：内置字体（iText内置中文字体）");
        FinalSolutionPdfCreator.createWithBuiltinChineseFont(data, templatePath, outputDir + "/recommended_builtin.pdf", null);
        
        System.out.println("推荐方案测试完成");
        System.out.println("请检查两个文件的显示效果和文件大小");
    }
    
    /**
     * 诊断模式
     */
    public static void runDiagnosticMode(String templatePath, Map<String, String> data) {
        System.out.println("=== 诊断模式 ===");
        System.out.println("逐步测试，找出问题根源");
        
        FinalSolutionPdfCreator.createDiagnosticVersion(data, templatePath, "diagnostic_output");
        
        System.out.println("诊断完成，请检查diagnostic_output目录");
        System.out.println("对比各个测试文件的大小和显示效果");
    }
}
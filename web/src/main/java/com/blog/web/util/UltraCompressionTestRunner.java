package com.blog.web.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 超级压缩测试运行器
 * 目标：将18.7MB压缩到1MB以下
 */
public class UltraCompressionTestRunner {
    
    public static void main(String[] args) {
        // 准备测试数据
        Map<String, String> testData = new HashMap<>();
        testData.put("name", "张三");
        testData.put("company", "中国人寿保险股份有限公司");
        testData.put("date", "2024年8月15日");
        testData.put("amount", "￥100,000.00");
        testData.put("policyNumber", "POLICY-2024-001");
        testData.put("description", "终身寿险保单，保障期限终身，受益人为法定继承人。");
        
        String templatePath = "template.pdf";  // 替换为你的模板路径
        String outputDir = "ultra_compression_test";
        String signaturePath = "signature.png"; // 替换为你的签名图片路径
        
        System.out.println("=== 超级压缩测试开始 ===");
        System.out.println("当前问题：18.7MB文件太大");
        System.out.println("目标：压缩到1MB以下");
        System.out.println("模板路径: " + templatePath);
        System.out.println();
        
        // 运行所有压缩方案
        UltraCompressedPdfCreator.testAllCompressionMethods(testData, templatePath, outputDir, signaturePath);
        
        System.out.println("\n=== 方案选择建议 ===");
        System.out.println("1. 如果系统字体方案文件最小且显示效果可接受 -> 使用方案4");
        System.out.println("2. 如果需要保持思源宋体效果 -> 选择文件最小的思源宋体方案");
        System.out.println("3. 如果所有方案都还是很大 -> 可能需要更换字体或使用图片替代");
    }
    
    /**
     * 快速测试系统字体方案
     */
    public static void quickTestSystemFont(String templatePath, String outputPath, Map<String, String> data) {
        System.out.println("快速测试系统字体方案...");
        UltraCompressedPdfCreator.createWithSystemFont(data, templatePath, outputPath, null);
    }
    
    /**
     * 快速测试Light字体方案
     */
    public static void quickTestLightFont(String templatePath, String outputPath, Map<String, String> data) {
        System.out.println("快速测试Light字体方案...");
        UltraCompressedPdfCreator.createWithLightFont(data, templatePath, outputPath, null);
    }
    
    /**
     * 推荐方案测试
     */
    public static void testRecommendedSolutions(String templatePath, String outputDir, Map<String, String> data) {
        System.out.println("=== 推荐方案测试 ===");
        
        new java.io.File(outputDir).mkdirs();
        
        // 推荐方案1：系统字体（最小文件）
        System.out.println("推荐方案1：系统字体（预期<500KB）");
        UltraCompressedPdfCreator.createWithSystemFont(data, templatePath, outputDir + "/recommended_system.pdf", null);
        
        // 推荐方案2：极限子集（保持思源宋体）
        System.out.println("推荐方案2：极限子集（预期1-3MB）");
        UltraCompressedPdfCreator.createWithExtremeSubset(data, templatePath, outputDir + "/recommended_subset.pdf", null);
        
        System.out.println("推荐方案测试完成");
    }
}
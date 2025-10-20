package com.blog.web.util;

import java.util.HashMap;
import java.util.Map;

/**
 * PDF转图片测试运行器
 * 解决PDF转图片时中文显示小方格的问题
 */
public class ImageConversionTestRunner {
    
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
        String outputDir = "image_conversion_test";
        String signaturePath = "signature.png"; // 替换为你的签名图片路径
        
        System.out.println("=== PDF转图片问题解决测试 ===");
        System.out.println("问题：PDF直接打开正常，转图片后中文显示小方格");
        System.out.println("原因：字体没有完全嵌入到PDF中");
        System.out.println("解决：创建图片转换友好的PDF");
        System.out.println();
        
        // 运行所有图片友好方案
        PdfToImageFontFixer.testImageFriendlyMethods(testData, templatePath, outputDir, signaturePath);
        
        System.out.println("\n=== 测试指南 ===");
        System.out.println("1. 使用PDF转图片工具测试生成的PDF文件");
        System.out.println("2. 检查哪个PDF转换的图片中文显示正常");
        System.out.println("3. 选择效果最好的方案应用到生产环境");
        System.out.println();
        System.out.println("推荐测试顺序：");
        System.out.println("1. 先测试 image_friendly_full.pdf（效果最好但文件大）");
        System.out.println("2. 再测试 image_friendly_smart.pdf（平衡方案）");
        System.out.println("3. 最后测试 image_friendly_standard.pdf（文件最小）");
    }
    
    /**
     * 快速测试完全嵌入方案
     */
    public static void quickTestFullEmbedded(String templatePath, String outputPath, Map<String, String> data) {
        System.out.println("快速测试完全嵌入字体方案...");
        PdfToImageFontFixer.createImageFriendlyPdf(data, templatePath, outputPath, null);
        System.out.println("完成！请测试此PDF转换为图片的效果");
    }
    
    /**
     * 快速测试智能嵌入方案
     */
    public static void quickTestSmartEmbedded(String templatePath, String outputPath, Map<String, String> data) {
        System.out.println("快速测试智能嵌入字体方案...");
        PdfToImageFontFixer.createSmartEmbeddedPdf(data, templatePath, outputPath, null);
        System.out.println("完成！请测试此PDF转换为图片的效果");
    }
    
    /**
     * 推荐方案测试
     */
    public static void testRecommendedForImageConversion(String templatePath, String outputDir, Map<String, String> data) {
        System.out.println("=== 图片转换推荐方案测试 ===");
        
        new java.io.File(outputDir).mkdirs();
        
        // 推荐方案1：完全嵌入（最可能解决问题）
        System.out.println("推荐方案1：完全嵌入思源宋体");
        PdfToImageFontFixer.createImageFriendlyPdf(data, templatePath, outputDir + "/recommended_full_embed.pdf", null);
        
        // 推荐方案2：智能嵌入（平衡方案）
        System.out.println("推荐方案2：智能嵌入策略");
        PdfToImageFontFixer.createSmartEmbeddedPdf(data, templatePath, outputDir + "/recommended_smart_embed.pdf", null);
        
        System.out.println("推荐方案测试完成");
        System.out.println("请用PDF转图片工具测试这两个文件");
    }
}
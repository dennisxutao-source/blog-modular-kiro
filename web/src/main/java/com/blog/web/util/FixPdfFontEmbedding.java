package com.blog.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 修复PDF字体嵌入问题
 * 重新生成一个真正嵌入字体的PDF
 */
public class FixPdfFontEmbedding {
    
    private static final Logger logger = LoggerFactory.getLogger(FixPdfFontEmbedding.class);
    
    public static void main(String[] args) {
        logger.info("=== 修复PDF字体嵌入问题 ===");
        
        // 准备测试数据（根据你的PDF内容）
        Map<String, String> testData = new HashMap<>();
        testData.put("id", "10004730");
        testData.put("name", "测韦欣");
        testData.put("title", "测新字体");
        testData.put("date", "2025-01-18");
        testData.put("content", "这是一个测试中文字体嵌入的PDF文档");
        testData.put("note", "确保在任何系统上都能正确显示中文");
        
        // 假设的模板路径（你需要提供实际的模板文件）
        String templatePath = "../test-pdf.pdf"; // 使用原PDF作为模板
        String outputPath = "../fixed-embedded-font.pdf"; // 输出路径
        
        try {
            logger.info("开始生成嵌入字体的PDF");
            logger.info("模板文件: {}", templatePath);
            logger.info("输出文件: {}", outputPath);
            
            // 使用静态方法生成嵌入字体的PDF
            StaticEmbeddedFontPdfFiller.createEmbeddedFontPDF(
                testData,
                templatePath,
                outputPath,
                null // 无签名图片
            );
            
            logger.info("✅ 嵌入字体PDF生成完成");
            
            // 立即验证新生成的PDF
            logger.info("\n=== 验证新生成的PDF ===");
            SimplePdfFontChecker.checkPdfFonts(outputPath);
            
        } catch (Exception e) {
            logger.error("❌ 生成嵌入字体PDF失败", e);
            
            // 提供替代方案
            logger.info("\n=== 替代方案 ===");
            logger.info("如果模板文件有问题，可以尝试:");
            logger.info("1. 创建一个简单的PDF模板");
            logger.info("2. 或者使用其他PDF生成方式");
            
            // 尝试创建一个简单的测试PDF
            createSimpleTestPdf();
        }
    }
    
    /**
     * 创建一个简单的测试PDF（不依赖模板）
     */
    private static void createSimpleTestPdf() {
        logger.info("\n=== 创建简单测试PDF ===");
        
        try {
            // 这里可以添加创建简单PDF的代码
            // 由于需要iText的Document类，暂时跳过
            logger.info("简单PDF创建功能待实现");
            
        } catch (Exception e) {
            logger.error("创建简单测试PDF失败", e);
        }
    }
    
    /**
     * 比较原PDF和新PDF的差异
     */
    public static void comparePdfs(String originalPdf, String newPdf) {
        logger.info("=== 比较PDF字体嵌入情况 ===");
        
        logger.info("\n原PDF文件: {}", originalPdf);
        SimplePdfFontChecker.checkPdfFonts(originalPdf);
        
        logger.info("\n新PDF文件: {}", newPdf);
        SimplePdfFontChecker.checkPdfFonts(newPdf);
        
        logger.info("\n=== 比较完成 ===");
    }
}
package com.blog.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试特定PDF文件的字体嵌入情况
 * 专门用于测试 "eg.10004730+测韦欣+测新字体 (8).pdf"
 */
public class TestSpecificPdf {
    
    private static final Logger logger = LoggerFactory.getLogger(TestSpecificPdf.class);
    
    public static void main(String[] args) {
        String pdfFile = "eg.10004730+测韦欣+测新字体 (8).pdf";
        
        logger.info("=== 测试特定PDF文件 ===");
        logger.info("文件名: {}", pdfFile);
        
        // 检查文件是否存在
        java.io.File file = new java.io.File(pdfFile);
        if (!file.exists()) {
            logger.error("❌ 文件不存在: {}", pdfFile);
            logger.info("请确保文件在当前工作目录中");
            return;
        }
        
        logger.info("✅ 文件存在");
        logger.info("文件大小: {} bytes ({} KB)", file.length(), file.length() / 1024);
        
        // 快速检查字体嵌入
        logger.info("\n=== 快速字体嵌入检查 ===");
        boolean hasEmbedded = QuickFontEmbeddingChecker.quickCheckFontEmbedding(pdfFile);
        
        // 详细分析
        logger.info("\n=== 详细字体分析 ===");
        PdfFontEmbeddingAnalyzer.analyzePdfFontEmbedding(pdfFile);
        
        // 最终结论
        logger.info("\n" + "=".repeat(60));
        logger.info("最终结论");
        logger.info("=".repeat(60));
        
        if (hasEmbedded) {
            logger.info("✅ 该PDF文件包含嵌入字体");
            logger.info("✅ 在任何系统上都能正确显示中文");
            logger.info("✅ 字体兼容性良好");
        } else {
            logger.error("❌ 该PDF文件未包含嵌入字体");
            logger.warn("⚠️  可能的问题:");
            logger.warn("   - 在没有相应字体的系统上显示异常");
            logger.warn("   - 中文可能显示为方框或其他字符");
            logger.warn("   - 字体依赖系统安装的字体");
            
            logger.info("\n💡 解决方案:");
            logger.info("   1. 使用 EmbeddedFontPdfFiller 重新生成PDF");
            logger.info("   2. 确保设置 BaseFont.EMBEDDED 参数");
            logger.info("   3. 使用思源字体或其他支持中文的字体");
        }
        
        // 文件大小分析
        long sizeKB = file.length() / 1024;
        if (sizeKB < 100) {
            logger.info("\n📊 文件大小分析: 文件较小 ({} KB)，符合未嵌入字体的特征", sizeKB);
        } else if (sizeKB > 1000) {
            logger.info("\n📊 文件大小分析: 文件较大 ({} KB)，可能包含其他资源", sizeKB);
        } else {
            logger.info("\n📊 文件大小分析: 文件中等大小 ({} KB)", sizeKB);
        }
    }
    
    /**
     * 提供建议的解决方案
     */
    public static void provideSolution() {
        logger.info("\n=== 推荐解决方案 ===");
        
        logger.info("1. 使用优化后的PDF生成工具:");
        logger.info("   EmbeddedFontPdfFiller.createEmbeddedFontPDF(...)");
        
        logger.info("\n2. 确保字体嵌入设置:");
        logger.info("   BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)");
        
        logger.info("\n3. 使用classpath字体资源:");
        logger.info("   将字体文件放在 src/main/resources/fonts/ 目录下");
        
        logger.info("\n4. 验证生成的PDF:");
        logger.info("   使用 QuickFontEmbeddingChecker 检查新生成的PDF");
        
        logger.info("\n5. 测试兼容性:");
        logger.info("   在不同系统上测试PDF显示效果");
    }
}
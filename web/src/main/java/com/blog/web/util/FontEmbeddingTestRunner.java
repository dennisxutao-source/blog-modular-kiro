package com.blog.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 字体嵌入测试运行器
 * 用于在Spring Boot应用中测试PDF字体嵌入情况
 */
@Component
public class FontEmbeddingTestRunner implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(FontEmbeddingTestRunner.class);
    
    @Override
    public void run(String... args) throws Exception {
        // 只在特定参数下运行
        if (args.length > 0 && "check-font-embedding".equals(args[0])) {
            runFontEmbeddingTests();
        }
    }
    
    /**
     * 运行字体嵌入测试
     */
    public void runFontEmbeddingTests() {
        logger.info("=== 开始字体嵌入测试 ===");
        
        // 测试文件列表
        String[] testFiles = {
            "eg.10004730+测韦欣+测新字体 (8).pdf",
            // 可以添加更多测试文件
        };
        
        for (String testFile : testFiles) {
            testSingleFile(testFile);
        }
        
        logger.info("=== 字体嵌入测试完成 ===");
    }
    
    /**
     * 测试单个文件
     */
    private void testSingleFile(String filePath) {
        logger.info("\n" + "=".repeat(50));
        logger.info("测试文件: {}", filePath);
        logger.info("=".repeat(50));
        
        try {
            // 检查文件是否存在
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                logger.error("❌ 文件不存在: {}", filePath);
                return;
            }
            
            logger.info("✅ 文件存在，大小: {} KB", file.length() / 1024);
            
            // 1. 快速检查
            logger.info("\n--- 快速检查 ---");
            boolean hasEmbedded = QuickFontEmbeddingChecker.quickCheckFontEmbedding(filePath);
            
            // 2. 详细分析
            logger.info("\n--- 详细分析 ---");
            PdfFontEmbeddingAnalyzer.analyzePdfFontEmbedding(filePath);
            
            // 3. 结论
            logger.info("\n--- 结论 ---");
            if (hasEmbedded) {
                logger.info("✅ 该PDF包含嵌入字体");
                logger.info("💡 优点: 在任何系统上都能正确显示");
                logger.info("⚠️  注意: 文件大小可能较大");
            } else {
                logger.error("❌ 该PDF未包含嵌入字体");
                logger.warn("⚠️  风险: 在没有相应字体的系统上可能显示异常");
                logger.info("💡 建议: 使用嵌入字体的PDF生成方式");
            }
            
        } catch (Exception e) {
            logger.error("测试文件失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 手动触发测试
     */
    public void manualTest(String filePath) {
        logger.info("手动测试文件: {}", filePath);
        testSingleFile(filePath);
    }
    
    /**
     * 比较两个PDF文件的字体嵌入情况
     */
    public void compareFontEmbedding(String file1, String file2) {
        logger.info("=== 比较PDF字体嵌入情况 ===");
        
        logger.info("\n文件1: {}", file1);
        boolean file1HasEmbedded = QuickFontEmbeddingChecker.quickCheckFontEmbedding(file1);
        
        logger.info("\n文件2: {}", file2);
        boolean file2HasEmbedded = QuickFontEmbeddingChecker.quickCheckFontEmbedding(file2);
        
        logger.info("\n=== 比较结果 ===");
        logger.info("文件1嵌入字体: {}", file1HasEmbedded ? "✅ 是" : "❌ 否");
        logger.info("文件2嵌入字体: {}", file2HasEmbedded ? "✅ 是" : "❌ 否");
        
        if (file1HasEmbedded == file2HasEmbedded) {
            logger.info("两个文件的字体嵌入状态相同");
        } else {
            logger.warn("两个文件的字体嵌入状态不同！");
            if (file1HasEmbedded) {
                logger.info("文件1有嵌入字体，文件2没有");
            } else {
                logger.info("文件2有嵌入字体，文件1没有");
            }
        }
    }
    
    /**
     * 检查项目中的字体资源
     */
    public void checkProjectFontResources() {
        logger.info("=== 检查项目字体资源 ===");
        
        String[] fontPaths = {
            "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
            "web/src/main/resources/fonts/NotoSerifCJKsc-Regular.otf"
        };
        
        for (String fontPath : fontPaths) {
            java.io.File fontFile = new java.io.File(fontPath);
            if (fontFile.exists()) {
                long sizeKB = fontFile.length() / 1024;
                long sizeMB = fontFile.length() / (1024 * 1024);
                logger.info("✅ 字体文件存在: {} ({} KB / {} MB)", fontPath, sizeKB, sizeMB);
            } else {
                logger.warn("❌ 字体文件不存在: {}", fontPath);
            }
        }
        
        // 检查classpath资源
        logger.info("\n--- 检查Classpath字体资源 ---");
        String fontResourcePath = "fonts/NotoSerifCJKsc-Regular.otf";
        java.net.URL fontUrl = getClass().getClassLoader().getResource(fontResourcePath);
        
        if (fontUrl != null) {
            logger.info("✅ Classpath字体资源存在: {}", fontUrl.toString());
        } else {
            logger.warn("❌ Classpath字体资源不存在: {}", fontResourcePath);
        }
    }
}
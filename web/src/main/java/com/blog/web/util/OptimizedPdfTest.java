package com.blog.web.util;

import com.blog.web.config.PdfConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 优化后的PDF工具类测试
 * 验证非静态方法和logger的使用
 */
@Component
public class OptimizedPdfTest implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(OptimizedPdfTest.class);
    
    @Autowired
    private EmbeddedFontPdfFiller embeddedFontPdfFiller;
    
    @Autowired
    private PdfConfig pdfConfig;
    
    @Override
    public void run(String... args) throws Exception {
        // 只在特定条件下运行测试，避免每次启动都执行
        if (args.length > 0 && "test-pdf".equals(args[0])) {
            testOptimizedPdfCreation();
        }
    }
    
    public void testOptimizedPdfCreation() {
        logger.info("开始测试优化后的PDF工具类");
        
        try {
            // 显示配置信息
            logger.info("PDF配置信息:");
            logger.info("字体路径: {}", pdfConfig.getFontPath());
            logger.info("默认字体大小: {}", pdfConfig.getDefaultFontSize());
            logger.info("是否嵌入字体: {}", pdfConfig.isEmbedFont());
            
            // 准备测试数据
            Map<String, String> testData = new HashMap<>();
            testData.put("testField1", "测试中文内容");
            testData.put("testField2", "Test English Content");
            testData.put("testField3", "混合内容 Mixed Content");
            testData.put("date", "2025-01-18");
            testData.put("amount", "￥12,345.67");
            
            String templatePath = "test-template.pdf"; // 假设的模板路径
            String outputPath = pdfConfig.getOutputDir() + "optimized-test-output.pdf";
            
            logger.info("准备生成PDF:");
            logger.info("模板路径: {}", templatePath);
            logger.info("输出路径: {}", outputPath);
            logger.info("测试数据字段数: {}", testData.size());
            
            // 使用优化后的工具类
            embeddedFontPdfFiller.createEmbeddedFontPDF(
                testData,
                templatePath,
                outputPath,
                null
            );
            
            logger.info("✅ 优化后的PDF工具类测试完成");
            
        } catch (Exception e) {
            logger.error("❌ 优化后的PDF工具类测试失败", e);
        }
    }
    
    /**
     * 手动触发测试的方法
     */
    public void manualTest() {
        logger.info("手动触发PDF工具类测试");
        testOptimizedPdfCreation();
    }
}
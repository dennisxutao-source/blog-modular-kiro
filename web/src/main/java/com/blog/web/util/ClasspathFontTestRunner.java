package com.blog.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Classpath字体加载测试
 * 测试两种方式：实例方法和静态方法
 */
@Component
public class ClasspathFontTestRunner implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(ClasspathFontTestRunner.class);
    
    @Autowired
    private EmbeddedFontPdfFiller embeddedFontPdfFiller;
    
    @Override
    public void run(String... args) throws Exception {
        // 只在特定条件下运行测试
        if (args.length > 0 && "test-classpath-font".equals(args[0])) {
            testClasspathFontLoading();
        }
    }
    
    public void testClasspathFontLoading() {
        logger.info("开始测试Classpath字体加载");
        
        // 准备测试数据
        Map<String, String> testData = new HashMap<>();
        testData.put("name", "张三");
        testData.put("company", "测试公司");
        testData.put("date", "2025-01-18");
        testData.put("amount", "￥10,000.00");
        testData.put("description", "这是一个测试中文字体显示的PDF文档");
        
        String templatePath = "test-template.pdf"; // 假设的模板路径
        
        // 测试1：使用实例方法（Spring Bean）
        testInstanceMethod(testData, templatePath);
        
        // 测试2：使用静态方法
        testStaticMethod(testData, templatePath);
        
        logger.info("Classpath字体加载测试完成");
    }
    
    /**
     * 测试实例方法（Spring Bean方式）
     */
    private void testInstanceMethod(Map<String, String> testData, String templatePath) {
        logger.info("=== 测试实例方法（Spring Bean） ===");
        
        try {
            String outputPath = "output/instance-method-test.pdf";
            
            logger.info("使用Spring Bean实例方法生成PDF");
            embeddedFontPdfFiller.createEmbeddedFontPDF(
                testData,
                templatePath,
                outputPath,
                null
            );
            
            logger.info("✅ 实例方法测试成功");
            
        } catch (Exception e) {
            logger.error("❌ 实例方法测试失败", e);
        }
    }
    
    /**
     * 测试静态方法
     */
    private void testStaticMethod(Map<String, String> testData, String templatePath) {
        logger.info("=== 测试静态方法 ===");
        
        try {
            String outputPath = "output/static-method-test.pdf";
            
            logger.info("使用静态方法生成PDF");
            StaticEmbeddedFontPdfFiller.createEmbeddedFontPDF(
                testData,
                templatePath,
                outputPath,
                null
            );
            
            logger.info("✅ 静态方法测试成功");
            
        } catch (Exception e) {
            logger.error("❌ 静态方法测试失败", e);
        }
    }
    
    /**
     * 手动触发测试
     */
    public void manualTest() {
        logger.info("手动触发Classpath字体加载测试");
        testClasspathFontLoading();
    }
    
    /**
     * 测试字体资源是否存在
     */
    public void testFontResourceExists() {
        logger.info("=== 测试字体资源是否存在 ===");
        
        String fontResourcePath = "fonts/NotoSerifCJKsc-Regular.otf";
        
        // 测试ClassLoader是否能找到资源
        java.net.URL fontUrl = getClass().getClassLoader().getResource(fontResourcePath);
        
        if (fontUrl != null) {
            logger.info("✅ 字体资源存在: {}", fontUrl.toString());
            
            // 测试能否读取资源流
            try (java.io.InputStream fontStream = getClass().getClassLoader().getResourceAsStream(fontResourcePath)) {
                if (fontStream != null) {
                    int available = fontStream.available();
                    logger.info("✅ 字体资源可读取，大小: {} bytes", available);
                } else {
                    logger.error("❌ 无法读取字体资源流");
                }
            } catch (Exception e) {
                logger.error("❌ 读取字体资源流失败", e);
            }
        } else {
            logger.error("❌ 字体资源不存在: {}", fontResourcePath);
            
            // 列出fonts目录下的资源
            logger.info("尝试列出fonts目录下的资源...");
            try {
                java.net.URL fontsDir = getClass().getClassLoader().getResource("fonts/");
                if (fontsDir != null) {
                    logger.info("fonts目录存在: {}", fontsDir.toString());
                } else {
                    logger.error("fonts目录不存在");
                }
            } catch (Exception e) {
                logger.error("检查fonts目录失败", e);
            }
        }
    }
}
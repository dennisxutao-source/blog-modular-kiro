package com.blog.web.util;

import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * PDF压缩测试类
 * 用于验证压缩效果是否真正生效
 */
public class PdfCompressionTest {
    
    /**
     * 运行完整的压缩测试
     */
    public static void runFullTest() {
        System.out.println("开始PDF压缩测试...");
        
        // 准备测试数据
        Map<String, String> testData = createTestData();
        
        // 模拟PDF模板（实际使用时替换为真实模板）
        try {
            // 这里需要替换为你的实际PDF模板文件
            // ClassPathResource templateResource = new ClassPathResource("templates/your-template.pdf");
            // InputStream templateStream = templateResource.getInputStream();
            
            // 由于没有实际模板，我们先验证字体设置
            System.out.println("验证字体设置...");
            EffectivePdfCompressor.validateFontSettings();
            
            System.out.println("打印使用建议...");
            EffectivePdfCompressor.printUsageRecommendations();
            
            // 如果有实际模板，取消注释下面的代码
            // EffectivePdfCompressor.testCompressionEffectiveness(templateStream, testData);
            
        } catch (Exception e) {
            System.out.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 快速测试 - 只验证字体
     */
    public static void quickTest() {
        System.out.println("=== 快速字体测试 ===");
        EffectivePdfCompressor.validateFontSettings();
        System.out.println("==================");
    }
    
    /**
     * 创建测试数据
     */
    private static Map<String, String> createTestData() {
        Map<String, String> data = new HashMap<>();
        data.put("name", "张三");
        data.put("company", "测试公司有限责任公司");
        data.put("date", "2024年8月15日");
        data.put("amount", "￥10,000.00");
        data.put("description", "这是一个测试描述，包含中文字符用于验证字体子集化的效果。");
        data.put("address", "北京市朝阳区测试街道123号");
        data.put("phone", "138-0000-0000");
        data.put("email", "test@example.com");
        return data;
    }
    
    /**
     * 测试真实PDF模板（需要提供实际模板文件）
     */
    public static void testWithRealTemplate(String templatePath) {
        System.out.println("使用真实模板测试: " + templatePath);
        
        try {
            ClassPathResource templateResource = new ClassPathResource(templatePath);
            if (!templateResource.exists()) {
                System.out.println("模板文件不存在: " + templatePath);
                return;
            }
            
            InputStream templateStream = templateResource.getInputStream();
            Map<String, String> testData = createTestData();
            
            // 运行压缩测试
            EffectivePdfCompressor.testCompressionEffectiveness(templateStream, testData);
            
        } catch (Exception e) {
            System.out.println("真实模板测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 对比不同方法的文件大小
     */
    public static void compareFileSizes(String templatePath) {
        System.out.println("=== 文件大小对比测试 ===");
        
        try {
            ClassPathResource templateResource = new ClassPathResource(templatePath);
            if (!templateResource.exists()) {
                System.out.println("模板文件不存在，无法进行对比测试");
                return;
            }
            
            Map<String, String> testData = createTestData();
            
            // 测试最小文件
            InputStream stream1 = templateResource.getInputStream();
            byte[] minimalPdf = EffectivePdfCompressor.createMinimalSizePdf(stream1, testData);
            System.out.println("最小文件方法: " + formatSize(minimalPdf.length));
            stream1.close();
            
            // 测试子集字体
            InputStream stream2 = templateResource.getInputStream();
            byte[] subsetPdf = EffectivePdfCompressor.createSubsetPdf(stream2, testData);
            System.out.println("子集字体方法: " + formatSize(subsetPdf.length));
            stream2.close();
            
            // 计算差异
            long difference = subsetPdf.length - minimalPdf.length;
            System.out.println("大小差异: " + formatSize(difference));
            
            if (minimalPdf.length > 0) {
                double ratio = (double) subsetPdf.length / minimalPdf.length;
                System.out.println("子集字体是最小文件的 " + String.format("%.1f", ratio) + " 倍");
            }
            
        } catch (Exception e) {
            System.out.println("对比测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("========================");
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
    
    /**
     * 主测试方法
     */
    public static void main(String[] args) {
        System.out.println("PDF压缩工具测试开始...");
        
        // 运行快速测试
        quickTest();
        
        // 如果有PDF模板文件，可以运行完整测试
        // testWithRealTemplate("templates/your-template.pdf");
        
        System.out.println("测试完成。");
    }
}
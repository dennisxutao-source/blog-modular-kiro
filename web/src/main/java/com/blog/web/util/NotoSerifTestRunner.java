package com.blog.web.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 思源宋体子集化测试运行器
 */
public class NotoSerifTestRunner {
    
    public static void main(String[] args) {
        // 准备测试数据
        Map<String, String> testData = new HashMap<>();
        testData.put("name", "张三");
        testData.put("company", "中国人寿保险股份有限公司");
        testData.put("date", "2024年8月15日");
        testData.put("amount", "￥100,000.00");
        testData.put("description", "人寿保险合同，保障期限终身，受益人为法定继承人。");
        
        String templatePath = "template.pdf";  // 替换为你的模板路径
        String outputDir = "noto_serif_test";
        
        System.out.println("=== 思源宋体子集化测试 ===");
        System.out.println("模板路径: " + templatePath);
        System.out.println("测试数据字符数: " + countTotalChars(testData));
        System.out.println();
        
        // 运行所有测试
        NotoSerifSubsetCreator.testSubsetMethods(testData, templatePath, outputDir);
        
        System.out.println("\n=== 使用建议 ===");
        System.out.println("1. 如果所有方法都生成大文件，问题可能在PDF模板本身");
        System.out.println("2. 如果某个方法生成小文件，使用那个方法");
        System.out.println("3. 理想情况下，文件大小应该在1-5MB之间");
    }
    
    /**
     * 单独测试标准子集化方法
     */
    public static void testStandardSubset(String templatePath, String outputPath, Map<String, String> data) {
        System.out.println("测试标准思源宋体子集化...");
        NotoSerifSubsetCreator.createPDFWithNotoSerifSubset(data, templatePath, outputPath, null);
    }
    
    /**
     * 计算总字符数
     */
    private static int countTotalChars(Map<String, String> data) {
        int total = 0;
        for (String value : data.values()) {
            if (value != null) {
                total += value.length();
            }
        }
        return total;
    }
    
    /**
     * 快速测试方法
     */
    public static void quickTest() {
        Map<String, String> simpleData = new HashMap<>();
        simpleData.put("name", "测试");
        simpleData.put("company", "测试公司");
        
        System.out.println("快速测试思源宋体子集化...");
        NotoSerifSubsetCreator.createPDFWithNotoSerifSubset(
            simpleData, 
            "template.pdf", 
            "quick_test.pdf", 
            null
        );
    }
}
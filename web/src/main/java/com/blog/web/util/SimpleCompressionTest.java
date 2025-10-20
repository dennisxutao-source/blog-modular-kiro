package com.blog.web.util;

/**
 * 简单的压缩测试类
 * 不依赖iText，先验证基本功能
 */
public class SimpleCompressionTest {
    
    /**
     * 测试基本功能
     */
    public static void testBasicFunctionality() {
        System.out.println("=== 基本功能测试 ===");
        System.out.println("1. 检查依赖是否正确添加");
        
        try {
            // 尝试加载iText类
            Class.forName("com.itextpdf.text.pdf.BaseFont");
            System.out.println("✓ iText BaseFont 类加载成功");
        } catch (ClassNotFoundException e) {
            System.out.println("✗ iText BaseFont 类未找到: " + e.getMessage());
        }
        
        try {
            Class.forName("com.itextpdf.text.pdf.PdfWriter");
            System.out.println("✓ iText PdfWriter 类加载成功");
        } catch (ClassNotFoundException e) {
            System.out.println("✗ iText PdfWriter 类未找到: " + e.getMessage());
        }
        
        try {
            Class.forName("com.itextpdf.text.pdf.PdfReader");
            System.out.println("✓ iText PdfReader 类加载成功");
        } catch (ClassNotFoundException e) {
            System.out.println("✗ iText PdfReader 类未找到: " + e.getMessage());
        }
        
        System.out.println("==================");
    }
    
    /**
     * 检查字体文件
     */
    public static void checkFontFile() {
        System.out.println("=== 字体文件检查 ===");
        
        try {
            org.springframework.core.io.ClassPathResource resource = 
                new org.springframework.core.io.ClassPathResource("fonts/NotoSerifCJKsc-Regular.otf");
            
            if (resource.exists()) {
                System.out.println("✓ 字体文件存在: fonts/NotoSerifCJKsc-Regular.otf");
                try (java.io.InputStream is = resource.getInputStream()) {
                    int size = is.available();
                    System.out.println("  文件大小: " + formatSize(size));
                }
            } else {
                System.out.println("✗ 字体文件不存在: fonts/NotoSerifCJKsc-Regular.otf");
                System.out.println("  建议: 请确保字体文件放在 src/main/resources/fonts/ 目录下");
            }
        } catch (Exception e) {
            System.out.println("✗ 检查字体文件时出错: " + e.getMessage());
        }
        
        System.out.println("==================");
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
     * 打印解决方案
     */
    public static void printSolution() {
        System.out.println("=== PDF压缩问题解决方案 ===");
        System.out.println("问题原因:");
        System.out.println("1. 缺少iText 5.5.11依赖 - 已解决");
        System.out.println("2. 字体配置不正确 - 需要正确的子集化设置");
        System.out.println("3. 压缩设置不完整 - 需要启用全压缩");
        System.out.println();
        System.out.println("解决方案:");
        System.out.println("1. 使用 EffectivePdfCompressor.createMinimalSizePdf() - 最小文件");
        System.out.println("2. 使用 EffectivePdfCompressor.createSubsetPdf() - 平衡方案");
        System.out.println();
        System.out.println("预期效果:");
        System.out.println("- 最小文件: ~200KB (不嵌入字体)");
        System.out.println("- 子集字体: 1-3MB (嵌入子集字体)");
        System.out.println("- 压缩比: 70-90% 文件大小减少");
        System.out.println("==========================");
    }
    
    /**
     * 主测试方法
     */
    public static void main(String[] args) {
        System.out.println("PDF压缩问题诊断开始...");
        
        testBasicFunctionality();
        checkFontFile();
        printSolution();
        
        System.out.println("诊断完成。");
        System.out.println();
        System.out.println("下一步:");
        System.out.println("1. 确保iText依赖正确加载");
        System.out.println("2. 使用 EffectivePdfCompressor 类进行PDF压缩");
        System.out.println("3. 根据需求选择合适的压缩方案");
    }
}
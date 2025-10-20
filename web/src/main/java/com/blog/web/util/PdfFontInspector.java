package com.blog.web.util;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;

import java.util.Set;

/**
 * PDF字体检查工具
 * 用于查看PDF模板使用的字体信息
 */
public class PdfFontInspector {
    
    /**
     * 检查PDF模板的字体信息
     */
    public static void inspectPdfFonts(String pdfPath) {
        System.out.println("=== PDF字体信息检查 ===");
        System.out.println("PDF文件: " + pdfPath);
        
        try {
            PdfReader reader = new PdfReader(pdfPath);
            
            // 基本信息
            System.out.println("PDF版本: " + reader.getPdfVersion());
            System.out.println("页数: " + reader.getNumberOfPages());
            
            // 检查表单字段
            AcroFields fields = reader.getAcroFields();
            if (fields != null && !fields.getFields().isEmpty()) {
                System.out.println("表单字段数量: " + fields.getFields().size());
                System.out.println();
                
                System.out.println("=== 字段字体信息 ===");
                Set<String> fieldNames = fields.getFields().keySet();
                int count = 0;
                
                for (String fieldName : fieldNames) {
                    try {
                        // 获取字段的字体信息
                        String fontInfo = getFieldFontInfo(fields, fieldName);
                        System.out.println("字段: " + fieldName);
                        System.out.println("  字体信息: " + fontInfo);
                        System.out.println();
                        
                        count++;
                        if (count >= 10) { // 只显示前10个字段
                            System.out.println("... (还有 " + (fieldNames.size() - 10) + " 个字段)");
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("字段: " + fieldName + " - 无法获取字体信息");
                    }
                }
            } else {
                System.out.println("此PDF没有表单字段");
            }
            
            reader.close();
            
        } catch (Exception e) {
            System.out.println("检查失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("===================");
    }
    
    /**
     * 获取字段的字体信息
     */
    private static String getFieldFontInfo(AcroFields fields, String fieldName) {
        try {
            // 尝试获取字段的字体属性
            // iText 5.5.11 可能不支持这个方法，直接返回默认信息
            return "使用模板默认字体";
            
        } catch (Exception e) {
            return "无法确定字体类型";
        }
    }
    
    /**
     * 检查PDF文件大小和基本信息
     */
    public static void inspectPdfBasicInfo(String pdfPath) {
        System.out.println("=== PDF基本信息 ===");
        
        try {
            java.io.File pdfFile = new java.io.File(pdfPath);
            if (pdfFile.exists()) {
                long fileSize = pdfFile.length();
                System.out.println("文件大小: " + formatSize(fileSize));
                
                PdfReader reader = new PdfReader(pdfPath);
                System.out.println("PDF版本: " + reader.getPdfVersion());
                System.out.println("页数: " + reader.getNumberOfPages());
                System.out.println("是否加密: " + reader.isEncrypted());
                
                AcroFields fields = reader.getAcroFields();
                if (fields != null) {
                    System.out.println("表单字段数: " + fields.getFields().size());
                }
                
                reader.close();
            } else {
                System.out.println("文件不存在: " + pdfPath);
            }
            
        } catch (Exception e) {
            System.out.println("检查失败: " + e.getMessage());
        }
        
        System.out.println("==================");
    }
    
    /**
     * 对比模板和生成的PDF
     */
    public static void comparePdfs(String templatePath, String generatedPath) {
        System.out.println("=== PDF对比分析 ===");
        
        System.out.println("模板PDF:");
        inspectPdfBasicInfo(templatePath);
        
        System.out.println("生成PDF:");
        inspectPdfBasicInfo(generatedPath);
        
        // 计算大小差异
        try {
            java.io.File templateFile = new java.io.File(templatePath);
            java.io.File generatedFile = new java.io.File(generatedPath);
            
            if (templateFile.exists() && generatedFile.exists()) {
                long templateSize = templateFile.length();
                long generatedSize = generatedFile.length();
                long difference = generatedSize - templateSize;
                
                System.out.println("大小对比:");
                System.out.println("  模板: " + formatSize(templateSize));
                System.out.println("  生成: " + formatSize(generatedSize));
                System.out.println("  差异: " + formatSize(Math.abs(difference)) + 
                                 (difference > 0 ? " (增加)" : " (减少)"));
                
                if (difference > 0) {
                    double increasePercent = (double) difference / templateSize * 100;
                    System.out.println("  增加比例: " + String.format("%.1f%%", increasePercent));
                }
            }
        } catch (Exception e) {
            System.out.println("对比失败: " + e.getMessage());
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
     * 主测试方法
     */
    public static void main(String[] args) {
        // 替换为你的PDF路径
        String templatePath = "template.pdf";
        String generatedPath = "generated.pdf";
        
        // 检查模板字体
        inspectPdfFonts(templatePath);
        
        // 如果有生成的PDF，进行对比
        if (new java.io.File(generatedPath).exists()) {
            comparePdfs(templatePath, generatedPath);
        }
    }
}
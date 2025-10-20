package com.blog.web.util;

import com.itextpdf.text.pdf.BaseFont;
import java.io.File;

/**
 * 最优字体解决方案
 * 
 * 基于测试结果的最佳实践
 */
public class OptimalFontSolution {
    
    /**
     * 获取最优的中文字体（确保小文件）
     * 
     * 策略：
     * 1. 优先使用项目中的 Noto Serif CJK（不嵌入）
     * 2. 回退到系统字体 STSong-Light
     * 3. 最终回退到 Helvetica
     * 
     * @return BaseFont 对象，确保PDF文件小于1MB
     */
    public static BaseFont getOptimalChineseFont() throws Exception {
        
        // 方案1：使用项目字体文件（不嵌入）
        String[] projectFonts = {
            "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
            "src/main/resources/fonts/NotoSerifCJKsc-Light.otf"
        };
        
        for (String fontPath : projectFonts) {
            File fontFile = new File(fontPath);
            if (fontFile.exists()) {
                try {
                    // 关键：使用 NOT_EMBEDDED 确保文件小
                    BaseFont font = BaseFont.createFont(
                        fontPath, 
                        BaseFont.IDENTITY_H, 
                        BaseFont.NOT_EMBEDDED  // 确保不嵌入
                    );
                    System.out.println("✅ 使用项目字体（不嵌入）: " + fontPath);
                    return font;
                } catch (Exception e) {
                    System.out.println("项目字体失败: " + fontPath + " - " + e.getMessage());
                }
            }
        }
        
        // 方案2：使用系统字体
        String[] systemFonts = {
            "STSong-Light",
            "SimSun"
        };
        
        for (String fontName : systemFonts) {
            try {
                BaseFont font = BaseFont.createFont(
                    fontName, 
                    "UniGB-UCS2-H", 
                    BaseFont.NOT_EMBEDDED
                );
                System.out.println("✅ 使用系统字体: " + fontName);
                return font;
            } catch (Exception e) {
                System.out.println("系统字体失败: " + fontName + " - " + e.getMessage());
            }
        }
        
        // 方案3：最终回退
        BaseFont font = BaseFont.createFont(
            BaseFont.HELVETICA, 
            BaseFont.CP1252, 
            BaseFont.NOT_EMBEDDED
        );
        System.out.println("⚠️  使用回退字体: Helvetica");
        return font;
    }
    
    /**
     * 验证字体选择是否会产生小文件
     */
    public static void validateFontChoice() {
        try {
            System.out.println("=== 验证最优字体选择 ===");
            
            BaseFont font = getOptimalChineseFont();
            System.out.println("选择的字体: " + font.getPostscriptFontName());
            
            // 创建测试PDF验证文件大小
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream("optimal_font_test.pdf"));
            document.open();
            
            com.itextpdf.text.Font pdfFont = new com.itextpdf.text.Font(font, 12);
            document.add(new com.itextpdf.text.Paragraph("最优字体测试", pdfFont));
            document.add(new com.itextpdf.text.Paragraph("这是中文测试：你好世界！", pdfFont));
            document.add(new com.itextpdf.text.Paragraph("This is English test: Hello World!", pdfFont));
            
            document.close();
            
            File testFile = new File("optimal_font_test.pdf");
            long sizeKB = testFile.length() / 1024;
            
            System.out.println("测试PDF大小: " + testFile.length() + " bytes (" + sizeKB + " KB)");
            
            if (sizeKB < 500) {
                System.out.println("✅ 字体选择优秀：文件大小 < 500KB");
            } else if (sizeKB < 2000) {
                System.out.println("✅ 字体选择良好：文件大小 < 2MB");
            } else {
                System.out.println("❌ 字体选择有问题：文件过大");
            }
            
        } catch (Exception e) {
            System.out.println("验证失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        validateFontChoice();
    }
}
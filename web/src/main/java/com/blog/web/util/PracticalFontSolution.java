package com.blog.web.util;

import com.itextpdf.text.pdf.BaseFont;
import java.io.File;

/**
 * 实用字体解决方案
 * 
 * 策略：
 * 1. 优先使用项目中的 Noto Serif CJK 字体文件（不嵌入）
 * 2. 回退到 STSong-Light（系统字体）
 * 3. 最终回退到 Helvetica
 */
public class PracticalFontSolution {
    
    private static final String[] FONT_PATHS = {
        "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
        "src/main/resources/fonts/NotoSerifCJKsc-Light.otf",
        "src/main/resources/fonts/NotoSerifCJKsc-Bold.otf"
    };
    
    private static final String[] SYSTEM_FONTS = {
        "STSong-Light",
        "SimSun", 
        "PingFang SC",
        "Hiragino Sans GB"
    };
    
    /**
     * 获取最佳可用的中文字体（不嵌入）
     * 
     * @return BaseFont 对象
     */
    public static BaseFont getBestChineseFont() {
        // 方案1：尝试项目字体文件
        for (String fontPath : FONT_PATHS) {
            try {
                File fontFile = new File(fontPath);
                if (fontFile.exists()) {
                    BaseFont font = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                    System.out.println("✅ 使用项目字体: " + fontPath);
                    System.out.println("   PostScript名称: " + font.getPostscriptFontName());
                    return font;
                }
            } catch (Exception e) {
                System.out.println("项目字体失败: " + fontPath + " - " + e.getMessage());
            }
        }
        
        // 方案2：尝试系统字体
        for (String fontName : SYSTEM_FONTS) {
            try {
                BaseFont font = BaseFont.createFont(fontName, "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                System.out.println("✅ 使用系统字体: " + fontName);
                System.out.println("   PostScript名称: " + font.getPostscriptFontName());
                return font;
            } catch (Exception e) {
                System.out.println("系统字体失败: " + fontName + " - " + e.getMessage());
            }
        }
        
        // 方案3：最终回退
        try {
            BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            System.out.println("⚠️  使用回退字体: Helvetica（中文可能显示为方块）");
            return font;
        } catch (Exception e) {
            throw new RuntimeException("无法创建任何字体", e);
        }
    }
    
    /**
     * 获取轻量级中文字体方案
     * 
     * 特点：
     * - 不嵌入字体，文件小
     * - 依赖查看者系统有对应字体
     * - 适合内部使用或已知环境
     */
    public static BaseFont getLightweightChineseFont() throws Exception {
        return getBestChineseFont();
    }
    
    /**
     * 检查字体兼容性
     */
    public static void checkFontCompatibility() {
        System.out.println("=== 字体兼容性检查 ===");
        
        System.out.println("\n1. 项目字体文件检查:");
        for (String fontPath : FONT_PATHS) {
            File fontFile = new File(fontPath);
            if (fontFile.exists()) {
                System.out.println("✅ " + fontPath + " (大小: " + fontFile.length() + " bytes)");
            } else {
                System.out.println("❌ " + fontPath + " (不存在)");
            }
        }
        
        System.out.println("\n2. 系统字体检查:");
        for (String fontName : SYSTEM_FONTS) {
            try {
                BaseFont font = BaseFont.createFont(fontName, "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                System.out.println("✅ " + fontName + " -> " + font.getPostscriptFontName());
            } catch (Exception e) {
                System.out.println("❌ " + fontName + " (不可用)");
            }
        }
        
        System.out.println("\n3. 推荐方案:");
        try {
            BaseFont bestFont = getBestChineseFont();
            System.out.println("✅ 最佳字体: " + bestFont.getPostscriptFontName());
            System.out.println("   建议：使用此字体创建PDF，文件小且兼容性好");
            System.out.println("   注意：查看者系统需要有对应字体才能正确显示");
        } catch (Exception e) {
            System.out.println("❌ 无法确定最佳字体: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        checkFontCompatibility();
    }
}
package com.blog.web.util;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * 快速字体嵌入检查工具
 * 专门用于快速判断PDF是否嵌入了字体
 */
public class QuickFontEmbeddingChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(QuickFontEmbeddingChecker.class);
    
    /**
     * 快速检查PDF字体嵌入状态
     */
    public static boolean quickCheckFontEmbedding(String pdfPath) {
        logger.info("快速检查PDF字体嵌入: {}", pdfPath);
        
        PdfReader reader = null;
        boolean hasEmbeddedFonts = false;
        int totalFonts = 0;
        int embeddedFonts = 0;
        
        try {
            reader = new PdfReader(pdfPath);
            
            // 检查所有页面的字体
            for (int pageNum = 1; pageNum <= reader.getNumberOfPages(); pageNum++) {
                PdfDictionary pageDict = reader.getPageN(pageNum);
                PdfDictionary resources = pageDict.getAsDict(PdfName.RESOURCES);
                
                if (resources != null) {
                    PdfDictionary fonts = resources.getAsDict(PdfName.FONT);
                    
                    if (fonts != null) {
                        Set<PdfName> fontNames = fonts.getKeys();
                        
                        for (PdfName fontName : fontNames) {
                            PdfDictionary font = fonts.getAsDict(fontName);
                            if (font != null) {
                                totalFonts++;
                                
                                if (isFontEmbedded(font)) {
                                    embeddedFonts++;
                                    hasEmbeddedFonts = true;
                                    
                                    // 获取字体信息
                                    PdfName baseFontName = font.getAsName(PdfName.BASEFONT);
                                    logger.info("发现嵌入字体: {} ({})", fontName, baseFontName);
                                } else {
                                    PdfName baseFontName = font.getAsName(PdfName.BASEFONT);
                                    logger.info("发现非嵌入字体: {} ({})", fontName, baseFontName);
                                }
                            }
                        }
                    }
                }
            }
            
            // 输出结果
            logger.info("=== 字体嵌入检查结果 ===");
            logger.info("总字体数: {}", totalFonts);
            logger.info("嵌入字体数: {}", embeddedFonts);
            logger.info("非嵌入字体数: {}", totalFonts - embeddedFonts);
            logger.info("是否包含嵌入字体: {}", hasEmbeddedFonts ? "✅ 是" : "❌ 否");
            
            // 文件大小信息
            java.io.File file = new java.io.File(pdfPath);
            if (file.exists()) {
                long sizeKB = file.length() / 1024;
                logger.info("文件大小: {} KB", sizeKB);
                
                if (!hasEmbeddedFonts && sizeKB > 1000) {
                    logger.warn("⚠️  文件较大但未检测到嵌入字体，可能存在其他大型资源");
                }
            }
            
        } catch (IOException e) {
            logger.error("读取PDF文件失败: {}", e.getMessage());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        
        return hasEmbeddedFonts;
    }
    
    /**
     * 检查单个字体是否嵌入
     */
    private static boolean isFontEmbedded(PdfDictionary font) {
        PdfDictionary fontDescriptor = font.getAsDict(PdfName.FONTDESCRIPTOR);
        
        if (fontDescriptor == null) {
            // 没有字体描述符，通常是标准字体，未嵌入
            return false;
        }
        
        // 检查字体文件流
        boolean hasEmbeddedFont = 
            fontDescriptor.get(PdfName.FONTFILE) != null ||
            fontDescriptor.get(PdfName.FONTFILE2) != null ||
            fontDescriptor.get(PdfName.FONTFILE3) != null;
        
        return hasEmbeddedFont;
    }
    
    /**
     * 批量检查多个PDF文件
     */
    public static void batchCheckFontEmbedding(String... pdfPaths) {
        logger.info("=== 批量检查PDF字体嵌入 ===");
        
        for (String pdfPath : pdfPaths) {
            logger.info("\n检查文件: {}", pdfPath);
            boolean hasEmbedded = quickCheckFontEmbedding(pdfPath);
            logger.info("结果: {}", hasEmbedded ? "包含嵌入字体" : "无嵌入字体");
        }
        
        logger.info("\n=== 批量检查完成 ===");
    }
    
    /**
     * 主方法，用于命令行测试
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            batchCheckFontEmbedding(args);
        } else {
            // 测试默认文件
            String testFile = "eg.10004730+测韦欣+测新字体 (8).pdf";
            logger.info("测试文件: {}", testFile);
            quickCheckFontEmbedding(testFile);
        }
    }
}
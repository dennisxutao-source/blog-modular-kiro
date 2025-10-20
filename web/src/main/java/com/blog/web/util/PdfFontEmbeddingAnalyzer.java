package com.blog.web.util;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * PDF字体嵌入分析工具
 * 专门用于检查PDF文件中的字体是否真正嵌入
 */
public class PdfFontEmbeddingAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(PdfFontEmbeddingAnalyzer.class);
    
    /**
     * 分析PDF文件的字体嵌入情况
     */
    public static void analyzePdfFontEmbedding(String pdfPath) {
        logger.info("=== 开始分析PDF字体嵌入情况 ===");
        logger.info("PDF文件: {}", pdfPath);
        
        PdfReader reader = null;
        
        try {
            reader = new PdfReader(pdfPath);
            
            // 基本信息
            logger.info("PDF页数: {}", reader.getNumberOfPages());
            logger.info("PDF版本: {}", reader.getPdfVersion());
            
            // 分析字体
            analyzeFonts(reader);
            
            // 分析文件大小
            analyzeFileSize(pdfPath);
            
        } catch (IOException e) {
            logger.error("读取PDF文件失败: {}", e.getMessage());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        
        logger.info("=== PDF字体嵌入分析完成 ===");
    }
    
    /**
     * 分析PDF中的字体
     */
    private static void analyzeFonts(PdfReader reader) {
        logger.info("\n--- 字体分析 ---");
        
        try {
            // 获取所有页面的资源
            for (int pageNum = 1; pageNum <= reader.getNumberOfPages(); pageNum++) {
                logger.info("分析第 {} 页字体", pageNum);
                
                PdfDictionary pageDict = reader.getPageN(pageNum);
                PdfDictionary resources = pageDict.getAsDict(PdfName.RESOURCES);
                
                if (resources != null) {
                    PdfDictionary fonts = resources.getAsDict(PdfName.FONT);
                    
                    if (fonts != null) {
                        analyzeFontDictionary(fonts, pageNum);
                    } else {
                        logger.info("第 {} 页没有字体资源", pageNum);
                    }
                } else {
                    logger.info("第 {} 页没有资源字典", pageNum);
                }
            }
            
            // 分析全局字体资源
            analyzeGlobalFonts(reader);
            
        } catch (Exception e) {
            logger.error("分析字体失败: {}", e.getMessage());
        }
    }
    
    /**
     * 分析字体字典
     */
    private static void analyzeFontDictionary(PdfDictionary fonts, int pageNum) {
        Set<PdfName> fontNames = fonts.getKeys();
        
        logger.info("第 {} 页发现 {} 个字体", pageNum, fontNames.size());
        
        for (PdfName fontName : fontNames) {
            PdfDictionary font = fonts.getAsDict(fontName);
            if (font != null) {
                analyzeSingleFont(fontName, font, pageNum);
            }
        }
    }
    
    /**
     * 分析单个字体
     */
    private static void analyzeSingleFont(PdfName fontName, PdfDictionary font, int pageNum) {
        logger.info("\n字体名称: {}", fontName);
        
        // 字体类型
        PdfName subtype = font.getAsName(PdfName.SUBTYPE);
        if (subtype != null) {
            logger.info("字体类型: {}", subtype);
        }
        
        // 基础字体名
        PdfName baseFontName = font.getAsName(PdfName.BASEFONT);
        if (baseFontName != null) {
            logger.info("基础字体: {}", baseFontName);
        }
        
        // 检查是否嵌入
        boolean isEmbedded = checkFontEmbedding(font);
        logger.info("字体嵌入状态: {}", isEmbedded ? "✅ 已嵌入" : "❌ 未嵌入");
        
        // 字体描述符
        PdfDictionary fontDescriptor = font.getAsDict(PdfName.FONTDESCRIPTOR);
        if (fontDescriptor != null) {
            analyzeFontDescriptor(fontDescriptor);
        } else {
            logger.warn("缺少字体描述符 - 可能是标准字体");
        }
        
        // 编码信息
        PdfObject encoding = font.get(PdfName.ENCODING);
        if (encoding != null) {
            logger.info("字体编码: {}", encoding);
        }
        
        // ToUnicode映射
        PdfObject toUnicode = font.get(PdfName.TOUNICODE);
        if (toUnicode != null) {
            logger.info("ToUnicode映射: 存在");
        }
    }
    
    /**
     * 检查字体是否嵌入
     */
    private static boolean checkFontEmbedding(PdfDictionary font) {
        PdfDictionary fontDescriptor = font.getAsDict(PdfName.FONTDESCRIPTOR);
        
        if (fontDescriptor == null) {
            // 没有字体描述符，通常是标准字体，未嵌入
            return false;
        }
        
        // 检查字体文件流
        PdfObject fontFile = fontDescriptor.get(PdfName.FONTFILE);
        PdfObject fontFile2 = fontDescriptor.get(PdfName.FONTFILE2);
        PdfObject fontFile3 = fontDescriptor.get(PdfName.FONTFILE3);
        
        boolean hasEmbeddedFont = (fontFile != null) || (fontFile2 != null) || (fontFile3 != null);
        
        if (hasEmbeddedFont) {
            logger.info("发现嵌入字体文件:");
            if (fontFile != null) logger.info("  - FontFile (Type1)");
            if (fontFile2 != null) logger.info("  - FontFile2 (TrueType)");
            if (fontFile3 != null) logger.info("  - FontFile3 (OpenType/CFF)");
        }
        
        return hasEmbeddedFont;
    }
    
    /**
     * 分析字体描述符
     */
    private static void analyzeFontDescriptor(PdfDictionary fontDescriptor) {
        // 字体名称
        PdfName fontName = fontDescriptor.getAsName(PdfName.FONTNAME);
        if (fontName != null) {
            logger.info("描述符字体名: {}", fontName);
        }
        
        // 字体标志
        PdfObject flags = fontDescriptor.get(PdfName.FLAGS);
        if (flags != null) {
            logger.info("字体标志: {}", flags);
        }
        
        // 字体边界框
        PdfArray fontBBox = fontDescriptor.getAsArray(PdfName.FONTBBOX);
        if (fontBBox != null) {
            logger.info("字体边界框: {}", fontBBox);
        }
        
        // 字符集 (某些iText版本可能没有CHARSET常量)
        try {
            PdfObject charset = fontDescriptor.get(new PdfName("CharSet"));
            if (charset != null) {
                logger.info("字符集: {}", charset);
            }
        } catch (Exception e) {
            // 忽略字符集检查错误
        }
    }
    
    /**
     * 分析全局字体资源
     */
    private static void analyzeGlobalFonts(PdfReader reader) {
        logger.info("\n--- 全局字体资源分析 ---");
        
        try {
            // 获取文档目录
            PdfDictionary catalog = reader.getCatalog();
            
            // 检查是否有全局字体资源
            // 这里可以扩展更多的全局字体检查逻辑
            
            logger.info("全局字体资源检查完成");
            
        } catch (Exception e) {
            logger.error("分析全局字体资源失败: {}", e.getMessage());
        }
    }
    
    /**
     * 分析文件大小
     */
    private static void analyzeFileSize(String pdfPath) {
        logger.info("\n--- 文件大小分析 ---");
        
        try {
            java.io.File file = new java.io.File(pdfPath);
            if (file.exists()) {
                long sizeBytes = file.length();
                long sizeKB = sizeBytes / 1024;
                long sizeMB = sizeBytes / (1024 * 1024);
                
                logger.info("文件大小: {} bytes ({} KB / {} MB)", sizeBytes, sizeKB, sizeMB);
                
                // 根据文件大小推测字体嵌入情况
                if (sizeMB > 10) {
                    logger.warn("文件较大 ({} MB)，可能包含嵌入字体", sizeMB);
                } else if (sizeKB > 500) {
                    logger.info("文件中等大小 ({} KB)，可能部分嵌入字体", sizeKB);
                } else {
                    logger.info("文件较小 ({} KB)，可能未嵌入字体", sizeKB);
                }
            }
        } catch (Exception e) {
            logger.error("分析文件大小失败: {}", e.getMessage());
        }
    }
    
    /**
     * 主方法，用于测试
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            analyzePdfFontEmbedding(args[0]);
        } else {
            // 测试文件
            String testPdfPath = "eg.10004730+测韦欣+测新字体 (8).pdf";
            analyzePdfFontEmbedding(testPdfPath);
        }
    }
}
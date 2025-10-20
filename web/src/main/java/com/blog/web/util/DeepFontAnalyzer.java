package com.blog.web.util;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * 深度字体分析工具
 * 详细检查PDF中字体的嵌入情况
 */
public class DeepFontAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(DeepFontAnalyzer.class);
    
    /**
     * 深度分析PDF字体
     */
    public static void deepAnalyzePdfFonts(String pdfPath) {
        logger.info("=== 深度PDF字体分析 ===");
        logger.info("文件: {}", pdfPath);
        
        PdfReader reader = null;
        
        try {
            reader = new PdfReader(pdfPath);
            logger.info("PDF页数: {}", reader.getNumberOfPages());
            
            // 分析每一页的字体
            for (int pageNum = 1; pageNum <= reader.getNumberOfPages(); pageNum++) {
                logger.info("\n--- 分析第 {} 页 ---", pageNum);
                analyzePageFonts(reader, pageNum);
            }
            
        } catch (IOException e) {
            logger.error("读取PDF失败", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    /**
     * 分析页面字体
     */
    private static void analyzePageFonts(PdfReader reader, int pageNum) {
        try {
            PdfDictionary pageDict = reader.getPageN(pageNum);
            PdfDictionary resources = pageDict.getAsDict(PdfName.RESOURCES);
            
            if (resources != null) {
                PdfDictionary fonts = resources.getAsDict(PdfName.FONT);
                
                if (fonts != null) {
                    Set<PdfName> fontNames = fonts.getKeys();
                    logger.info("发现 {} 个字体", fontNames.size());
                    
                    for (PdfName fontName : fontNames) {
                        PdfDictionary font = fonts.getAsDict(fontName);
                        if (font != null) {
                            analyzeFont(fontName, font);
                        }
                    }
                } else {
                    logger.info("该页没有字体资源");
                }
            } else {
                logger.info("该页没有资源字典");
            }
        } catch (Exception e) {
            logger.error("分析第 {} 页字体失败", pageNum, e);
        }
    }
    
    /**
     * 详细分析单个字体
     */
    private static void analyzeFont(PdfName fontName, PdfDictionary font) {
        logger.info("\n字体: {}", fontName);
        
        // 基本信息
        PdfName subtype = font.getAsName(PdfName.SUBTYPE);
        PdfName baseFontName = font.getAsName(PdfName.BASEFONT);
        
        logger.info("  类型: {}", subtype);
        logger.info("  基础字体: {}", baseFontName);
        
        // 字体描述符
        PdfDictionary fontDescriptor = font.getAsDict(PdfName.FONTDESCRIPTOR);
        if (fontDescriptor != null) {
            logger.info("  ✅ 有字体描述符");
            analyzeFontDescriptor(fontDescriptor);
        } else {
            logger.info("  ❌ 无字体描述符（可能是标准字体）");
        }
        
        // 编码
        PdfObject encoding = font.get(PdfName.ENCODING);
        if (encoding != null) {
            logger.info("  编码: {}", encoding);
        }
        
        // ToUnicode
        PdfObject toUnicode = font.get(PdfName.TOUNICODE);
        if (toUnicode != null) {
            logger.info("  ToUnicode: 存在");
        }
        
        // 宽度信息
        PdfObject widths = font.get(PdfName.WIDTHS);
        if (widths != null) {
            logger.info("  宽度信息: 存在");
        }
        
        // CIDFont信息（对于Type0字体）
        if (PdfName.TYPE0.equals(subtype)) {
            analyzeCIDFont(font);
        }
    }
    
    /**
     * 分析字体描述符
     */
    private static void analyzeFontDescriptor(PdfDictionary fontDescriptor) {
        // 字体名称
        PdfName fontName = fontDescriptor.getAsName(PdfName.FONTNAME);
        if (fontName != null) {
            logger.info("    描述符字体名: {}", fontName);
        }
        
        // 检查字体文件
        boolean hasEmbeddedFont = false;
        
        PdfObject fontFile = fontDescriptor.get(PdfName.FONTFILE);
        if (fontFile != null) {
            logger.info("    ✅ FontFile (Type1): 存在");
            hasEmbeddedFont = true;
            analyzeEmbeddedFontStream(fontFile, "Type1");
        }
        
        PdfObject fontFile2 = fontDescriptor.get(PdfName.FONTFILE2);
        if (fontFile2 != null) {
            logger.info("    ✅ FontFile2 (TrueType): 存在");
            hasEmbeddedFont = true;
            analyzeEmbeddedFontStream(fontFile2, "TrueType");
        }
        
        PdfObject fontFile3 = fontDescriptor.get(PdfName.FONTFILE3);
        if (fontFile3 != null) {
            logger.info("    ✅ FontFile3 (OpenType/CFF): 存在");
            hasEmbeddedFont = true;
            analyzeEmbeddedFontStream(fontFile3, "OpenType/CFF");
        }
        
        if (!hasEmbeddedFont) {
            logger.warn("    ❌ 没有找到嵌入的字体文件");
        }
        
        // 字体标志
        PdfObject flags = fontDescriptor.get(PdfName.FLAGS);
        if (flags != null) {
            logger.info("    字体标志: {}", flags);
        }
    }
    
    /**
     * 分析嵌入字体流
     */
    private static void analyzeEmbeddedFontStream(PdfObject fontFileObj, String type) {
        try {
            logger.info("      字体类型: {}", type);
            logger.info("      字体对象类型: {}", fontFileObj.getClass().getSimpleName());
            
            // 尝试获取流信息
            if (fontFileObj.isStream()) {
                logger.info("      ✅ 这是一个流对象");
                
                // 尝试获取长度信息
                PdfDictionary streamDict = (PdfDictionary) fontFileObj;
                PdfObject length = streamDict.get(PdfName.LENGTH);
                if (length != null) {
                    logger.info("      流长度: {}", length);
                }
                
                // 检查压缩
                PdfObject filter = streamDict.get(PdfName.FILTER);
                if (filter != null) {
                    logger.info("      压缩方式: {}", filter);
                }
            } else {
                logger.info("      ❌ 不是流对象");
            }
        } catch (Exception e) {
            logger.error("      分析字体流失败", e);
        }
    }
    
    /**
     * 分析CIDFont（用于Type0字体）
     */
    private static void analyzeCIDFont(PdfDictionary font) {
        PdfObject descendantFonts = font.get(PdfName.DESCENDANTFONTS);
        if (descendantFonts != null) {
            logger.info("  CIDFont信息: 存在");
            // 这里可以进一步分析CIDFont的详细信息
        }
    }
    
    /**
     * 主方法
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            for (String pdfPath : args) {
                deepAnalyzePdfFonts(pdfPath);
                System.out.println("\n" + "=".repeat(80) + "\n");
            }
        } else {
            // 测试文件
            String[] testFiles = {
                "../test-pdf.pdf",
                "../embedded-text-pdf.pdf"
            };
            
            for (String testFile : testFiles) {
                deepAnalyzePdfFonts(testFile);
                System.out.println("\n" + "=".repeat(80) + "\n");
            }
        }
    }
}
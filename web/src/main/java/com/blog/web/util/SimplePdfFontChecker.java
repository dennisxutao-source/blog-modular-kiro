package com.blog.web.util;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * 简化的PDF字体检查工具
 * 专门用于快速检查PDF是否嵌入字体
 */
public class SimplePdfFontChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(SimplePdfFontChecker.class);
    
    /**
     * 检查PDF字体嵌入情况
     */
    public static void checkPdfFonts(String pdfPath) {
        logger.info("=== PDF字体检查 ===");
        logger.info("文件: {}", pdfPath);
        
        // 检查文件是否存在
        java.io.File file = new java.io.File(pdfPath);
        if (!file.exists()) {
            logger.error("❌ 文件不存在: {}", pdfPath);
            return;
        }
        
        logger.info("✅ 文件存在，大小: {} KB", file.length() / 1024);
        
        PdfReader reader = null;
        boolean hasEmbeddedFonts = false;
        int totalFonts = 0;
        
        try {
            reader = new PdfReader(pdfPath);
            logger.info("PDF页数: {}", reader.getNumberOfPages());
            
            // 检查每一页的字体
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
                                
                                // 获取字体基本信息
                                PdfName baseFontName = font.getAsName(PdfName.BASEFONT);
                                PdfName subtype = font.getAsName(PdfName.SUBTYPE);
                                
                                logger.info("字体 {}: {} (类型: {})", fontName, baseFontName, subtype);
                                
                                // 检查是否嵌入
                                boolean isEmbedded = isEmbeddedFont(font);
                                if (isEmbedded) {
                                    hasEmbeddedFonts = true;
                                    logger.info("  ✅ 已嵌入");
                                } else {
                                    logger.info("  ❌ 未嵌入");
                                }
                            }
                        }
                    }
                }
            }
            
            // 输出总结
            logger.info("\n=== 检查结果 ===");
            logger.info("总字体数: {}", totalFonts);
            logger.info("是否包含嵌入字体: {}", hasEmbeddedFonts ? "✅ 是" : "❌ 否");
            
            if (!hasEmbeddedFonts) {
                logger.warn("\n⚠️  警告: 该PDF未嵌入字体");
                logger.warn("可能的问题:");
                logger.warn("- 在没有相应字体的系统上显示异常");
                logger.warn("- 中文可能显示为方框或乱码");
                logger.warn("- 依赖目标系统安装的字体");
                
                logger.info("\n💡 建议:");
                logger.info("- 使用 EmbeddedFontPdfFiller 重新生成PDF");
                logger.info("- 确保设置 BaseFont.EMBEDDED 参数");
            } else {
                logger.info("\n✅ 该PDF包含嵌入字体，兼容性良好");
            }
            
        } catch (IOException e) {
            logger.error("读取PDF失败: {}", e.getMessage());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    /**
     * 简单检查字体是否嵌入
     */
    private static boolean isEmbeddedFont(PdfDictionary font) {
        PdfDictionary fontDescriptor = font.getAsDict(PdfName.FONTDESCRIPTOR);
        
        if (fontDescriptor == null) {
            return false; // 没有字体描述符，通常是标准字体
        }
        
        // 检查是否有字体文件流
        return fontDescriptor.get(PdfName.FONTFILE) != null ||
               fontDescriptor.get(PdfName.FONTFILE2) != null ||
               fontDescriptor.get(PdfName.FONTFILE3) != null;
    }
    
    /**
     * 主方法
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            for (String pdfPath : args) {
                checkPdfFonts(pdfPath);
                System.out.println(); // 空行分隔
            }
        } else {
            System.out.println("用法: java SimplePdfFontChecker <PDF文件路径>");
            System.out.println("示例: java SimplePdfFontChecker test.pdf");
        }
    }
}
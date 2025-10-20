package com.blog.web.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 直接在PDF上绘制嵌入字体文本的工具
 * 不依赖表单字段，直接在指定位置绘制文本
 */
public class DirectTextEmbeddedPdfCreator {
    
    private static final Logger logger = LoggerFactory.getLogger(DirectTextEmbeddedPdfCreator.class);
    
    /**
     * 在PDF上直接绘制嵌入字体文本
     */
    public static void addEmbeddedTextToPdf(String inputPdfPath, String outputPdfPath) {
        logger.info("=== 直接绘制嵌入字体文本 ===");
        logger.info("输入PDF: {}", inputPdfPath);
        logger.info("输出PDF: {}", outputPdfPath);
        
        PdfReader reader = null;
        PdfStamper stamper = null;
        
        try {
            // 1. 创建嵌入字体
            BaseFont embeddedFont = createEmbeddedFont();
            Font font = new Font(embeddedFont, 12);
            
            // 2. 读取原PDF
            reader = new PdfReader(inputPdfPath);
            logger.info("原PDF页数: {}", reader.getNumberOfPages());
            
            // 3. 创建stamper
            stamper = new PdfStamper(reader, new FileOutputStream(outputPdfPath));
            
            // 4. 在每一页上添加嵌入字体文本
            for (int pageNum = 1; pageNum <= reader.getNumberOfPages(); pageNum++) {
                addTextToPage(stamper, pageNum, embeddedFont);
            }
            
            // 5. 设置表单不可编辑
            stamper.setFormFlattening(true);
            
            logger.info("✅ 嵌入字体文本添加完成");
            
        } catch (Exception e) {
            logger.error("❌ 添加嵌入字体文本失败", e);
            throw new RuntimeException("处理失败", e);
        } finally {
            // 清理资源
            if (stamper != null) {
                try {
                    stamper.close();
                } catch (Exception e) {
                    logger.error("关闭stamper失败", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    logger.error("关闭reader失败", e);
                }
            }
        }
        
        // 检查文件大小
        checkFileSize(outputPdfPath);
    }
    
    /**
     * 创建嵌入字体
     */
    private static BaseFont createEmbeddedFont() throws DocumentException, IOException {
        logger.info("创建嵌入字体...");
        
        try {
            // 从classpath加载字体
            String fontResourcePath = "fonts/NotoSerifCJKsc-Regular.otf";
            java.io.InputStream fontStream = DirectTextEmbeddedPdfCreator.class.getClassLoader().getResourceAsStream(fontResourcePath);
            
            if (fontStream != null) {
                byte[] fontBytes = readStreamToBytes(fontStream);
                logger.info("字体文件大小: {} KB", fontBytes.length / 1024);
                
                BaseFont font = BaseFont.createFont(
                        fontResourcePath,
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED, // 关键：嵌入字体
                        true,
                        fontBytes,
                        null
                );
                
                logger.info("✅ 嵌入字体创建成功: {}", font.getPostscriptFontName());
                return font;
            } else {
                throw new RuntimeException("字体资源不存在: " + fontResourcePath);
            }
        } catch (Exception e) {
            logger.error("创建嵌入字体失败", e);
            throw e;
        }
    }
    
    /**
     * 在指定页面添加文本
     */
    private static void addTextToPage(PdfStamper stamper, int pageNum, BaseFont embeddedFont) 
            throws IOException, DocumentException {
        
        logger.info("在第 {} 页添加嵌入字体文本", pageNum);
        
        PdfContentByte canvas = stamper.getOverContent(pageNum);
        
        // 设置嵌入字体
        canvas.beginText();
        canvas.setFontAndSize(embeddedFont, 12);
        
        // 在页面上添加一些测试文本（你可以根据需要调整位置）
        float x = 50; // 左边距
        float y = 750; // 从页面顶部开始
        
        // 添加多行测试文本
        String[] testTexts = {
            "✅ 这是嵌入字体测试文本",
            "✅ 字体: NotoSerifCJKsc-Regular (已嵌入)",
            "✅ 在任何系统上都能正确显示中文",
            "✅ 测试用户: 测韦欣 (ID: 10004730)",
            "✅ 生成时间: 2025-01-18"
        };
        
        for (int i = 0; i < testTexts.length; i++) {
            canvas.setTextMatrix(x, y - (i * 20)); // 每行间距20
            canvas.showText(testTexts[i]);
        }
        
        canvas.endText();
        
        logger.debug("第 {} 页文本添加完成", pageNum);
    }
    
    /**
     * 读取InputStream到字节数组
     */
    private static byte[] readStreamToBytes(java.io.InputStream inputStream) throws IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        
        inputStream.close();
        return buffer.toByteArray();
    }
    
    /**
     * 检查文件大小
     */
    private static void checkFileSize(String filePath) {
        try {
            java.io.File file = new java.io.File(filePath);
            if (file.exists()) {
                long sizeKB = file.length() / 1024;
                long sizeMB = file.length() / (1024 * 1024);
                
                logger.info("=== 文件大小检查 ===");
                logger.info("文件大小: {} KB ({} MB)", sizeKB, sizeMB);
                
                if (sizeMB > 20) {
                    logger.info("✅ 文件较大 ({} MB)，可能包含嵌入字体", sizeMB);
                } else if (sizeKB > 1000) {
                    logger.info("✅ 文件中等大小 ({} KB)，可能包含嵌入字体", sizeKB);
                } else {
                    logger.warn("⚠️  文件较小 ({} KB)，可能未成功嵌入字体", sizeKB);
                }
            }
        } catch (Exception e) {
            logger.error("检查文件大小失败", e);
        }
    }
    
    /**
     * 主方法
     */
    public static void main(String[] args) {
        String inputPdf = "../test-pdf.pdf";
        String outputPdf = "../embedded-text-pdf.pdf";
        
        try {
            addEmbeddedTextToPdf(inputPdf, outputPdf);
            
            // 立即验证结果
            logger.info("\n=== 验证新生成的PDF ===");
            SimplePdfFontChecker.checkPdfFonts(outputPdf);
            
        } catch (Exception e) {
            logger.error("处理失败", e);
        }
    }
}
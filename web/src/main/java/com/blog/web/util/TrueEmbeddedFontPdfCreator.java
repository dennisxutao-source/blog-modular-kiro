package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 真正嵌入字体的PDF创建工具
 * 从零开始创建PDF，确保字体真正嵌入
 */
public class TrueEmbeddedFontPdfCreator {
    
    private static final Logger logger = LoggerFactory.getLogger(TrueEmbeddedFontPdfCreator.class);
    
    /**
     * 创建真正嵌入字体的PDF
     */
    public static void createTrueEmbeddedFontPdf(String outputPath) {
        logger.info("=== 创建真正嵌入字体的PDF ===");
        logger.info("输出文件: {}", outputPath);
        
        Document document = null;
        PdfWriter writer = null;
        
        try {
            // 1. 创建文档
            document = new Document(PageSize.A4);
            writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            
            // 2. 打开文档
            document.open();
            
            // 3. 创建嵌入字体
            BaseFont embeddedFont = createTrueEmbeddedFont();
            Font font = new Font(embeddedFont, 12);
            
            logger.info("✅ 字体创建成功: {}", embeddedFont.getPostscriptFontName());
            
            // 4. 添加内容
            addContentToDocument(document, font);
            
            logger.info("✅ PDF内容添加完成");
            
        } catch (Exception e) {
            logger.error("❌ 创建PDF失败", e);
            throw new RuntimeException("创建PDF失败", e);
        } finally {
            // 5. 关闭文档
            if (document != null) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
        
        // 6. 检查结果
        checkResult(outputPath);
    }
    
    /**
     * 创建真正嵌入的字体
     */
    private static BaseFont createTrueEmbeddedFont() throws DocumentException, IOException {
        logger.info("创建真正嵌入的字体...");
        
        try {
            // 从classpath加载字体
            String fontResourcePath = "fonts/NotoSerifCJKsc-Regular.otf";
            java.io.InputStream fontStream = TrueEmbeddedFontPdfCreator.class.getClassLoader().getResourceAsStream(fontResourcePath);
            
            if (fontStream == null) {
                throw new RuntimeException("字体资源不存在: " + fontResourcePath);
            }
            
            // 读取字体数据
            byte[] fontBytes = readStreamToBytes(fontStream);
            logger.info("字体文件大小: {} KB", fontBytes.length / 1024);
            
            // 创建嵌入字体 - 使用最严格的嵌入设置
            BaseFont font = BaseFont.createFont(
                    "NotoSerifCJKsc-Regular.otf", // 字体名称
                    BaseFont.IDENTITY_H,          // 编码
                    BaseFont.EMBEDDED,            // 嵌入模式
                    true,                         // 缓存
                    fontBytes,                    // 字体数据
                    null                          // 附加数据
            );
            
            logger.info("字体PostScript名称: {}", font.getPostscriptFontName());
            logger.info("字体全名: {}", font.getFullFontName());
            logger.info("字体族名: {}", font.getFamilyFontName());
            
            return font;
            
        } catch (Exception e) {
            logger.error("创建嵌入字体失败", e);
            throw e;
        }
    }
    
    /**
     * 添加内容到文档
     */
    private static void addContentToDocument(Document document, Font font) throws DocumentException {
        logger.info("添加内容到文档...");
        
        // 标题
        Paragraph title = new Paragraph("真正嵌入字体测试文档", font);
        title.setSpacingAfter(20);
        document.add(title);
        
        // 测试内容
        String[] testContents = {
            "✅ 这是使用真正嵌入字体的PDF文档",
            "✅ 字体: NotoSerifCJKsc-Regular (完全嵌入)",
            "✅ 测试用户: 测韦欣",
            "✅ 用户ID: 10004730", 
            "✅ 测试日期: 2025年1月18日",
            "",
            "中文字符测试:",
            "汉字、标点符号、数字123、英文ABC",
            "特殊字符: ！@#￥%……&*（）",
            "",
            "兼容性说明:",
            "• 该PDF包含完全嵌入的思源字体",
            "• 在任何系统上都能正确显示中文",
            "• 无需目标系统安装相应字体",
            "• 确保跨平台一致性显示效果"
        };
        
        for (String content : testContents) {
            if (content.isEmpty()) {
                document.add(new Paragraph(" ")); // 空行
            } else {
                Paragraph p = new Paragraph(content, font);
                p.setSpacingAfter(5);
                document.add(p);
            }
        }
        
        logger.info("文档内容添加完成，共 {} 段", testContents.length);
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
     * 检查结果
     */
    private static void checkResult(String outputPath) {
        logger.info("\n=== 检查创建结果 ===");
        
        // 文件大小检查
        java.io.File file = new java.io.File(outputPath);
        if (file.exists()) {
            long sizeKB = file.length() / 1024;
            long sizeMB = file.length() / (1024 * 1024);
            
            logger.info("文件大小: {} KB ({} MB)", sizeKB, sizeMB);
            
            if (sizeMB > 20) {
                logger.info("✅ 文件很大 ({} MB)，很可能包含嵌入字体", sizeMB);
            } else if (sizeMB > 5) {
                logger.info("✅ 文件较大 ({} MB)，可能包含嵌入字体", sizeMB);
            } else if (sizeKB > 1000) {
                logger.info("⚠️  文件中等大小 ({} KB)，需要验证字体嵌入", sizeKB);
            } else {
                logger.warn("❌ 文件较小 ({} KB)，可能字体未嵌入", sizeKB);
            }
        } else {
            logger.error("❌ 输出文件不存在");
        }
        
        // 立即验证字体嵌入
        logger.info("\n=== 验证字体嵌入 ===");
        SimplePdfFontChecker.checkPdfFonts(outputPath);
    }
    
    /**
     * 主方法
     */
    public static void main(String[] args) {
        String outputPath = "../true-embedded-font.pdf";
        
        try {
            createTrueEmbeddedFontPdf(outputPath);
            
            logger.info("\n🎉 PDF创建完成！");
            logger.info("请检查文件: {}", outputPath);
            
        } catch (Exception e) {
            logger.error("❌ 处理失败", e);
        }
    }
}
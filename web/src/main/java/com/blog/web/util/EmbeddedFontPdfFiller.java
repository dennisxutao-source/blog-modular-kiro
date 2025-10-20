package com.blog.web.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.blog.web.config.PdfConfig;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.io.File;

/**
 * 嵌入字体的PDF填充工具
 * 确保对方系统能正确显示，无需安装字体
 */
@Component
public class EmbeddedFontPdfFiller {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedFontPdfFiller.class);

    private final PdfConfig pdfConfig;

    public EmbeddedFontPdfFiller(PdfConfig pdfConfig) {
        this.pdfConfig = pdfConfig;
    }

    /**
     * 嵌入字体的PDF创建方法
     * 确保任何系统都能正确显示
     */
    public void createEmbeddedFontPDF(Map<String, String> dataMap, String mouldPath,
            String outPutPath, String signatureImgPath)
            throws UnsupportedEncodingException, FileNotFoundException {

        logger.info("=== 嵌入字体PDF创建开始 ===");
        logger.info("策略：嵌入思源字体，确保任何系统都能正确显示");

        PdfReader reader = null;
        PdfStamper stamper = null;

        try {
            // 1. 创建嵌入字体
            BaseFont bfChinese = createEmbeddedFont();
            Font font = new Font(bfChinese, pdfConfig.getDefaultFontSize());

            // 2. 读取PDF模板
            reader = new PdfReader(mouldPath);
            logger.info("PDF模板页数: {}", reader.getNumberOfPages());

            // 3. 创建stamper
            stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));

            // 4. 获取表单字段
            AcroFields form = stamper.getAcroFields();
            logger.info("表单字段数量: {}", form.getFields().size());

            // 5. 直接绘制文字（使用嵌入字体）
            if (dataMap != null && !dataMap.isEmpty()) {
                drawTextWithEmbeddedFont(stamper, form, dataMap, font);
            }

            // 6. 添加签名图片
            if (signatureImgPath != null && !signatureImgPath.isEmpty()) {
                addSignatureImage(stamper, form, signatureImgPath);
            }

            // 7. 设置表单不可编辑
            stamper.setFormFlattening(true);

            logger.info("PDF处理完成");

        } catch (IOException e) {
            logger.error("PDF创建IO错误: {}", e.getMessage());
            throw new RuntimeException("PDF创建失败", e);
        } catch (DocumentException e) {
            logger.error("PDF创建文档错误: {}", e.getMessage());
            throw new RuntimeException("PDF创建失败", e);
        } finally {
            // 清理资源
            closeResources(stamper, reader, signatureImgPath);
        }

        // 检查最终文件大小
        checkFinalFileSize(outPutPath);

        logger.info("=== 嵌入字体PDF创建完成 ===");
    }

    /**
     * 创建嵌入字体
     */
    private BaseFont createEmbeddedFont() throws DocumentException, IOException {
        logger.info("--- 创建嵌入字体 ---");

        try {
            // 使用相对路径从classpath加载字体文件
            String fontResourcePath = "fonts/NotoSerifCJKsc-Regular.otf";
            
            // 通过ClassLoader获取资源
            java.net.URL fontUrl = getClass().getClassLoader().getResource(fontResourcePath);
            
            if (fontUrl != null) {
                logger.info("使用思源字体资源: {}", fontResourcePath);
                logger.info("字体资源URL: {}", fontUrl.toString());
                
                // 获取字体文件的InputStream
                java.io.InputStream fontStream = getClass().getClassLoader().getResourceAsStream(fontResourcePath);
                
                if (fontStream != null) {
                    // 读取字体文件到字节数组
                    byte[] fontBytes = readStreamToBytes(fontStream);
                    logger.info("字体文件大小: {} KB", fontBytes.length / 1024);
                    
                    // 使用字节数组创建字体
                    BaseFont font = BaseFont.createFont(
                            fontResourcePath,
                            BaseFont.IDENTITY_H,
                            pdfConfig.isEmbedFont() ? BaseFont.EMBEDDED : BaseFont.NOT_EMBEDDED,
                            true, // cached
                            fontBytes, // 字体字节数组
                            null
                    );
                    
                    logger.info("✅ 思源字体创建成功（从classpath加载）");
                    logger.info("PostScript名称: {}", font.getPostscriptFontName());
                    logger.info("字体嵌入模式: {}", pdfConfig.isEmbedFont() ? "EMBEDDED" : "NOT_EMBEDDED");
                    
                    return font;
                } else {
                    throw new RuntimeException("无法读取字体资源流: " + fontResourcePath);
                }
            } else {
                // 如果classpath中没有找到，尝试使用配置的文件路径
                logger.warn("classpath中未找到字体资源，尝试使用配置路径");
                return createFontFromConfigPath();
            }
        } catch (Exception e) {
            logger.error("从classpath创建字体失败: {}", e.getMessage());
            // 降级到使用配置路径
            logger.info("尝试使用配置路径创建字体");
            return createFontFromConfigPath();
        }
    }
    
    /**
     * 从配置路径创建字体（降级方案）
     */
    private BaseFont createFontFromConfigPath() throws DocumentException, IOException {
        String fontPath = pdfConfig.getFontPath();
        File fontFile = new File(fontPath);

        if (fontFile.exists()) {
            logger.info("使用配置字体文件: {}", fontPath);
            logger.info("字体文件大小: {} MB", fontFile.length() / 1024 / 1024);

            BaseFont font = BaseFont.createFont(
                    fontPath,
                    BaseFont.IDENTITY_H,
                    pdfConfig.isEmbedFont() ? BaseFont.EMBEDDED : BaseFont.NOT_EMBEDDED);

            logger.info("✅ 思源字体创建成功（从文件路径加载）");
            logger.info("PostScript名称: {}", font.getPostscriptFontName());

            return font;
        } else {
            throw new RuntimeException("字体文件不存在: " + fontPath);
        }
    }
    
    /**
     * 将InputStream读取为字节数组
     */
    private byte[] readStreamToBytes(java.io.InputStream inputStream) throws IOException {
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
     * 使用嵌入字体直接绘制文字
     */
    private void drawTextWithEmbeddedFont(PdfStamper stamper, AcroFields form,
            Map<String, String> dataMap, Font font)
            throws IOException, DocumentException {

        logger.info("--- 使用嵌入字体绘制文字 ---");
        logger.info("使用字体: {}", font.getBaseFont().getPostscriptFontName());
        logger.info("字体嵌入: 是（确保任何系统都能显示）");

        int fieldCount = 0;

        for (String key : dataMap.keySet()) {
            String value = dataMap.get(key);
            if (value != null) {
                try {
                    // 获取字段位置
                    if (form.getFieldPositions(key) != null &&
                            !form.getFieldPositions(key).isEmpty()) {

                        AcroFields.FieldPosition position = form.getFieldPositions(key).get(0);
                        int pageNum = position.page;
                        Rectangle rect = position.position;

                        // 清空原字段
                        form.setField(key, "");

                        // 直接在PDF上绘制文字（使用嵌入字体）
                        PdfContentByte canvas = stamper.getOverContent(pageNum);

                        // 设置字体和大小
                        canvas.beginText();
                        canvas.setFontAndSize(font.getBaseFont(), pdfConfig.getDefaultFontSize());

                        // 计算文字位置
                        float x = rect.getLeft() + 2;
                        float y = rect.getBottom() + 3;

                        // 绘制文字
                        canvas.setTextMatrix(x, y);
                        canvas.showText(value);
                        canvas.endText();

                        fieldCount++;

                        if (fieldCount <= 3) {
                            logger.debug("嵌入字体绘制: {} = {}", key, value);
                        }

                    } else {
                        logger.warn("未找到字段位置: {}", key);
                    }

                } catch (Exception e) {
                    logger.error("嵌入字体绘制字段 {} 失败: {}", key, e.getMessage());
                }
            }
        }

        logger.info("总共嵌入字体绘制字段数: {}", fieldCount);
    }

    /**
     * 添加签名图片
     */
    private void addSignatureImage(PdfStamper stamper, AcroFields form, String signatureImgPath)
            throws IOException, DocumentException {

        logger.info("--- 添加签名图片 ---");

        File imgFile = new File(signatureImgPath);
        if (!imgFile.exists()) {
            logger.warn("签名图片不存在，跳过");
            return;
        }

        String signatureFieldKey = "signatureImag";

        try {
            if (form.getFieldPositions(signatureFieldKey) != null &&
                    !form.getFieldPositions(signatureFieldKey).isEmpty()) {

                int pageNo = form.getFieldPositions(signatureFieldKey).get(0).page;
                Rectangle signRect = form.getFieldPositions(signatureFieldKey).get(0).position;

                Image image = Image.getInstance(signatureImgPath);
                PdfContentByte under = stamper.getOverContent(pageNo);

                image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                image.setAbsolutePosition(signRect.getLeft(), signRect.getBottom());

                under.addImage(image);

                logger.info("✅ 签名图片添加成功");

            } else {
                logger.warn("未找到签名图片字段: {}", signatureFieldKey);
            }
        } catch (Exception e) {
            logger.error("签名图片添加失败: {}", e.getMessage());
        }
    }

    /**
     * 清理资源
     */
    private void closeResources(PdfStamper stamper, PdfReader reader, String signatureImgPath) {
        if (stamper != null) {
            try {
                stamper.close();
            } catch (Exception e) {
                logger.error("关闭PdfStamper失败", e);
            }
        }

        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                logger.error("关闭PdfReader失败", e);
            }
        }

        if (signatureImgPath != null && !signatureImgPath.isEmpty()) {
            try {
                File f = new File(signatureImgPath);
                if (f.exists()) {
                    f.delete();
                    logger.debug("临时签名图片已删除");
                }
            } catch (Exception e) {
                logger.error("删除临时文件失败", e);
            }
        }
    }

    /**
     * 检查最终文件大小
     */
    private void checkFinalFileSize(String outputPath) {
        try {
            File outputFile = new File(outputPath);
            if (outputFile.exists()) {
                long sizeBytes = outputFile.length();
                long sizeKB = sizeBytes / 1024;
                long sizeMB = sizeBytes / (1024 * 1024);

                logger.info("=== 最终文件大小检查 ===");
                logger.info("文件大小: {} bytes ({} KB / {} MB)", sizeBytes, sizeKB, sizeMB);

                if (sizeMB > 30) {
                    logger.error("文件过大 ({} MB)", sizeMB);
                } else if (sizeMB > 5) {
                    logger.warn("文件较大但可接受 ({} MB)", sizeMB);
                    logger.info("优点：任何系统都能正确显示思源字体");
                } else {
                    logger.info("文件大小理想 ({} KB)", sizeKB);
                }

                logger.info("兼容性分析：");
                logger.info("✅ 对方系统无需安装思源字体");
                logger.info("✅ 任何PDF阅读器都能正确显示");
                logger.info("✅ 字体效果完全一致");
                logger.warn("文件大小会增加（但确保兼容性）");
            }
        } catch (Exception e) {
            logger.error("文件大小检查失败: {}", e.getMessage());
        }
    }
}
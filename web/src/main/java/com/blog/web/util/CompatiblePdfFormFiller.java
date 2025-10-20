package com.blog.web.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.io.File;

/**
 * 兼容iText 5.5.11的PDF表单填充工具
 * 专门解决字体显示问题
 */
public class CompatiblePdfFormFiller {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CompatiblePdfFormFiller.class);

    /**
     * 兼容版本的PDF创建方法
     * 重点解决字体显示问题
     */
    public static void createCompatiblePDF(Map<String, String> dataMap, String mouldPath,
            String outPutPath, String signatureImgPath)
            throws UnsupportedEncodingException, FileNotFoundException {

        System.out.println("=== 兼容PDF创建开始 ===");
        System.out.println("模板路径: " + mouldPath);
        System.out.println("输出路径: " + outPutPath);

        PdfReader reader = null;
        PdfStamper stamper = null;

        try {
            // 1. 创建字体（多种方案）
            BaseFont bfChinese = createCompatibleFont();

            // 2. 读取PDF模板
            reader = new PdfReader(mouldPath);
            System.out.println("PDF模板页数: " + reader.getNumberOfPages());

            // 3. 创建stamper
            stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));

            // 4. 获取表单字段
            AcroFields form = stamper.getAcroFields();
            System.out.println("表单字段数量: " + form.getFields().size());

            // 5. 填充文字数据（改进版）
            if (dataMap != null && !dataMap.isEmpty()) {
                fillTextFieldsCompatible(form, dataMap, bfChinese);
            }

            // 6. 添加签名图片
            if (signatureImgPath != null && !signatureImgPath.isEmpty()) {
                addSignatureImage(stamper, form, signatureImgPath);
            }

            // 7. 设置表单不可编辑
            stamper.setFormFlattening(true);

            System.out.println("PDF处理完成");

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

        System.out.println("=== 兼容PDF创建完成 ===");
    }

    /**
     * 创建兼容的字体（多重回退）
     */
    private static BaseFont createCompatibleFont() throws DocumentException, IOException {
        System.out.println("\n--- 创建兼容字体 ---");

        // 方案1：尝试使用Noto Serif字体 + UniGB-UCS2-H编码
        try {
            String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
            File fontFile = new File(fontPath);

            System.out.println("检查字体路径: " + fontPath);
            System.out.println("绝对路径: " + fontFile.getAbsolutePath());
            System.out.println("文件存在: " + fontFile.exists());

            if (fontFile.exists()) {
                System.out.println("尝试Noto Serif字体 + UniGB-UCS2-H编码");

                BaseFont font = BaseFont.createFont(
                        fontPath,
                        "UniGB-UCS2-H", // 使用传统编码
                        BaseFont.NOT_EMBEDDED);

                System.out.println("✅ Noto Serif字体创建成功 (UniGB-UCS2-H)");
                System.out.println("PostScript名称: " + font.getPostscriptFontName());
                return font;
            }
        } catch (Exception e) {
            System.out.println("Noto Serif (UniGB-UCS2-H) 失败: " + e.getMessage());
        }

        // 方案2：尝试使用Noto Serif字体 + Identity-H编码
        try {
            String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
            File fontFile = new File(fontPath);

            if (fontFile.exists()) {
                System.out.println("尝试Noto Serif字体 + Identity-H编码");

                BaseFont font = BaseFont.createFont(
                        fontPath,
                        BaseFont.IDENTITY_H,
                        BaseFont.NOT_EMBEDDED);

                System.out.println("✅ Noto Serif字体创建成功 (Identity-H)");
                System.out.println("PostScript名称: " + font.getPostscriptFontName());
                return font;
            }
        } catch (Exception e) {
            System.out.println("Noto Serif (Identity-H) 失败: " + e.getMessage());
        }

        // 方案3：尝试系统字体
        String[] systemFonts = {
                "STSong-Light",
                "SimSun",
                "Microsoft YaHei"
        };

        for (String systemFont : systemFonts) {
            try {
                System.out.println("尝试系统字体: " + systemFont);
                BaseFont font = BaseFont.createFont(systemFont, "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                System.out.println("✅ 系统字体创建成功: " + font.getPostscriptFontName());
                return font;
            } catch (Exception e) {
                System.out.println(systemFont + " 失败: " + e.getMessage());
            }
        }

        // 方案4：最终回退
        System.out.println("使用Helvetica回退字体");
        BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        System.out.println("⚠️  使用回退字体: " + font.getPostscriptFontName());
        return font;
    }

    /**
     * 兼容的文字字段填充
     */
    private static void fillTextFieldsCompatible(AcroFields form, Map<String, String> dataMap, BaseFont bfChinese)
            throws IOException, DocumentException {

        System.out.println("\n--- 兼容的文字字段填充 ---");
        int fieldCount = 0;

        for (String key : dataMap.keySet()) {
            String value = dataMap.get(key);
            if (value != null) {
                try {
                    // 关键：每次填充前都设置字体
                    form.setFieldProperty(key, "textfont", bfChinese, null);

                    // 可选：设置字体大小
                    try {
                        form.setFieldProperty(key, "textsize", new Float(12), null);
                    } catch (Exception e) {
                        // 忽略字体大小设置失败
                    }

                    // 设置字段值
                    form.setField(key, value);

                    fieldCount++;

                    if (fieldCount <= 3) {
                        System.out.println("填充字段: " + key + " = " + value);
                    }

                } catch (Exception e) {
                    System.out.println("填充字段 " + key + " 失败: " + e.getMessage());
                }
            }
        }

        System.out.println("总共填充字段数: " + fieldCount);

        // 额外尝试：批量设置所有字段的字体
        try {
            Map<String, AcroFields.Item> allFields = form.getFields();
            System.out.println("批量设置字体，共 " + allFields.size() + " 个字段");

            for (String fieldName : allFields.keySet()) {
                try {
                    form.setFieldProperty(fieldName, "textfont", bfChinese, null);
                } catch (Exception e) {
                    // 忽略个别字段设置失败
                }
            }

            System.out.println("✅ 批量字体设置完成");

        } catch (Exception e) {
            System.out.println("批量字体设置失败: " + e.getMessage());
        }
    }

    /**
     * 添加签名图片
     */
    private static void addSignatureImage(PdfStamper stamper, AcroFields form, String signatureImgPath)
            throws IOException, DocumentException {

        System.out.println("\n--- 添加签名图片 ---");
        System.out.println("签名图片路径: " + signatureImgPath);

        File imgFile = new File(signatureImgPath);
        if (!imgFile.exists()) {
            System.out.println("⚠️  签名图片不存在，跳过");
            return;
        }

        String signatureFieldKey = "signatureImag";

        try {
            if (form.getFieldPositions(signatureFieldKey) != null &&
                    !form.getFieldPositions(signatureFieldKey).isEmpty()) {

                int pageNo = form.getFieldPositions(signatureFieldKey).get(0).page;
                Rectangle signRect = form.getFieldPositions(signatureFieldKey).get(0).position;

                float x = signRect.getLeft();
                float y = signRect.getBottom();

                Image image = Image.getInstance(signatureImgPath);
                PdfContentByte under = stamper.getOverContent(pageNo);

                image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                image.setAbsolutePosition(x, y);

                under.addImage(image);

                System.out.println("✅ 签名图片添加成功");

            } else {
                System.out.println("⚠️  未找到签名图片字段: " + signatureFieldKey);
            }
        } catch (Exception e) {
            System.out.println("签名图片添加失败: " + e.getMessage());
        }
    }

    /**
     * 清理资源
     */
    private static void closeResources(PdfStamper stamper, PdfReader reader, String signatureImgPath) {
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
                    System.out.println("临时签名图片已删除");
                }
            } catch (Exception e) {
                logger.error("删除临时文件失败", e);
            }
        }
    }

    /**
     * 检查最终文件大小
     */
    private static void checkFinalFileSize(String outputPath) {
        try {
            File outputFile = new File(outputPath);
            if (outputFile.exists()) {
                long sizeBytes = outputFile.length();
                long sizeKB = sizeBytes / 1024;
                long sizeMB = sizeBytes / (1024 * 1024);

                System.out.println("\n=== 最终文件大小检查 ===");
                System.out.println("文件大小: " + sizeBytes + " bytes (" + sizeKB + " KB / " + sizeMB + " MB)");

                if (sizeMB > 10) {
                    System.out.println("❌ 警告：文件仍然过大 (" + sizeMB + " MB)");
                    System.out.println("可能的原因：");
                    System.out.println("1. 字体仍然被嵌入了");
                    System.out.println("2. PDF模板本身很大");
                    System.out.println("3. 图片文件过大");
                } else if (sizeKB > 2000) {
                    System.out.println("⚠️  文件较大但可接受 (" + sizeKB + " KB)");
                } else {
                    System.out.println("✅ 文件大小正常 (" + sizeKB + " KB)");
                }
            }
        } catch (Exception e) {
            System.out.println("文件大小检查失败: " + e.getMessage());
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        try {
            // 测试数据
            java.util.Map<String, String> testData = new java.util.HashMap<>();
            testData.put("name", "测韦欣");
            testData.put("id", "eg.10004730");
            testData.put("content", "这是测试内容");

            // 注意：需要提供实际的模板文件路径
            String templatePath = "path/to/your/template.pdf";
            String outputPath = "compatible_output.pdf";
            String signaturePath = "path/to/signature.png";

            System.out.println("请将模板路径替换为实际路径");
            // createCompatiblePDF(testData, templatePath, outputPath, signaturePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
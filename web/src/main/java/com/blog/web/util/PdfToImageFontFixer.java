package com.blog.web.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * PDF转图片字体修复工具
 * 解决PDF转图片时中文显示为小方格的问题
 */
public class PdfToImageFontFixer {
    
    private static final String NOTO_SERIF_REGULAR = "fonts/NotoSerifCJKsc-Regular.otf";
    
    /**
     * 创建图片转换友好的PDF - 完全嵌入字体
     */
    public static void createImageFriendlyPdf(Map<String, String> dataMap, String mouldPath,
                                            String outPutPath, String signatureImgPath) {
        
        System.out.println("创建图片转换友好的PDF...");
        
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 设置压缩但保证字体完整嵌入
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            writer.setPdfVersion(PdfWriter.VERSION_1_5);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 关键：创建完全嵌入的字体，不使用子集化
            BaseFont embeddedFont = createFullyEmbeddedFont();
            
            // 填充数据
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    String value = dataMap.get(key);
                    try {
                        form.setFieldProperty(key, "textfont", embeddedFont, null);
                        form.setField(key, value);
                        System.out.println("填充字段: " + key + " = " + value);
                    } catch (Exception e) {
                        System.out.println("字段填充失败: " + key + ", 错误: " + e.getMessage());
                    }
                }
            }
            
            // 处理图片
            if (signatureImgPath != null && new File(signatureImgPath).exists()) {
                processImage(form, stamper, signatureImgPath);
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            File outputFile = new File(outPutPath);
            System.out.println("图片友好PDF创建完成！");
            System.out.println("文件大小: " + formatSize(outputFile.length()));
            System.out.println("此PDF转换为图片时应该能正确显示中文");
            
        } catch (Exception e) {
            System.out.println("图片友好PDF创建失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建智能嵌入字体的PDF - 平衡大小和兼容性
     */
    public static void createSmartEmbeddedPdf(Map<String, String> dataMap, String mouldPath,
                                            String outPutPath, String signatureImgPath) {
        
        System.out.println("创建智能嵌入字体PDF...");
        
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            writer.setPdfVersion(PdfWriter.VERSION_1_5);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 创建智能嵌入字体
            BaseFont smartFont = createSmartEmbeddedFont(dataMap);
            
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    String value = dataMap.get(key);
                    try {
                        form.setFieldProperty(key, "textfont", smartFont, null);
                        form.setField(key, value);
                        System.out.println("填充字段: " + key + " = " + value);
                    } catch (Exception e) {
                        System.out.println("字段填充失败: " + key);
                    }
                }
            }
            
            // 处理图片
            if (signatureImgPath != null && new File(signatureImgPath).exists()) {
                processImage(form, stamper, signatureImgPath);
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            File outputFile = new File(outPutPath);
            System.out.println("智能嵌入PDF创建完成！");
            System.out.println("文件大小: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("智能嵌入PDF创建失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建使用标准中文字体的PDF
     */
    public static void createStandardChinesePdf(Map<String, String> dataMap, String mouldPath,
                                              String outPutPath, String signatureImgPath) {
        
        System.out.println("创建标准中文字体PDF...");
        
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            writer.setPdfVersion(PdfWriter.VERSION_1_5);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 使用标准中文字体
            BaseFont chineseFont = createStandardChineseFont();
            
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    String value = dataMap.get(key);
                    try {
                        form.setFieldProperty(key, "textfont", chineseFont, null);
                        form.setField(key, value);
                        System.out.println("填充字段: " + key + " = " + value);
                    } catch (Exception e) {
                        System.out.println("字段填充失败: " + key);
                    }
                }
            }
            
            // 处理图片
            if (signatureImgPath != null && new File(signatureImgPath).exists()) {
                processImage(form, stamper, signatureImgPath);
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            File outputFile = new File(outPutPath);
            System.out.println("标准中文字体PDF创建完成！");
            System.out.println("文件大小: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("标准中文字体PDF创建失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建完全嵌入的字体 - 不使用子集化
     */
    private static BaseFont createFullyEmbeddedFont() throws DocumentException, IOException {
        ClassPathResource resource = new ClassPathResource(NOTO_SERIF_REGULAR);
        if (!resource.exists()) {
            System.out.println("思源宋体文件不存在，使用标准中文字体");
            return createStandardChineseFont();
        }
        
        try (InputStream is = resource.getInputStream()) {
            byte[] fontBytes = readAllBytes(is);
            
            BaseFont font = BaseFont.createFont(
                NOTO_SERIF_REGULAR,
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED,  // 完全嵌入
                true,
                fontBytes,
                null
            );
            
            // 关键：不使用子集化，完全嵌入字体
            font.setSubset(false);
            
            System.out.println("完全嵌入思源宋体创建成功");
            System.out.println("字体子集化状态: " + font.isSubset());
            
            return font;
        }
    }
    
    /**
     * 创建智能嵌入字体 - 根据内容决定嵌入策略
     */
    private static BaseFont createSmartEmbeddedFont(Map<String, String> dataMap) throws DocumentException, IOException {
        // 分析文本内容
        int chineseCharCount = 0;
        int totalCharCount = 0;
        
        if (dataMap != null) {
            for (String value : dataMap.values()) {
                if (value != null) {
                    for (char c : value.toCharArray()) {
                        totalCharCount++;
                        if (c >= 0x4E00 && c <= 0x9FFF) {
                            chineseCharCount++;
                        }
                    }
                }
            }
        }
        
        System.out.println("文本分析 - 总字符数: " + totalCharCount + ", 中文字符数: " + chineseCharCount);
        
        ClassPathResource resource = new ClassPathResource(NOTO_SERIF_REGULAR);
        if (!resource.exists()) {
            return createStandardChineseFont();
        }
        
        try (InputStream is = resource.getInputStream()) {
            byte[] fontBytes = readAllBytes(is);
            
            BaseFont font = BaseFont.createFont(
                NOTO_SERIF_REGULAR,
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED,
                true,
                fontBytes,
                null
            );
            
            // 智能决策：如果中文字符少，使用子集化；如果多，完全嵌入
            if (chineseCharCount < 50) {
                font.setSubset(true);
                System.out.println("中文字符较少，使用子集化");
            } else {
                font.setSubset(false);
                System.out.println("中文字符较多，完全嵌入字体");
            }
            
            return font;
        }
    }
    
    /**
     * 创建标准中文字体
     */
    private static BaseFont createStandardChineseFont() throws DocumentException, IOException {
        try {
            // 尝试使用iText内置的中文字体
            BaseFont font = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            System.out.println("使用STSong-Light标准中文字体");
            return font;
        } catch (Exception e1) {
            try {
                BaseFont font = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                System.out.println("使用STSongStd-Light标准中文字体");
                return font;
            } catch (Exception e2) {
                try {
                    BaseFont font = BaseFont.createFont("MHei-Medium", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                    System.out.println("使用MHei-Medium标准中文字体");
                    return font;
                } catch (Exception e3) {
                    // 最后回退
                    BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                    System.out.println("回退到Helvetica字体");
                    return font;
                }
            }
        }
    }
    
    /**
     * 处理图片
     */
    private static void processImage(AcroFields form, PdfStamper stamper, String imagePath) {
        try {
            for (String fieldName : form.getFields().keySet()) {
                if (fieldName.toLowerCase().contains("sign") || 
                    fieldName.toLowerCase().contains("image") ||
                    fieldName.toLowerCase().contains("imag")) {
                    
                    try {
                        int pageNo = form.getFieldPositions(fieldName).get(0).page;
                        Rectangle signRect = form.getFieldPositions(fieldName).get(0).position;
                        float x = signRect.getLeft();
                        float y = signRect.getBottom();
                        
                        Image image = Image.getInstance(imagePath);
                        image.setCompressionLevel(9);
                        
                        PdfContentByte under = stamper.getOverContent(pageNo);
                        image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                        image.setAbsolutePosition(x, y);
                        under.addImage(image);
                        
                        System.out.println("图片处理完成: " + fieldName);
                        break;
                        
                    } catch (Exception e) {
                        System.out.println("图片处理失败: " + fieldName);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("图片处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试所有图片友好方案
     */
    public static void testImageFriendlyMethods(Map<String, String> dataMap, String mouldPath, 
                                              String outputDir, String signatureImgPath) {
        System.out.println("=== PDF转图片友好方案测试 ===");
        System.out.println("目标：解决PDF转图片时中文显示小方格问题");
        System.out.println();
        
        new File(outputDir).mkdirs();
        
        // 方案1：完全嵌入字体
        System.out.println("方案1：完全嵌入思源宋体（文件会较大但转图片效果最好）");
        createImageFriendlyPdf(dataMap, mouldPath, outputDir + "/image_friendly_full.pdf", signatureImgPath);
        System.out.println();
        
        // 方案2：智能嵌入
        System.out.println("方案2：智能嵌入策略（平衡大小和效果）");
        createSmartEmbeddedPdf(dataMap, mouldPath, outputDir + "/image_friendly_smart.pdf", signatureImgPath);
        System.out.println();
        
        // 方案3：标准中文字体
        System.out.println("方案3：标准中文字体（最小文件）");
        createStandardChinesePdf(dataMap, mouldPath, outputDir + "/image_friendly_standard.pdf", signatureImgPath);
        System.out.println();
        
        System.out.println("=== 测试完成 ===");
        System.out.println("请测试这些PDF转换为图片的效果：");
        System.out.println("1. image_friendly_full.pdf - 应该转图片效果最好");
        System.out.println("2. image_friendly_smart.pdf - 平衡方案");
        System.out.println("3. image_friendly_standard.pdf - 最小文件");
    }
    
    /**
     * 格式化文件大小
     */
    private static String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        }
    }
    
    /**
     * 兼容Java 8的readAllBytes方法
     */
    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[8192];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}
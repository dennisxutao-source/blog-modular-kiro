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
 * 超级压缩PDF创建工具
 * 目标：将18.7MB压缩到1MB以下
 */
public class UltraCompressedPdfCreator {
    
    private static final String NOTO_SERIF_LIGHT = "fonts/NotoSerifCJKsc-Light.otf";
    private static final String NOTO_SERIF_REGULAR = "fonts/NotoSerifCJKsc-Regular.otf";
    
    /**
     * 方案1：使用更轻量的思源宋体Light版本
     */
    public static void createWithLightFont(Map<String, String> dataMap, String mouldPath,
                                         String outPutPath, String signatureImgPath) {
        
        System.out.println("尝试使用思源宋体Light版本...");
        
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 最强压缩设置
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            writer.setPdfVersion(PdfWriter.VERSION_1_4); // 使用更老的版本，压缩更好
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 尝试使用Light版本字体
            BaseFont font = createLightSubsetFont();
            
            // 填充数据
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    try {
                        form.setFieldProperty(key, "textfont", font, null);
                        form.setField(key, dataMap.get(key));
                    } catch (Exception e) {
                        System.out.println("字段填充失败: " + key);
                    }
                }
            }
            
            // 处理图片
            if (signatureImgPath != null && new File(signatureImgPath).exists()) {
                processCompressedImage(form, stamper, signatureImgPath);
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            File outputFile = new File(outPutPath);
            System.out.println("Light字体PDF大小: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("Light字体方案失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 方案2：极限子集化 - 只包含实际使用的字符
     */
    public static void createWithExtremeSubset(Map<String, String> dataMap, String mouldPath,
                                             String outPutPath, String signatureImgPath) {
        
        System.out.println("尝试极限子集化...");
        
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 极限压缩设置
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            writer.setPdfVersion(PdfWriter.VERSION_1_4);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 创建极限子集字体
            BaseFont font = createExtremeSubsetFont(dataMap);
            
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    try {
                        form.setFieldProperty(key, "textfont", font, null);
                        form.setField(key, dataMap.get(key));
                    } catch (Exception e) {
                        System.out.println("字段填充失败: " + key);
                    }
                }
            }
            
            // 处理图片
            if (signatureImgPath != null && new File(signatureImgPath).exists()) {
                processCompressedImage(form, stamper, signatureImgPath);
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            File outputFile = new File(outPutPath);
            System.out.println("极限子集PDF大小: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("极限子集方案失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 方案3：混合方案 - 中文用子集，英文数字用系统字体
     */
    public static void createWithHybridFonts(Map<String, String> dataMap, String mouldPath,
                                           String outPutPath, String signatureImgPath) {
        
        System.out.println("尝试混合字体方案...");
        
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            writer.setPdfVersion(PdfWriter.VERSION_1_4);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 创建混合字体策略
            BaseFont chineseFont = createMinimalChineseFont(dataMap);
            BaseFont englishFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    String value = dataMap.get(key);
                    try {
                        // 根据内容选择字体
                        BaseFont selectedFont = containsChinese(value) ? chineseFont : englishFont;
                        form.setFieldProperty(key, "textfont", selectedFont, null);
                        form.setField(key, value);
                    } catch (Exception e) {
                        System.out.println("字段填充失败: " + key);
                    }
                }
            }
            
            // 处理图片
            if (signatureImgPath != null && new File(signatureImgPath).exists()) {
                processCompressedImage(form, stamper, signatureImgPath);
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            File outputFile = new File(outPutPath);
            System.out.println("混合字体PDF大小: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("混合字体方案失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 方案4：终极压缩 - 使用系统中文字体
     */
    public static void createWithSystemFont(Map<String, String> dataMap, String mouldPath,
                                          String outPutPath, String signatureImgPath) {
        
        System.out.println("尝试系统字体方案...");
        
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            writer.setPdfVersion(PdfWriter.VERSION_1_4);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 使用系统中文字体，不嵌入
            BaseFont font = null;
            try {
                font = BaseFont.createFont("SimSun", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                System.out.println("使用SimSun系统字体（不嵌入）");
            } catch (Exception e) {
                try {
                    font = BaseFont.createFont("Microsoft YaHei", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                    System.out.println("使用Microsoft YaHei系统字体（不嵌入）");
                } catch (Exception e2) {
                    font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                    System.out.println("使用Helvetica字体（不嵌入）");
                }
            }
            
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    try {
                        form.setFieldProperty(key, "textfont", font, null);
                        form.setField(key, dataMap.get(key));
                    } catch (Exception e) {
                        System.out.println("字段填充失败: " + key);
                    }
                }
            }
            
            // 处理图片
            if (signatureImgPath != null && new File(signatureImgPath).exists()) {
                processCompressedImage(form, stamper, signatureImgPath);
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            File outputFile = new File(outPutPath);
            System.out.println("系统字体PDF大小: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("系统字体方案失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建Light版本子集字体
     */
    private static BaseFont createLightSubsetFont() throws DocumentException, IOException {
        // 先尝试Light版本
        ClassPathResource lightResource = new ClassPathResource(NOTO_SERIF_LIGHT);
        if (lightResource.exists()) {
            try (InputStream is = lightResource.getInputStream()) {
                byte[] fontBytes = readAllBytes(is);
                BaseFont font = BaseFont.createFont(
                    NOTO_SERIF_LIGHT,
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    false, // 不缓存
                    fontBytes,
                    null
                );
                font.setSubset(true);
                System.out.println("使用思源宋体Light版本");
                return font;
            }
        }
        
        // 回退到Regular版本
        ClassPathResource regularResource = new ClassPathResource(NOTO_SERIF_REGULAR);
        if (regularResource.exists()) {
            try (InputStream is = regularResource.getInputStream()) {
                byte[] fontBytes = readAllBytes(is);
                BaseFont font = BaseFont.createFont(
                    NOTO_SERIF_REGULAR,
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    false,
                    fontBytes,
                    null
                );
                font.setSubset(true);
                System.out.println("回退到思源宋体Regular版本");
                return font;
            }
        }
        
        // 最后回退到系统字体
        return BaseFont.createFont("SimSun", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
    }
    
    /**
     * 创建极限子集字体
     */
    private static BaseFont createExtremeSubsetFont(Map<String, String> dataMap) throws DocumentException, IOException {
        ClassPathResource resource = new ClassPathResource(NOTO_SERIF_REGULAR);
        if (!resource.exists()) {
            return BaseFont.createFont("SimSun", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        }
        
        try (InputStream is = resource.getInputStream()) {
            byte[] fontBytes = readAllBytes(is);
            
            BaseFont font = BaseFont.createFont(
                NOTO_SERIF_REGULAR,
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED,
                false, // 关键：不缓存，每次都重新创建
                fontBytes,
                null
            );
            
            // 强制设置子集化
            font.setSubset(true);
            
            // 统计实际使用的字符
            int totalChars = 0;
            if (dataMap != null) {
                for (String value : dataMap.values()) {
                    if (value != null) {
                        totalChars += value.length();
                    }
                }
            }
            
            System.out.println("极限子集化，实际字符数: " + totalChars);
            return font;
        }
    }
    
    /**
     * 创建最小中文字体
     */
    private static BaseFont createMinimalChineseFont(Map<String, String> dataMap) throws DocumentException, IOException {
        ClassPathResource resource = new ClassPathResource(NOTO_SERIF_REGULAR);
        if (!resource.exists()) {
            return BaseFont.createFont("SimSun", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        }
        
        try (InputStream is = resource.getInputStream()) {
            byte[] fontBytes = readAllBytes(is);
            
            BaseFont font = BaseFont.createFont(
                NOTO_SERIF_REGULAR,
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED,
                false,
                fontBytes,
                null
            );
            
            font.setSubset(true);
            System.out.println("创建最小中文字体子集");
            return font;
        }
    }
    
    /**
     * 检查字符串是否包含中文
     */
    private static boolean containsChinese(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FFF) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 处理压缩图片
     */
    private static void processCompressedImage(AcroFields form, PdfStamper stamper, String imagePath) {
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
                        
                        // 极限压缩图片
                        image.setCompressionLevel(9);
                        
                        // 如果图片太大，进一步缩小
                        float maxWidth = signRect.getWidth();
                        float maxHeight = signRect.getHeight();
                        if (image.getWidth() > maxWidth || image.getHeight() > maxHeight) {
                            image.scaleToFit(maxWidth * 0.8f, maxHeight * 0.8f); // 缩小到80%
                        }
                        
                        PdfContentByte under = stamper.getOverContent(pageNo);
                        image.setAbsolutePosition(x, y);
                        under.addImage(image);
                        
                        System.out.println("压缩图片处理完成");
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
     * 运行所有压缩方案测试
     */
    public static void testAllCompressionMethods(Map<String, String> dataMap, String mouldPath, 
                                               String outputDir, String signatureImgPath) {
        System.out.println("=== 超级压缩方案测试 ===");
        System.out.println("目标：将18.7MB压缩到1MB以下");
        System.out.println();
        
        new File(outputDir).mkdirs();
        
        // 方案1：Light字体
        System.out.println("测试方案1：Light字体版本");
        createWithLightFont(dataMap, mouldPath, outputDir + "/ultra_light.pdf", signatureImgPath);
        System.out.println();
        
        // 方案2：极限子集
        System.out.println("测试方案2：极限子集化");
        createWithExtremeSubset(dataMap, mouldPath, outputDir + "/ultra_subset.pdf", signatureImgPath);
        System.out.println();
        
        // 方案3：混合字体
        System.out.println("测试方案3：混合字体策略");
        createWithHybridFonts(dataMap, mouldPath, outputDir + "/ultra_hybrid.pdf", signatureImgPath);
        System.out.println();
        
        // 方案4：系统字体
        System.out.println("测试方案4：系统字体（最小）");
        createWithSystemFont(dataMap, mouldPath, outputDir + "/ultra_system.pdf", signatureImgPath);
        System.out.println();
        
        System.out.println("=== 测试完成 ===");
        System.out.println("请检查 " + outputDir + " 目录下的文件大小");
        System.out.println("选择最小的文件对应的方案");
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
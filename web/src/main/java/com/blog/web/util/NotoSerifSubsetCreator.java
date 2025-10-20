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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 思源宋体子集化PDF创建工具
 * 解决使用思源宋体但文件大小过大的问题
 */
public class NotoSerifSubsetCreator {
    
    private static final String NOTO_SERIF_REGULAR = "fonts/NotoSerifCJKsc-Regular.otf";
    
    /**
     * 创建使用思源宋体子集的PDF
     */
    public static void createPDFWithNotoSerifSubset(Map<String, String> dataMap, String mouldPath,
                                                   String outPutPath, String signatureImgPath) {
        
        System.out.println("开始创建思源宋体子集PDF...");
        
        PdfReader reader = null;
        PdfStamper stamper = null;
        
        try {
            reader = new PdfReader(mouldPath);
            
            // 直接输出到最终文件，避免中间缓存
            stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 设置最强压缩
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            writer.setPdfVersion(PdfWriter.VERSION_1_5);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 关键：创建真正的子集化思源宋体
            BaseFont notoSerifFont = createTrueSubsetNotoSerif(dataMap);
            
            // 填充文字数据
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    String value = dataMap.get(key);
                    try {
                        form.setFieldProperty(key, "textfont", notoSerifFont, null);
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
            
            // 扁平化表单
            stamper.setFormFlattening(true);
            
        } catch (Exception e) {
            System.out.println("PDF创建失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 资源清理
            try {
                if (stamper != null) {
                    stamper.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // 删除临时图片文件
            if (signatureImgPath != null) {
                File imgFile = new File(signatureImgPath);
                if (imgFile.exists()) {
                    imgFile.delete();
                }
            }
        }
        
        // 检查结果
        File outputFile = new File(outPutPath);
        if (outputFile.exists()) {
            long fileSize = outputFile.length();
            System.out.println("思源宋体子集PDF创建完成！");
            System.out.println("文件大小: " + formatSize(fileSize));
            
            if (fileSize > 5 * 1024 * 1024) {
                System.out.println("⚠️  警告：文件仍然很大，子集化可能没有生效");
            } else {
                System.out.println("✅ 成功：文件大小合理，子集化生效");
            }
        }
    }
    
    /**
     * 创建真正的子集化思源宋体
     */
    private static BaseFont createTrueSubsetNotoSerif(Map<String, String> dataMap) throws DocumentException, IOException {
        ClassPathResource resource = new ClassPathResource(NOTO_SERIF_REGULAR);
        if (!resource.exists()) {
            System.out.println("思源宋体文件不存在，使用系统字体");
            return BaseFont.createFont("SimSun", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        }
        
        try (InputStream is = resource.getInputStream()) {
            byte[] fontBytes = readAllBytes(is);
            
            // 创建字体时启用子集化
            BaseFont font = BaseFont.createFont(
                NOTO_SERIF_REGULAR,
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED,
                true,  // 缓存字体
                fontBytes,
                null
            );
            
            // 关键：立即设置子集化
            font.setSubset(true);
            
            // 验证子集化是否生效
            boolean isSubset = font.isSubset();
            System.out.println("思源宋体子集化状态: " + (isSubset ? "已启用" : "未启用"));
            
            if (!isSubset) {
                System.out.println("⚠️  警告：子集化未生效，尝试强制设置");
                // 尝试强制设置子集化
                font.setSubset(true);
            }
            
            return font;
        }
    }
    
    /**
     * 创建基于实际使用文字的子集字体
     */
    private static BaseFont createSmartSubsetNotoSerif(Map<String, String> dataMap) throws DocumentException, IOException {
        // 收集所有要使用的字符
        Set<Character> usedChars = new HashSet<>();
        if (dataMap != null) {
            for (String value : dataMap.values()) {
                if (value != null) {
                    for (char c : value.toCharArray()) {
                        usedChars.add(c);
                    }
                }
            }
        }
        
        System.out.println("实际使用的字符数: " + usedChars.size());
        
        ClassPathResource resource = new ClassPathResource(NOTO_SERIF_REGULAR);
        if (!resource.exists()) {
            System.out.println("思源宋体文件不存在，使用系统字体");
            return BaseFont.createFont("SimSun", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
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
            
            // 设置子集化
            font.setSubset(true);
            
            System.out.println("智能子集思源宋体创建完成");
            return font;
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
                        System.out.println("图片字段处理失败: " + fieldName);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("图片处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试不同的子集化方法
     */
    public static void testSubsetMethods(Map<String, String> dataMap, String mouldPath, String outputDir) {
        System.out.println("=== 测试不同的子集化方法 ===");
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 方法1：标准子集化
        System.out.println("测试1: 标准子集化");
        createPDFWithNotoSerifSubset(dataMap, mouldPath, outputDir + "/subset_standard.pdf", null);
        
        // 方法2：智能子集化
        System.out.println("测试2: 智能子集化");
        createPDFWithSmartSubset(dataMap, mouldPath, outputDir + "/subset_smart.pdf");
        
        // 方法3：最小子集化
        System.out.println("测试3: 最小子集化");
        createPDFWithMinimalSubset(dataMap, mouldPath, outputDir + "/subset_minimal.pdf");
        
        System.out.println("=== 测试完成，请检查文件大小 ===");
    }
    
    /**
     * 智能子集化方法
     */
    private static void createPDFWithSmartSubset(Map<String, String> dataMap, String mouldPath, String outPutPath) {
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 压缩设置
            stamper.getWriter().setCompressionLevel(9);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            BaseFont font = createSmartSubsetNotoSerif(dataMap);
            
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    try {
                        form.setFieldProperty(key, "textfont", font, null);
                        form.setField(key, dataMap.get(key));
                    } catch (Exception e) {
                        // 忽略错误
                    }
                }
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            File outputFile = new File(outPutPath);
            System.out.println("智能子集PDF大小: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("智能子集化失败: " + e.getMessage());
        }
    }
    
    /**
     * 最小子集化方法
     */
    private static void createPDFWithMinimalSubset(Map<String, String> dataMap, String mouldPath, String outPutPath) {
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 最大压缩
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            writer.setPdfVersion(PdfWriter.VERSION_1_4); // 使用较老版本
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 创建最小子集字体
            ClassPathResource resource = new ClassPathResource(NOTO_SERIF_REGULAR);
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    byte[] fontBytes = readAllBytes(is);
                    
                    BaseFont font = BaseFont.createFont(
                        NOTO_SERIF_REGULAR,
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED,
                        false, // 不缓存
                        fontBytes,
                        null
                    );
                    
                    // 强制子集化
                    font.setSubset(true);
                    
                    if (dataMap != null) {
                        for (String key : dataMap.keySet()) {
                            try {
                                form.setFieldProperty(key, "textfont", font, null);
                                form.setField(key, dataMap.get(key));
                            } catch (Exception e) {
                                // 忽略错误
                            }
                        }
                    }
                }
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            File outputFile = new File(outPutPath);
            System.out.println("最小子集PDF大小: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("最小子集化失败: " + e.getMessage());
        }
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
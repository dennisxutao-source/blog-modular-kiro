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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * 终极解决方案PDF创建工具
 * 解决两个核心问题：
 * 1. 系统字体中文显示问题
 * 2. 思源宋体文件大小问题
 */
public class FinalSolutionPdfCreator {
    
    /**
     * 方案1：使用iText内置中文字体 - 解决显示和大小问题
     */
    public static void createWithBuiltinChineseFont(Map<String, String> dataMap, String mouldPath,
                                                  String outPutPath, String signatureImgPath) {
        
        System.out.println("使用iText内置中文字体方案...");
        
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 最大压缩设置
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            writer.setPdfVersion(PdfWriter.VERSION_1_4);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 关键：使用iText内置的中文字体
            BaseFont chineseFont = null;
            try {
                // 方案1：使用STSong-Light（iText内置）
                chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                System.out.println("使用STSong-Light内置字体");
            } catch (Exception e1) {
                try {
                    // 方案2：使用STSongStd-Light
                    chineseFont = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                    System.out.println("使用STSongStd-Light内置字体");
                } catch (Exception e2) {
                    try {
                        // 方案3：使用MHei-Medium
                        chineseFont = BaseFont.createFont("MHei-Medium", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                        System.out.println("使用MHei-Medium内置字体");
                    } catch (Exception e3) {
                        // 最后回退
                        chineseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                        System.out.println("回退到Helvetica字体");
                    }
                }
            }
            
            // 填充数据
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    String value = dataMap.get(key);
                    try {
                        form.setFieldProperty(key, "textfont", chineseFont, null);
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
            System.out.println("内置字体PDF大小: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("内置字体方案失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 方案2：不设置字体，让PDF使用默认字体
     */
    public static void createWithDefaultFont(Map<String, String> dataMap, String mouldPath,
                                           String outPutPath, String signatureImgPath) {
        
        System.out.println("使用默认字体方案...");
        
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 最大压缩
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            writer.setPdfVersion(PdfWriter.VERSION_1_4);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 关键：不设置任何字体，让PDF使用模板原有字体
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    String value = dataMap.get(key);
                    try {
                        // 只设置值，不设置字体
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
            System.out.println("默认字体PDF大小: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("默认字体方案失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 方案3：强制子集化思源宋体 - 使用不同的参数组合
     */
    public static void createWithForcedSubset(Map<String, String> dataMap, String mouldPath,
                                            String outPutPath, String signatureImgPath) {
        
        System.out.println("使用强制子集化方案...");
        
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 极限压缩设置
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9);
            writer.setPdfVersion(PdfWriter.VERSION_1_4);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 强制子集化思源宋体
            BaseFont font = createForcedSubsetFont();
            
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    String value = dataMap.get(key);
                    try {
                        form.setFieldProperty(key, "textfont", font, null);
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
            System.out.println("强制子集PDF大小: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("强制子集方案失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 方案4：诊断模式 - 逐步测试找出问题
     */
    public static void createDiagnosticVersion(Map<String, String> dataMap, String mouldPath, String outputDir) {
        System.out.println("=== 诊断模式开始 ===");
        
        new File(outputDir).mkdirs();
        
        try {
            // 测试1：完全不修改PDF
            System.out.println("测试1：完全不修改PDF");
            PdfReader reader1 = new PdfReader(mouldPath);
            PdfStamper stamper1 = new PdfStamper(reader1, new FileOutputStream(outputDir + "/test1_no_change.pdf"));
            stamper1.close();
            reader1.close();
            
            File test1 = new File(outputDir + "/test1_no_change.pdf");
            System.out.println("测试1结果: " + formatSize(test1.length()));
            
            // 测试2：只添加压缩，不填充内容
            System.out.println("测试2：只添加压缩");
            PdfReader reader2 = new PdfReader(mouldPath);
            PdfStamper stamper2 = new PdfStamper(reader2, new FileOutputStream(outputDir + "/test2_compress_only.pdf"));
            stamper2.getWriter().setCompressionLevel(9);
            stamper2.setFullCompression();
            stamper2.close();
            reader2.close();
            
            File test2 = new File(outputDir + "/test2_compress_only.pdf");
            System.out.println("测试2结果: " + formatSize(test2.length()));
            
            // 测试3：填充内容但不设置字体
            System.out.println("测试3：填充内容不设置字体");
            PdfReader reader3 = new PdfReader(mouldPath);
            PdfStamper stamper3 = new PdfStamper(reader3, new FileOutputStream(outputDir + "/test3_fill_no_font.pdf"));
            stamper3.getWriter().setCompressionLevel(9);
            stamper3.setFullCompression();
            
            AcroFields form3 = stamper3.getAcroFields();
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    try {
                        form3.setField(key, dataMap.get(key));
                    } catch (Exception e) {
                        // 忽略错误
                    }
                }
            }
            
            stamper3.setFormFlattening(true);
            stamper3.close();
            reader3.close();
            
            File test3 = new File(outputDir + "/test3_fill_no_font.pdf");
            System.out.println("测试3结果: " + formatSize(test3.length()));
            
            // 测试4：使用内置字体
            System.out.println("测试4：使用内置字体");
            createWithBuiltinChineseFont(dataMap, mouldPath, outputDir + "/test4_builtin_font.pdf", null);
            
        } catch (Exception e) {
            System.out.println("诊断测试失败: " + e.getMessage());
        }
        
        System.out.println("=== 诊断模式结束 ===");
    }
    
    /**
     * 创建强制子集字体
     */
    private static BaseFont createForcedSubsetFont() throws DocumentException, IOException {
        try {
            org.springframework.core.io.ClassPathResource resource = 
                new org.springframework.core.io.ClassPathResource("fonts/NotoSerifCJKsc-Regular.otf");
            
            if (resource.exists()) {
                try (java.io.InputStream is = resource.getInputStream()) {
                    byte[] fontBytes = readAllBytes(is);
                    
                    // 尝试不同的参数组合强制子集化
                    BaseFont font = BaseFont.createFont(
                        "fonts/NotoSerifCJKsc-Regular.otf",
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED,
                        false, // 不缓存
                        fontBytes,
                        null,
                        true   // 强制子集化
                    );
                    
                    // 多次设置子集化
                    font.setSubset(true);
                    
                    System.out.println("强制子集字体创建完成，子集状态: " + font.isSubset());
                    return font;
                }
            }
        } catch (Exception e) {
            System.out.println("强制子集字体创建失败: " + e.getMessage());
        }
        
        // 回退到内置字体
        try {
            return BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
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
     * 运行所有解决方案测试
     */
    public static void testAllSolutions(Map<String, String> dataMap, String mouldPath, 
                                      String outputDir, String signatureImgPath) {
        System.out.println("=== 终极解决方案测试 ===");
        System.out.println("目标：解决显示问题和大小问题");
        System.out.println();
        
        new File(outputDir).mkdirs();
        
        // 方案1：内置中文字体
        System.out.println("方案1：内置中文字体");
        createWithBuiltinChineseFont(dataMap, mouldPath, outputDir + "/solution1_builtin.pdf", signatureImgPath);
        System.out.println();
        
        // 方案2：默认字体
        System.out.println("方案2：默认字体");
        createWithDefaultFont(dataMap, mouldPath, outputDir + "/solution2_default.pdf", signatureImgPath);
        System.out.println();
        
        // 方案3：强制子集
        System.out.println("方案3：强制子集");
        createWithForcedSubset(dataMap, mouldPath, outputDir + "/solution3_forced.pdf", signatureImgPath);
        System.out.println();
        
        // 方案4：诊断测试
        System.out.println("方案4：诊断测试");
        createDiagnosticVersion(dataMap, mouldPath, outputDir + "/diagnostic");
        System.out.println();
        
        System.out.println("=== 测试完成 ===");
        System.out.println("请检查以下文件：");
        System.out.println("1. solution1_builtin.pdf - 内置字体方案");
        System.out.println("2. solution2_default.pdf - 默认字体方案");
        System.out.println("3. solution3_forced.pdf - 强制子集方案");
        System.out.println("4. diagnostic/ - 诊断测试结果");
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
    private static byte[] readAllBytes(java.io.InputStream inputStream) throws IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[8192];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}
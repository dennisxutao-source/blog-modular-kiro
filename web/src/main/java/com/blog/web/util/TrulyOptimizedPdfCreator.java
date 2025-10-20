package com.blog.web.util;

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
import java.util.Map;

/**
 * 真正优化的PDF创建工具
 * 解决38MB问题的最终方案
 */
public class TrulyOptimizedPdfCreator {
    
    /**
     * 创建真正压缩的PDF - 去掉所有可能导致大文件的因素
     */
    public static void createTrulyOptimizedPDF(Map<String, String> dataMap, String mouldPath,
                                             String outPutPath, String signatureImgPath) {
        
        System.out.println("开始创建真正优化的PDF...");
        
        PdfReader reader = null;
        PdfStamper stamper = null;
        
        try {
            reader = new PdfReader(mouldPath);
            
            // 关键：直接输出到最终文件，不使用ByteArrayOutputStream和PdfCopy
            stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 设置最强压缩
            PdfWriter writer = stamper.getWriter();
            writer.setCompressionLevel(9); // 最高压缩级别
            writer.setPdfVersion(PdfWriter.VERSION_1_5);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 关键：使用系统字体，绝对不嵌入
            BaseFont bfChinese = null;
            try {
                // 方案1：使用Helvetica（最安全）
                bfChinese = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                System.out.println("使用Helvetica字体（不嵌入）");
            } catch (Exception e1) {
                try {
                    // 方案2：使用系统中文字体
                    bfChinese = BaseFont.createFont("SimSun", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                    System.out.println("使用SimSun字体（不嵌入）");
                } catch (Exception e2) {
                    System.out.println("警告：无法创建字体，将使用默认字体");
                }
            }
            
            // 填充文字数据
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    String value = dataMap.get(key);
                    try {
                        if (bfChinese != null) {
                            form.setFieldProperty(key, "textfont", bfChinese, null);
                        }
                        form.setField(key, value);
                        System.out.println("填充字段: " + key + " = " + value);
                    } catch (Exception e) {
                        System.out.println("字段填充失败: " + key + ", 错误: " + e.getMessage());
                    }
                }
            }
            
            // 处理图片（如果存在）
            if (signatureImgPath != null && new File(signatureImgPath).exists()) {
                processImageOptimized(form, stamper, signatureImgPath);
            }
            
            // 扁平化表单
            stamper.setFormFlattening(true);
            
        } catch (Exception e) {
            System.out.println("PDF创建失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 确保资源正确关闭
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
            System.out.println("PDF创建完成！");
            System.out.println("文件大小: " + formatSize(fileSize));
            
            if (fileSize > 5 * 1024 * 1024) { // 大于5MB
                System.out.println("⚠️  警告：文件仍然很大，可能存在其他问题");
            } else if (fileSize < 1024 * 1024) { // 小于1MB
                System.out.println("✅ 成功：文件大小正常");
            }
        }
    }
    
    /**
     * 优化的图片处理
     */
    private static void processImageOptimized(AcroFields form, PdfStamper stamper, String imagePath) {
        try {
            System.out.println("处理签名图片: " + imagePath);
            
            // 查找图片字段
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
                        
                        // 关键：压缩图片
                        image.setCompressionLevel(9);
                        
                        PdfContentByte under = stamper.getOverContent(pageNo);
                        image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                        image.setAbsolutePosition(x, y);
                        under.addImage(image);
                        
                        System.out.println("图片处理完成: " + fieldName);
                        break; // 只处理第一个找到的图片字段
                        
                    } catch (Exception e) {
                        System.out.println("图片字段处理失败: " + fieldName + ", 错误: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("图片处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 超级简化版本 - 如果上面的还是不行，用这个
     */
    public static void createSuperSimplePDF(Map<String, String> dataMap, String mouldPath, String outPutPath) {
        System.out.println("使用超级简化版本...");
        
        try {
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 最基本的压缩
            stamper.getWriter().setCompressionLevel(9);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            
            // 不设置任何字体，只填充数据
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    try {
                        form.setField(key, dataMap.get(key));
                    } catch (Exception e) {
                        // 忽略字段错误
                    }
                }
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            File outputFile = new File(outPutPath);
            System.out.println("超级简化版PDF大小: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("超级简化版失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 诊断版本 - 逐步测试每个步骤
     */
    public static void createDiagnosticPDF(Map<String, String> dataMap, String mouldPath, String outPutPath) {
        System.out.println("=== 诊断模式 ===");
        
        try {
            // 步骤1：只读取和写入，不做任何修改
            System.out.println("步骤1：基础读写测试");
            PdfReader reader = new PdfReader(mouldPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath + "_step1.pdf"));
            stamper.close();
            reader.close();
            
            File step1File = new File(outPutPath + "_step1.pdf");
            System.out.println("步骤1结果: " + formatSize(step1File.length()));
            
            // 步骤2：添加压缩设置
            System.out.println("步骤2：添加压缩设置");
            reader = new PdfReader(mouldPath);
            stamper = new PdfStamper(reader, new FileOutputStream(outPutPath + "_step2.pdf"));
            stamper.getWriter().setCompressionLevel(9);
            stamper.setFullCompression();
            stamper.close();
            reader.close();
            
            File step2File = new File(outPutPath + "_step2.pdf");
            System.out.println("步骤2结果: " + formatSize(step2File.length()));
            
            // 步骤3：添加表单扁平化
            System.out.println("步骤3：添加表单扁平化");
            reader = new PdfReader(mouldPath);
            stamper = new PdfStamper(reader, new FileOutputStream(outPutPath + "_step3.pdf"));
            stamper.getWriter().setCompressionLevel(9);
            stamper.setFullCompression();
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            File step3File = new File(outPutPath + "_step3.pdf");
            System.out.println("步骤3结果: " + formatSize(step3File.length()));
            
            // 步骤4：添加字段填充（不设置字体）
            System.out.println("步骤4：添加字段填充");
            reader = new PdfReader(mouldPath);
            stamper = new PdfStamper(reader, new FileOutputStream(outPutPath + "_step4.pdf"));
            stamper.getWriter().setCompressionLevel(9);
            stamper.setFullCompression();
            
            AcroFields form = stamper.getAcroFields();
            if (dataMap != null) {
                for (String key : dataMap.keySet()) {
                    try {
                        form.setField(key, dataMap.get(key));
                    } catch (Exception e) {
                        // 忽略
                    }
                }
            }
            
            stamper.setFormFlattening(true);
            stamper.close();
            reader.close();
            
            File step4File = new File(outPutPath + "_step4.pdf");
            System.out.println("步骤4结果: " + formatSize(step4File.length()));
            
        } catch (Exception e) {
            System.out.println("诊断失败: " + e.getMessage());
            e.printStackTrace();
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
}
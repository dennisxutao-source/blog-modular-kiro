package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 极简PDF创建工具
 * 用于诊断文件大小问题
 */
public class UltraMinimalPdfCreator {
    
    /**
     * 方法1: 完全不修改PDF，只是复制
     */
    public static void justCopyPdf(String templatePath, String outputPath) {
        System.out.println("测试1: 只复制PDF，不做任何修改");
        
        try {
            PdfReader reader = new PdfReader(templatePath);
            
            try (OutputStream out = new FileOutputStream(outputPath)) {
                Document document = new Document();
                PdfCopy copy = new PdfCopy(document, out);
                document.open();
                
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    PdfImportedPage page = copy.getImportedPage(reader, i);
                    copy.addPage(page);
                }
                
                document.close();
            }
            
            reader.close();
            
            File outputFile = new File(outputPath);
            System.out.println("只复制结果: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("只复制失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 方法2: 只填充文字，不使用任何字体
     */
    public static void fillTextWithoutFont(String templatePath, String outputPath, Map<String, String> data) {
        System.out.println("测试2: 填充文字但不设置字体");
        
        try {
            PdfReader reader = new PdfReader(templatePath);
            
            try (OutputStream out = new FileOutputStream(outputPath);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                
                PdfStamper stamper = new PdfStamper(reader, bos);
                
                // 启用最大压缩
                PdfWriter writer = stamper.getWriter();
                writer.setCompressionLevel(9);
                writer.setPdfVersion(PdfWriter.VERSION_1_5);
                stamper.setFullCompression();
                
                AcroFields form = stamper.getAcroFields();
                
                // 只填充文字，不设置字体
                if (data != null) {
                    for (String key : data.keySet()) {
                        String value = data.get(key);
                        try {
                            form.setField(key, value);
                        } catch (Exception e) {
                            // 忽略字段设置错误
                        }
                    }
                }
                
                stamper.setFormFlattening(true);
                stamper.close();
                
                // 复制到最终文件
                Document document = new Document();
                PdfCopy copy = new PdfCopy(document, out);
                document.open();
                
                PdfReader finalReader = new PdfReader(bos.toByteArray());
                for (int i = 1; i <= finalReader.getNumberOfPages(); i++) {
                    PdfImportedPage page = copy.getImportedPage(finalReader, i);
                    copy.addPage(page);
                }
                
                document.close();
                finalReader.close();
            }
            
            reader.close();
            
            File outputFile = new File(outputPath);
            System.out.println("无字体填充结果: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("无字体填充失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 方法3: 只处理图片，不处理文字
     */
    public static void onlyProcessImage(String templatePath, String outputPath, String imagePath) {
        System.out.println("测试3: 只处理图片，不处理文字");
        
        try {
            PdfReader reader = new PdfReader(templatePath);
            
            try (OutputStream out = new FileOutputStream(outputPath);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                
                PdfStamper stamper = new PdfStamper(reader, bos);
                
                // 启用压缩
                PdfWriter writer = stamper.getWriter();
                writer.setCompressionLevel(9);
                stamper.setFullCompression();
                
                // 只处理图片
                if (imagePath != null && new File(imagePath).exists()) {
                    AcroFields form = stamper.getAcroFields();
                    
                    // 查找图片字段
                    for (String fieldName : form.getFields().keySet()) {
                        if (fieldName.toLowerCase().contains("sign") || 
                            fieldName.toLowerCase().contains("image")) {
                            
                            try {
                                int pageNo = form.getFieldPositions(fieldName).get(0).page;
                                Rectangle signRect = form.getFieldPositions(fieldName).get(0).position;
                                float x = signRect.getLeft();
                                float y = signRect.getBottom();
                                
                                Image image = Image.getInstance(imagePath);
                                PdfContentByte under = stamper.getOverContent(pageNo);
                                image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                                image.setAbsolutePosition(x, y);
                                under.addImage(image);
                                
                                break; // 只处理第一个图片字段
                            } catch (Exception e) {
                                // 忽略图片处理错误
                            }
                        }
                    }
                }
                
                stamper.setFormFlattening(true);
                stamper.close();
                
                // 复制到最终文件
                Document document = new Document();
                PdfCopy copy = new PdfCopy(document, out);
                document.open();
                
                PdfReader finalReader = new PdfReader(bos.toByteArray());
                for (int i = 1; i <= finalReader.getNumberOfPages(); i++) {
                    PdfImportedPage page = copy.getImportedPage(finalReader, i);
                    copy.addPage(page);
                }
                
                document.close();
                finalReader.close();
            }
            
            reader.close();
            
            File outputFile = new File(outputPath);
            System.out.println("只处理图片结果: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("只处理图片失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 方法4: 最简单的stamper处理
     */
    public static void simpleStamperTest(String templatePath, String outputPath) {
        System.out.println("测试4: 最简单的stamper处理");
        
        try {
            PdfReader reader = new PdfReader(templatePath);
            
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                PdfStamper stamper = new PdfStamper(reader, out);
                
                // 什么都不做，直接关闭
                stamper.close();
            }
            
            reader.close();
            
            File outputFile = new File(outputPath);
            System.out.println("简单stamper结果: " + formatSize(outputFile.length()));
            
        } catch (Exception e) {
            System.out.println("简单stamper失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 运行所有测试
     */
    public static void runAllTests(String templatePath, String outputDir) {
        System.out.println("=== 极简PDF处理测试 ===");
        System.out.println("模板文件: " + templatePath);
        
        // 检查模板文件
        File templateFile = new File(templatePath);
        if (!templateFile.exists()) {
            System.out.println("❌ 模板文件不存在!");
            return;
        }
        
        System.out.println("模板文件大小: " + formatSize(templateFile.length()));
        System.out.println();
        
        // 创建输出目录
        new File(outputDir).mkdirs();
        
        // 测试1: 只复制
        justCopyPdf(templatePath, outputDir + "/test1_copy.pdf");
        
        // 测试2: 简单stamper
        simpleStamperTest(templatePath, outputDir + "/test2_stamper.pdf");
        
        // 测试3: 填充文字无字体
        Map<String, String> testData = new HashMap<>();
        testData.put("name", "测试");
        testData.put("company", "测试公司");
        fillTextWithoutFont(templatePath, outputDir + "/test3_text.pdf", testData);
        
        // 测试4: 只处理图片（如果有图片的话）
        // onlyProcessImage(templatePath, outputDir + "/test4_image.pdf", "signature.png");
        
        System.out.println("\n=== 测试完成 ===");
        System.out.println("请检查 " + outputDir + " 目录下的测试文件");
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
     * 主测试方法
     */
    public static void main(String[] args) {
        // 替换为你的实际模板路径
        String templatePath = "your_template.pdf";
        String outputDir = "test_output";
        
        runAllTests(templatePath, outputDir);
    }
}
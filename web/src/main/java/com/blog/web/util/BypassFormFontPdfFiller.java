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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.io.File;

/**
 * 绕过表单字体设置的PDF填充工具
 * 直接在PDF上绘制文字，不依赖表单字体设置
 */
public class BypassFormFontPdfFiller {
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BypassFormFontPdfFiller.class);
    
    /**
     * 绕过表单字体设置的PDF创建方法
     */
    public static void createBypassFontPDF(Map<String, String> dataMap, String mouldPath, 
                                         String outPutPath, String signatureImgPath) 
            throws UnsupportedEncodingException, FileNotFoundException {
        
        System.out.println("=== 绕过表单字体PDF创建开始 ===");
        System.out.println("策略：直接绘制文字，不依赖表单字体设置");
        
        PdfReader reader = null;
        PdfStamper stamper = null;
        
        try {
            // 1. 创建字体
            BaseFont bfChinese = createBypassFont();
            Font font = new Font(bfChinese, 12);
            
            // 2. 读取PDF模板
            reader = new PdfReader(mouldPath);
            System.out.println("PDF模板页数: " + reader.getNumberOfPages());
            
            // 3. 创建stamper
            stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 4. 获取表单字段
            AcroFields form = stamper.getAcroFields();
            System.out.println("表单字段数量: " + form.getFields().size());
            
            // 5. 方法A：先尝试清空表单字段，然后直接绘制文字
            if (dataMap != null && !dataMap.isEmpty()) {
                drawTextDirectly(stamper, form, dataMap, font);
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
        
        System.out.println("=== 绕过表单字体PDF创建完成 ===");
    }
    
    /**
     * 创建绕过字体
     */
    private static BaseFont createBypassFont() throws DocumentException, IOException {
        System.out.println("\n--- 创建绕过字体 ---");
        
        try {
            String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
            File fontFile = new File(fontPath);
            
            if (fontFile.exists()) {
                // 尝试使用最小嵌入策略
                BaseFont font = BaseFont.createFont(
                    fontPath, 
                    BaseFont.IDENTITY_H, 
                    BaseFont.NOT_EMBEDDED
                );
                
                System.out.println("✅ 思源字体创建成功");
                System.out.println("PostScript名称: " + font.getPostscriptFontName());
                
                return font;
            } else {
                throw new RuntimeException("字体文件不存在");
            }
        } catch (Exception e) {
            System.out.println("思源字体创建失败: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 直接绘制文字（绕过表单字体设置）
     */
    private static void drawTextDirectly(PdfStamper stamper, AcroFields form, 
                                       Map<String, String> dataMap, Font font) 
            throws IOException, DocumentException {
        
        System.out.println("\n--- 直接绘制文字 ---");
        System.out.println("使用字体: " + font.getBaseFont().getPostscriptFontName());
        
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
                        
                        // 清空原字段（设置为空值）
                        form.setField(key, "");
                        
                        // 直接在PDF上绘制文字
                        PdfContentByte canvas = stamper.getOverContent(pageNum);
                        
                        // 设置字体和大小
                        canvas.beginText();
                        canvas.setFontAndSize(font.getBaseFont(), 12);
                        
                        // 计算文字位置（字段的左下角）
                        float x = rect.getLeft() + 2; // 稍微向右偏移
                        float y = rect.getBottom() + 3; // 稍微向上偏移
                        
                        // 绘制文字
                        canvas.setTextMatrix(x, y);
                        canvas.showText(value);
                        canvas.endText();
                        
                        fieldCount++;
                        
                        if (fieldCount <= 3) {
                            System.out.println("直接绘制: " + key + " = " + value + 
                                             " (位置: " + x + ", " + y + ")");
                        }
                        
                    } else {
                        System.out.println("⚠️  未找到字段位置: " + key);
                    }
                    
                } catch (Exception e) {
                    System.out.println("直接绘制字段 " + key + " 失败: " + e.getMessage());
                }
            }
        }
        
        System.out.println("总共直接绘制字段数: " + fieldCount);
    }
    
    /**
     * 添加签名图片
     */
    private static void addSignatureImage(PdfStamper stamper, AcroFields form, String signatureImgPath) 
            throws IOException, DocumentException {
        
        System.out.println("\n--- 添加签名图片 ---");
        
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
                
                Image image = Image.getInstance(signatureImgPath);
                PdfContentByte under = stamper.getOverContent(pageNo);
                
                image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                image.setAbsolutePosition(signRect.getLeft(), signRect.getBottom());
                
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
                    System.out.println("可能原因：Identity-H编码导致字体被嵌入");
                } else if (sizeKB > 2000) {
                    System.out.println("⚠️  文件较大但可接受 (" + sizeKB + " KB)");
                } else {
                    System.out.println("✅ 文件大小正常 (" + sizeKB + " KB)");
                }
                
                System.out.println("\n💡 关键点：");
                System.out.println("1. 这个方法绕过了表单字体设置");
                System.out.println("2. 直接使用思源字体绘制文字");
                System.out.println("3. 应该能在PDF中看到思源字体效果");
                System.out.println("4. 如果文件仍然很大，说明Identity-H编码导致了字体嵌入");
            }
        } catch (Exception e) {
            System.out.println("文件大小检查失败: " + e.getMessage());
        }
    }
}
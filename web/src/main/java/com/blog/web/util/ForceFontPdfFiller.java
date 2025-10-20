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
 * 强制字体设置的PDF表单填充工具
 * 尝试解决字体被覆盖的问题
 */
public class ForceFontPdfFiller {
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ForceFontPdfFiller.class);
    
    /**
     * 强制字体设置的PDF创建方法
     */
    public static void createForceFontPDF(Map<String, String> dataMap, String mouldPath, 
                                        String outPutPath, String signatureImgPath) 
            throws UnsupportedEncodingException, FileNotFoundException {
        
        System.out.println("=== 强制字体PDF创建开始 ===");
        System.out.println("模板路径: " + mouldPath);
        System.out.println("输出路径: " + outPutPath);
        
        PdfReader reader = null;
        PdfStamper stamper = null;
        
        try {
            // 1. 创建字体
            BaseFont bfChinese = createForceFont();
            
            // 2. 读取PDF模板
            reader = new PdfReader(mouldPath);
            System.out.println("PDF模板页数: " + reader.getNumberOfPages());
            
            // 3. 创建stamper
            stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 4. 获取表单字段
            AcroFields form = stamper.getAcroFields();
            System.out.println("表单字段数量: " + form.getFields().size());
            
            // 5. 强制设置所有字段的字体（在填充之前）
            forceSetAllFieldsFonts(form, bfChinese);
            
            // 6. 填充文字数据
            if (dataMap != null && !dataMap.isEmpty()) {
                fillTextFieldsForce(form, dataMap, bfChinese);
            }
            
            // 7. 再次强制设置字体（在填充之后）
            forceSetAllFieldsFonts(form, bfChinese);
            
            // 8. 添加签名图片
            if (signatureImgPath != null && !signatureImgPath.isEmpty()) {
                addSignatureImage(stamper, form, signatureImgPath);
            }
            
            // 9. 设置表单不可编辑
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
        
        System.out.println("=== 强制字体PDF创建完成 ===");
    }
    
    /**
     * 创建强制字体
     */
    private static BaseFont createForceFont() throws DocumentException, IOException {
        System.out.println("\n--- 创建强制字体 ---");
        
        // 直接使用Identity-H编码（因为UniGB-UCS2-H失败了）
        try {
            String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
            File fontFile = new File(fontPath);
            
            System.out.println("使用思源字体 + Identity-H编码");
            System.out.println("字体路径: " + fontPath);
            System.out.println("文件存在: " + fontFile.exists());
            
            if (fontFile.exists()) {
                BaseFont font = BaseFont.createFont(
                    fontPath, 
                    BaseFont.IDENTITY_H, 
                    BaseFont.NOT_EMBEDDED  // 仍然尝试不嵌入
                );
                
                System.out.println("✅ 思源字体创建成功");
                System.out.println("PostScript名称: " + font.getPostscriptFontName());
                System.out.println("全名: " + font.getFullFontName());
                System.out.println("族名: " + font.getFamilyFontName());
                
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
     * 强制设置所有字段的字体
     */
    private static void forceSetAllFieldsFonts(AcroFields form, BaseFont bfChinese) {
        System.out.println("\n--- 强制设置所有字段字体 ---");
        System.out.println("设置字体: " + bfChinese.getPostscriptFontName());
        
        try {
            Map<String, AcroFields.Item> allFields = form.getFields();
            int successCount = 0;
            int failCount = 0;
            
            for (String fieldName : allFields.keySet()) {
                try {
                    // 强制设置字体
                    form.setFieldProperty(fieldName, "textfont", bfChinese, null);
                    
                    // 尝试设置字体大小
                    form.setFieldProperty(fieldName, "textsize", Float.valueOf(12), null);
                    
                    successCount++;
                    
                } catch (Exception e) {
                    failCount++;
                    if (failCount <= 3) {
                        System.out.println("字段 " + fieldName + " 字体设置失败: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("字体设置结果: 成功 " + successCount + " 个，失败 " + failCount + " 个");
            
        } catch (Exception e) {
            System.out.println("批量字体设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 强制填充文字字段
     */
    private static void fillTextFieldsForce(AcroFields form, Map<String, String> dataMap, BaseFont bfChinese) 
            throws IOException, DocumentException {
        
        System.out.println("\n--- 强制填充文字字段 ---");
        
        int fieldCount = 0;
        
        for (String key : dataMap.keySet()) {
            String value = dataMap.get(key);
            if (value != null) {
                try {
                    // 填充前再次设置字体
                    form.setFieldProperty(key, "textfont", bfChinese, null);
                    
                    // 设置字段值
                    form.setField(key, value);
                    
                    // 填充后再次设置字体
                    form.setFieldProperty(key, "textfont", bfChinese, null);
                    
                    fieldCount++;
                    
                    if (fieldCount <= 3) {
                        System.out.println("强制填充字段: " + key + " = " + value);
                    }
                    
                } catch (Exception e) {
                    System.out.println("强制填充字段 " + key + " 失败: " + e.getMessage());
                }
            }
        }
        
        System.out.println("总共强制填充字段数: " + fieldCount);
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
                    System.out.println("这可能是因为Identity-H编码导致字体被嵌入");
                } else if (sizeKB > 2000) {
                    System.out.println("⚠️  文件较大但可接受 (" + sizeKB + " KB)");
                } else {
                    System.out.println("✅ 文件大小正常 (" + sizeKB + " KB)");
                }
                
                System.out.println("\n💡 下一步：");
                System.out.println("1. 使用PdfFontAnalyzer分析生成的PDF");
                System.out.println("2. 如果仍显示SimSun，可能是PDF模板的问题");
                System.out.println("3. 考虑使用不同的PDF处理方法");
            }
        } catch (Exception e) {
            System.out.println("文件大小检查失败: " + e.getMessage());
        }
    }
}
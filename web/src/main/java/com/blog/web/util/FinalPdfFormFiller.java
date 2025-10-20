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
 * 最终版PDF表单填充工具
 * 解决所有字体和路径问题
 */
public class FinalPdfFormFiller {
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FinalPdfFormFiller.class);
    
    /**
     * 最终版本的PDF创建方法
     * 解决字体显示和文件大小问题
     */
    public static void createFinalPDF(Map<String, String> dataMap, String mouldPath, 
                                    String outPutPath, String signatureImgPath) 
            throws UnsupportedEncodingException, FileNotFoundException {
        
        System.out.println("=== 最终PDF创建开始 ===");
        System.out.println("模板路径: " + mouldPath);
        System.out.println("输出路径: " + outPutPath);
        System.out.println("当前工作目录: " + System.getProperty("user.dir"));
        
        PdfReader reader = null;
        PdfStamper stamper = null;
        
        try {
            // 1. 创建字体（确保路径正确）
            BaseFont bfChinese = createFinalFont();
            
            // 2. 读取PDF模板
            reader = new PdfReader(mouldPath);
            System.out.println("PDF模板页数: " + reader.getNumberOfPages());
            
            // 3. 创建stamper
            stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 4. 获取表单字段
            AcroFields form = stamper.getAcroFields();
            System.out.println("表单字段数量: " + form.getFields().size());
            
            // 5. 填充文字数据
            if (dataMap != null && !dataMap.isEmpty()) {
                fillTextFieldsFinal(form, dataMap, bfChinese);
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
        
        System.out.println("=== 最终PDF创建完成 ===");
    }
    
    /**
     * 创建最终字体（确保路径正确）
     */
    private static BaseFont createFinalFont() throws DocumentException, IOException {
        System.out.println("\n--- 创建最终字体 ---");
        
        // 方案1：使用正确路径的Noto Serif字体 + UniGB-UCS2-H编码
        try {
            String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
            File fontFile = new File(fontPath);
            
            System.out.println("尝试字体路径: " + fontPath);
            System.out.println("绝对路径: " + fontFile.getAbsolutePath());
            System.out.println("文件存在: " + fontFile.exists());
            
            if (fontFile.exists()) {
                System.out.println("使用Noto Serif字体 + UniGB-UCS2-H编码");
                
                BaseFont font = BaseFont.createFont(
                    fontPath, 
                    "UniGB-UCS2-H",  // 关键：使用传统编码避免嵌入问题
                    BaseFont.NOT_EMBEDDED  // 确保不嵌入
                );
                
                System.out.println("✅ Noto Serif字体创建成功 (UniGB-UCS2-H)");
                System.out.println("PostScript名称: " + font.getPostscriptFontName());
                
                // 验证字体名称
                if (font.getPostscriptFontName().contains("NotoSerif")) {
                    System.out.println("✅ 字体验证成功：确实是思源字体");
                    return font;
                } else {
                    System.out.println("⚠️  字体验证警告：PostScript名称不包含NotoSerif");
                }
                
                return font;
            } else {
                System.out.println("❌ 字体文件不存在");
            }
        } catch (Exception e) {
            System.out.println("Noto Serif (UniGB-UCS2-H) 失败: " + e.getMessage());
        }
        
        // 方案2：尝试Identity-H编码（可能导致嵌入）
        try {
            String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
            File fontFile = new File(fontPath);
            
            if (fontFile.exists()) {
                System.out.println("尝试Noto Serif字体 + Identity-H编码");
                
                BaseFont font = BaseFont.createFont(
                    fontPath, 
                    BaseFont.IDENTITY_H, 
                    BaseFont.NOT_EMBEDDED
                );
                
                System.out.println("⚠️  Noto Serif字体创建成功 (Identity-H) - 可能导致文件变大");
                System.out.println("PostScript名称: " + font.getPostscriptFontName());
                return font;
            }
        } catch (Exception e) {
            System.out.println("Noto Serif (Identity-H) 失败: " + e.getMessage());
        }
        
        // 方案3：系统字体回退
        String[] systemFonts = {
            "STSong-Light",
            "SimSun"
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
     * 最终的文字字段填充
     */
    private static void fillTextFieldsFinal(AcroFields form, Map<String, String> dataMap, BaseFont bfChinese) 
            throws IOException, DocumentException {
        
        System.out.println("\n--- 最终的文字字段填充 ---");
        System.out.println("使用字体: " + bfChinese.getPostscriptFontName());
        
        int fieldCount = 0;
        
        for (String key : dataMap.keySet()) {
            String value = dataMap.get(key);
            if (value != null) {
                try {
                    // 关键：每次填充前都设置字体
                    form.setFieldProperty(key, "textfont", bfChinese, null);
                    
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
        
        // 批量设置所有字段的字体（确保没有遗漏）
        try {
            Map<String, AcroFields.Item> allFields = form.getFields();
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
                    System.out.println("可能原因：");
                    System.out.println("1. 使用了Identity-H编码导致字体嵌入");
                    System.out.println("2. PDF模板本身很大");
                    System.out.println("3. 图片文件过大");
                    System.out.println("建议：检查生成的PDF使用了什么字体");
                } else if (sizeKB > 2000) {
                    System.out.println("⚠️  文件较大但可接受 (" + sizeKB + " KB)");
                } else {
                    System.out.println("✅ 文件大小正常 (" + sizeKB + " KB)");
                }
                
                // 给出下一步建议
                System.out.println("\n💡 下一步：");
                System.out.println("1. 使用PdfFontAnalyzer分析生成的PDF");
                System.out.println("2. 检查PDF中显示的字体是否为思源字体");
                System.out.println("3. 如果仍显示SimSun，可能需要进一步调试");
            }
        } catch (Exception e) {
            System.out.println("文件大小检查失败: " + e.getMessage());
        }
    }
}
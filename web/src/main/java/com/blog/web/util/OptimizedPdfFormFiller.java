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
import java.util.HashMap;
import java.util.Map;
import java.io.File;

/**
 * 优化的PDF表单填充工具
 * 解决37MB文件大小问题
 */
public class OptimizedPdfFormFiller {
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OptimizedPdfFormFiller.class);
    
    /**
     * 优化版本的PDF创建方法
     * 解决37MB问题
     */
    public static void createOptimizedPDF(Map<String, String> dataMap, String mouldPath, 
                                        String outPutPath, String signatureImgPath) 
            throws UnsupportedEncodingException, FileNotFoundException {
        
        System.out.println("=== 优化PDF创建开始 ===");
        System.out.println("模板路径: " + mouldPath);
        System.out.println("输出路径: " + outPutPath);
        System.out.println("签名图片: " + signatureImgPath);
        
        PdfReader reader = null;
        PdfStamper stamper = null;
        
        try {
            // 1. 创建优化的字体
            BaseFont bfChinese = createOptimizedFont();
            
            // 2. 读取PDF模板
            reader = new PdfReader(mouldPath);
            System.out.println("PDF模板页数: " + reader.getNumberOfPages());
            
            // 3. 直接输出到最终文件（避免多次复制）
            stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
            
            // 4. 获取表单字段
            AcroFields form = stamper.getAcroFields();
            System.out.println("表单字段数量: " + form.getFields().size());
            
            // 5. 填充文字数据
            if (dataMap != null && !dataMap.isEmpty()) {
                fillTextFields(form, dataMap, bfChinese);
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
        
        System.out.println("=== 优化PDF创建完成 ===");
    }
    
    /**
     * 创建优化的字体
     */
    private static BaseFont createOptimizedFont() throws DocumentException, IOException {
        System.out.println("\n--- 创建优化字体 ---");
        
        try {
            // 方案1：尝试使用项目中的Noto Serif字体（不嵌入）
            String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
            File fontFile = new File(fontPath);
            
            if (fontFile.exists()) {
                System.out.println("使用Noto Serif字体: " + fontPath);
                BaseFont font = BaseFont.createFont(
                    fontPath, 
                    BaseFont.IDENTITY_H, 
                    BaseFont.NOT_EMBEDDED  // 关键：确保不嵌入
                );
                System.out.println("✅ Noto Serif字体创建成功: " + font.getPostscriptFontName());
                return font;
            }
        } catch (Exception e) {
            System.out.println("Noto Serif字体创建失败: " + e.getMessage());
        }
        
        try {
            // 方案2：使用STSong-Light系统字体
            System.out.println("尝试使用STSong-Light系统字体");
            BaseFont font = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            System.out.println("✅ STSong-Light字体创建成功: " + font.getPostscriptFontName());
            return font;
        } catch (Exception e) {
            System.out.println("STSong-Light字体创建失败: " + e.getMessage());
        }
        
        // 方案3：最终回退
        System.out.println("使用Helvetica回退字体");
        BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        System.out.println("⚠️  使用回退字体: " + font.getPostscriptFontName());
        return font;
    }
    
    /**
     * 填充文字字段
     */
    private static void fillTextFields(AcroFields form, Map<String, String> dataMap, BaseFont bfChinese) 
            throws IOException, DocumentException {
        
        System.out.println("\n--- 填充文字字段 ---");
        int fieldCount = 0;
        
        for (String key : dataMap.keySet()) {
            String value = dataMap.get(key);
            if (value != null) {
                // 关键优化：只设置一次字体，避免重复嵌入
                form.setFieldProperty(key, "textfont", bfChinese, null);
                form.setField(key, value);
                fieldCount++;
                
                if (fieldCount <= 5) { // 只打印前5个字段，避免日志过多
                    System.out.println("填充字段: " + key + " = " + value);
                }
            }
        }
        
        System.out.println("总共填充字段数: " + fieldCount);
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
        
        // 查找签名图片字段
        String signatureFieldKey = "signatureImag"; // 根据你的代码
        
        if (form.getFieldPositions(signatureFieldKey) != null && 
            !form.getFieldPositions(signatureFieldKey).isEmpty()) {
            
            int pageNo = form.getFieldPositions(signatureFieldKey).get(0).page;
            Rectangle signRect = form.getFieldPositions(signatureFieldKey).get(0).position;
            
            float x = signRect.getLeft();
            float y = signRect.getBottom();
            
            Image image = Image.getInstance(signatureImgPath);
            PdfContentByte under = stamper.getOverContent(pageNo);
            
            // 图片大小自适应
            image.scaleToFit(signRect.getWidth(), signRect.getHeight());
            image.setAbsolutePosition(x, y);
            
            under.addImage(image);
            
            System.out.println("✅ 签名图片添加成功");
            System.out.println("图片大小: " + (imgFile.length() / 1024) + " KB");
            
        } else {
            System.out.println("⚠️  未找到签名图片字段: " + signatureFieldKey);
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
        
        // 删除临时签名图片
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
                System.out.println("文件路径: " + outputFile.getAbsolutePath());
                System.out.println("文件大小: " + sizeBytes + " bytes");
                System.out.println("文件大小: " + sizeKB + " KB");
                System.out.println("文件大小: " + sizeMB + " MB");
                
                if (sizeMB > 10) {
                    System.out.println("❌ 警告：文件仍然过大 (" + sizeMB + " MB)");
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
            Map<String, String> testData = new HashMap<>();
            testData.put("name", "测韦欣");
            testData.put("id", "eg.10004730");
            testData.put("content", "这是测试内容");
            
            // 注意：需要提供实际的模板文件路径
            String templatePath = "path/to/your/template.pdf";
            String outputPath = "optimized_output.pdf";
            String signaturePath = "path/to/signature.png";
            
            createOptimizedPDF(testData, templatePath, outputPath, signaturePath);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.blog.web.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 有效的PDF压缩工具类
 * 专门针对iText 5.5.11，真正解决PDF文件大小问题
 */
public class EffectivePdfCompressor {
    
    private static final String NOTO_SERIF_REGULAR = "fonts/NotoSerifCJKsc-Regular.otf";
    
    /**
     * 生成最小文件大小的PDF (推荐方案)
     * 使用不嵌入字体，文件大小约200KB
     */
    public static byte[] createMinimalSizePdf(InputStream templateStream, 
                                            Map<String, String> fieldValues) 
            throws IOException, DocumentException {
        
        PdfReader reader = new PdfReader(templateStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);
        
        // iText 5.5.11 关键压缩设置
        PdfWriter writer = stamper.getWriter();
        writer.setCompressionLevel(9); // 最高压缩级别
        writer.setPdfVersion(PdfWriter.VERSION_1_5);
        stamper.setFullCompression();
        
        AcroFields form = stamper.getAcroFields();
        
        // 使用不嵌入字体 - 这是减小文件大小的关键
        BaseFont baseFont = createNotEmbeddedFont();
        
        // 填充表单字段
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            
            form.setField(fieldName, fieldValue);
            form.setFieldProperty(fieldName, "textfont", baseFont, null);
            form.setFieldProperty(fieldName, "textsize", 12f, null);
        }
        
        // 扁平化表单
        stamper.setFormFlattening(true);
        
        stamper.close();
        reader.close();
        
        System.out.println("生成最小PDF完成，预计大小: ~200KB");
        return baos.toByteArray();
    }
    
    /**
     * 生成子集化字体PDF
     * 平衡文件大小和兼容性，文件大小约1-3MB
     */
    public static byte[] createSubsetPdf(InputStream templateStream, 
                                       Map<String, String> fieldValues) 
            throws IOException, DocumentException {
        
        PdfReader reader = new PdfReader(templateStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);
        
        // 压缩设置
        PdfWriter writer = stamper.getWriter();
        writer.setCompressionLevel(9); // 最高压缩级别
        writer.setPdfVersion(PdfWriter.VERSION_1_5);
        stamper.setFullCompression();
        
        AcroFields form = stamper.getAcroFields();
        
        // 使用子集化字体
        BaseFont baseFont = createSubsetFont();
        
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            
            form.setField(fieldName, fieldValue);
            form.setFieldProperty(fieldName, "textfont", baseFont, null);
            form.setFieldProperty(fieldName, "textsize", 12f, null);
        }
        
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
        
        System.out.println("生成子集PDF完成，预计大小: 1-3MB");
        return baos.toByteArray();
    }
    
    /**
     * 创建不嵌入字体 - 最小文件大小
     */
    public static BaseFont createNotEmbeddedFont() throws DocumentException, IOException {
        // 方法1: 尝试使用系统中文字体
        try {
            return BaseFont.createFont(
                "SimSun", 
                "UniGB-UCS2-H", 
                BaseFont.NOT_EMBEDDED
            );
        } catch (Exception e1) {
            try {
                return BaseFont.createFont(
                    "Microsoft YaHei", 
                    "UniGB-UCS2-H", 
                    BaseFont.NOT_EMBEDDED
                );
            } catch (Exception e2) {
                // 最后的回退方案
                return BaseFont.createFont(
                    BaseFont.HELVETICA, 
                    BaseFont.CP1252, 
                    BaseFont.NOT_EMBEDDED
                );
            }
        }
    }
    
    /**
     * 创建子集化字体 - iText 5.5.11正确方法
     */
    public static BaseFont createSubsetFont() throws DocumentException, IOException {
        ClassPathResource resource = new ClassPathResource(NOTO_SERIF_REGULAR);
        if (!resource.exists()) {
            System.out.println("字体文件不存在，使用系统字体");
            return createNotEmbeddedFont();
        }
        
        try (InputStream is = resource.getInputStream()) {
            byte[] fontBytes = readAllBytes(is);
            
            BaseFont font = BaseFont.createFont(
                NOTO_SERIF_REGULAR,
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED,  // 嵌入字体
                true,
                fontBytes,
                null
            );
            
            // iText 5.5.11 关键步骤：必须在创建后立即设置子集化
            font.setSubset(true);
            
            System.out.println("子集化字体创建成功，isSubset: " + font.isSubset());
            return font;
        }
    }
    
    /**
     * 测试压缩效果
     */
    public static void testCompressionEffectiveness(InputStream templateStream, 
                                                  Map<String, String> fieldValues) {
        System.out.println("=== PDF压缩效果测试 ===");
        
        try {
            // 测试1: 最小文件
            byte[] minimalPdf = createMinimalSizePdf(templateStream, fieldValues);
            System.out.println("最小文件PDF: " + formatSize(minimalPdf.length));
            
            // 重置流
            templateStream.reset();
            
            // 测试2: 子集字体
            byte[] subsetPdf = createSubsetPdf(templateStream, fieldValues);
            System.out.println("子集字体PDF: " + formatSize(subsetPdf.length));
            
            // 计算压缩比
            if (subsetPdf.length > 0 && minimalPdf.length > 0) {
                double ratio = (double) minimalPdf.length / subsetPdf.length;
                System.out.println("最小文件比子集文件小 " + String.format("%.1f", ratio) + " 倍");
            }
            
        } catch (Exception e) {
            System.out.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("========================");
    }
    
    /**
     * 验证字体设置
     */
    public static void validateFontSettings() {
        System.out.println("=== 字体设置验证 ===");
        
        try {
            BaseFont notEmbeddedFont = createNotEmbeddedFont();
            BaseFont subsetFont = createSubsetFont();
            
            System.out.println("不嵌入字体创建: " + (notEmbeddedFont != null ? "成功" : "失败"));
            System.out.println("子集字体创建: " + (subsetFont != null ? "成功" : "失败"));
            System.out.println("子集字体启用状态: " + subsetFont.isSubset());
            
        } catch (Exception e) {
            System.out.println("验证失败: " + e.getMessage());
        }
        
        System.out.println("==================");
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
    
    /**
     * 使用建议
     */
    public static void printUsageRecommendations() {
        System.out.println("=== 使用建议 ===");
        System.out.println("1. 内部系统使用: createMinimalSizePdf()");
        System.out.println("   - 文件最小 (~200KB)");
        System.out.println("   - 依赖系统字体");
        System.out.println();
        System.out.println("2. 外部分发使用: createSubsetPdf()");
        System.out.println("   - 文件较大 (1-3MB)");
        System.out.println("   - 兼容性好，包含字体");
        System.out.println("===============");
    }
}
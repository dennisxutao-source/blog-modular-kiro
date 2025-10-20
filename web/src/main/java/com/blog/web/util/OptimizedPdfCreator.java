package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 优化的PDF创建工具类
 * 基于你的原始代码，添加了压缩功能
 */
public class OptimizedPdfCreator {
    
    private static final String NOTO_SERIF_REGULAR = "fonts/NotoSerifCJKsc-Regular.otf";
    
    /**
     * 创建压缩的PDF - 基于你的原始方法优化
     */
    public static void createCompressedPDF(Map<String, String> dataMap, String mouldPath, 
                                         String outPutPath, String signatureImgPath) 
            throws UnsupportedEncodingException, FileNotFoundException {
        
        String certificatePDFTemplate = mouldPath;
        String imgPathQianming = signatureImgPath;
        
        // 封装图片和文字的map
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Map<String, String> imgMap = new HashMap<String, String>();
        imgMap.put("signatureImag", imgPathQianming);
        paramMap.put("imgMap", imgMap);
        paramMap.put("dataMap", dataMap);
        
        BaseFont bfChinese = null;
        try {
            // 使用优化的字体创建方法
            bfChinese = createOptimizedFont();
        } catch (DocumentException e) {
            System.out.println("设置字体DocumentException:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException:" + e.getMessage());
            e.printStackTrace();
        }
        
        PdfReader reader = null;
        PdfStamper stamper = null;
        PdfCopy copy = null;
        Document document = null;
        String outputFile = outPutPath;
        
        Map<String, String> data = (Map<String, String>) paramMap.get("dataMap");
        
        try {
            reader = new PdfReader(certificatePDFTemplate);
            
            try (OutputStream out = new FileOutputStream(outputFile);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                
                stamper = new PdfStamper(reader, bos);
                
                // 关键：添加压缩设置
                PdfWriter writer = stamper.getWriter();
                writer.setCompressionLevel(9); // 最高压缩级别
                writer.setPdfVersion(PdfWriter.VERSION_1_5);
                stamper.setFullCompression(); // 启用全压缩
                
                AcroFields form = stamper.getAcroFields();
                
                // 文字处理
                if (data != null) {
                    for (String key : data.keySet()) {
                        String value = data.get(key);
                        form.setFieldProperty(key, "textfont", bfChinese, null);
                        form.setField(key, value);
                    }
                }
                
                // 图片处理
                if (paramMap.containsKey("imgMap")) {
                    Map<String, String> img = (Map<String, String>) paramMap.get("imgMap");
                    if (img.get("signatureImag") != null) {
                        for (String key : img.keySet()) {
                            String value = img.get(key);
                            String imgPath = value;
                            int pageNo = form.getFieldPositions(key).get(0).page;
                            Rectangle signRect = form.getFieldPositions(key).get(0).position;
                            float x = signRect.getLeft();
                            float y = signRect.getBottom();
                            Image image = Image.getInstance(imgPath);
                            PdfContentByte under = stamper.getOverContent(pageNo);
                            image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                            image.setAbsolutePosition(x, y);
                            under.addImage(image);
                        }
                    }
                }
                
                stamper.setFormFlattening(true);
                stamper.close();
                
                document = new Document();
                copy = new PdfCopy(document, out);
                document.open();
                
                PdfImportedPage importPage = null;
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), i);
                    copy.addPage(importPage);
                }
                document.close();
                
                System.out.println("压缩PDF生成完成，文件大小: " + new File(outputFile).length() + " bytes");
            }
        } catch (IOException e) {
            System.out.println("生产PDF的IO流出错：" + e.getMessage());
            e.printStackTrace();
        } catch (DocumentException e) {
            System.out.println("生产PDF的DocumentException流出错：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 资源清理
            closeResources(stamper, copy, document, reader, imgPathQianming);
        }
    }
    
    /**
     * 创建最小文件大小的PDF - 推荐方法
     */
    public static void createMinimalPDF(Map<String, String> dataMap, String mouldPath, 
                                      String outPutPath, String signatureImgPath) 
            throws UnsupportedEncodingException, FileNotFoundException {
        
        String certificatePDFTemplate = mouldPath;
        String imgPathQianming = signatureImgPath;
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Map<String, String> imgMap = new HashMap<String, String>();
        imgMap.put("signatureImag", imgPathQianming);
        paramMap.put("imgMap", imgMap);
        paramMap.put("dataMap", dataMap);
        
        BaseFont bfChinese = null;
        try {
            // 使用不嵌入字体 - 最小文件大小
            bfChinese = createMinimalFont();
        } catch (DocumentException e) {
            System.out.println("设置字体DocumentException:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException:" + e.getMessage());
            e.printStackTrace();
        }
        
        PdfReader reader = null;
        PdfStamper stamper = null;
        PdfCopy copy = null;
        Document document = null;
        String outputFile = outPutPath;
        
        Map<String, String> data = (Map<String, String>) paramMap.get("dataMap");
        
        try {
            reader = new PdfReader(certificatePDFTemplate);
            
            try (OutputStream out = new FileOutputStream(outputFile);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                
                stamper = new PdfStamper(reader, bos);
                
                // 最大压缩设置
                PdfWriter writer = stamper.getWriter();
                writer.setCompressionLevel(9);
                writer.setPdfVersion(PdfWriter.VERSION_1_5);
                stamper.setFullCompression();
                
                AcroFields form = stamper.getAcroFields();
                
                // 文字处理
                if (data != null) {
                    for (String key : data.keySet()) {
                        String value = data.get(key);
                        form.setFieldProperty(key, "textfont", bfChinese, null);
                        form.setField(key, value);
                    }
                }
                
                // 图片处理（与原代码相同）
                if (paramMap.containsKey("imgMap")) {
                    Map<String, String> img = (Map<String, String>) paramMap.get("imgMap");
                    if (img.get("signatureImag") != null) {
                        for (String key : img.keySet()) {
                            String value = img.get(key);
                            String imgPath = value;
                            int pageNo = form.getFieldPositions(key).get(0).page;
                            Rectangle signRect = form.getFieldPositions(key).get(0).position;
                            float x = signRect.getLeft();
                            float y = signRect.getBottom();
                            Image image = Image.getInstance(imgPath);
                            PdfContentByte under = stamper.getOverContent(pageNo);
                            image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                            image.setAbsolutePosition(x, y);
                            under.addImage(image);
                        }
                    }
                }
                
                stamper.setFormFlattening(true);
                stamper.close();
                
                document = new Document();
                copy = new PdfCopy(document, out);
                document.open();
                
                PdfImportedPage importPage = null;
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), i);
                    copy.addPage(importPage);
                }
                document.close();
                
                long fileSize = new File(outputFile).length();
                System.out.println("最小PDF生成完成，文件大小: " + formatSize(fileSize));
            }
        } catch (IOException e) {
            System.out.println("生产PDF的IO流出错：" + e.getMessage());
            e.printStackTrace();
        } catch (DocumentException e) {
            System.out.println("生产PDF的DocumentException流出错：" + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(stamper, copy, document, reader, imgPathQianming);
        }
    }
    
    /**
     * 创建优化字体 - 子集化嵌入
     */
    private static BaseFont createOptimizedFont() throws DocumentException, IOException {
        ClassPathResource resource = new ClassPathResource(NOTO_SERIF_REGULAR);
        if (!resource.exists()) {
            System.out.println("字体文件不存在，使用系统字体");
            return createMinimalFont();
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
            
            // 启用子集化 - 减小文件大小的关键
            font.setSubset(true);
            
            System.out.println("子集化字体创建成功，预计文件大小: 1-3MB");
            return font;
        }
    }
    
    /**
     * 创建最小字体 - 不嵌入字体
     */
    private static BaseFont createMinimalFont() throws DocumentException, IOException {
        try {
            BaseFont font = BaseFont.createFont(
                "SimSun",
                "UniGB-UCS2-H",
                BaseFont.NOT_EMBEDDED
            );
            System.out.println("不嵌入字体创建成功，预计文件大小: 200KB以下");
            return font;
        } catch (Exception e1) {
            try {
                return BaseFont.createFont(
                    "Microsoft YaHei",
                    "UniGB-UCS2-H",
                    BaseFont.NOT_EMBEDDED
                );
            } catch (Exception e2) {
                return BaseFont.createFont(
                    BaseFont.HELVETICA,
                    BaseFont.CP1252,
                    BaseFont.NOT_EMBEDDED
                );
            }
        }
    }
    
    /**
     * 资源清理
     */
    private static void closeResources(PdfStamper stamper, PdfCopy copy, Document document, 
                                     PdfReader reader, String imgPathQianming) {
        if (stamper != null) {
            try {
                stamper.close();
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
            }
        }
        if (copy != null) {
            copy.close();
        }
        if (document != null) {
            document.close();
        }
        if (reader != null) {
            reader.close();
        }
        if (imgPathQianming != null) {
            File f = new File(imgPathQianming);
            f.delete();
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
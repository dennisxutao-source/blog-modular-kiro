package com.blog.web.util;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;

import java.io.File;
import java.util.Set;

public class SimplePdfAnalyzer {
    
    public static void main(String[] args) {
        try {
            String pdfPath = "/Users/taotao/Downloads/eg.10004730+测韦欣+测新字体 (3).pdf";
            
            System.out.println("=== 简单PDF分析 ===");
            System.out.println("文件路径: " + pdfPath);
            
            analyzePdfBasicInfo(pdfPath);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void analyzePdfBasicInfo(String pdfPath) {
        try {
            File pdfFile = new File(pdfPath);
            if (!pdfFile.exists()) {
                System.out.println("❌ 文件不存在: " + pdfPath);
                return;
            }
            
            long fileSizeBytes = pdfFile.length();
            long fileSizeMB = fileSizeBytes / (1024 * 1024);
            
            System.out.println("\n--- 基本信息 ---");
            System.out.println("文件大小: " + fileSizeBytes + " bytes (" + fileSizeMB + " MB)");
            
            if (fileSizeMB > 30) {
                System.out.println("❌ 确认问题：文件确实过大 (" + fileSizeMB + " MB)");
            }
            
            PdfReader reader = new PdfReader(pdfPath);
            
            System.out.println("页数: " + reader.getNumberOfPages());
            System.out.println("PDF版本: " + reader.getPdfVersion());
            
            // 简单字体分析
            analyzeSimpleFonts(reader);
            
            // 分析对象数量
            int totalObjects = reader.getXrefSize();
            System.out.println("总对象数: " + totalObjects);
            
            reader.close();
            
            // 给出诊断结论
            provideDiagnosis(fileSizeMB);
            
        } catch (Exception e) {
            System.out.println("分析失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void analyzeSimpleFonts(PdfReader reader) {
        System.out.println("\n--- 字体信息 ---");
        
        try {
            boolean foundEmbeddedFont = false;
            
            for (int pageNum = 1; pageNum <= reader.getNumberOfPages(); pageNum++) {
                PdfDictionary page = reader.getPageN(pageNum);
                PdfDictionary resources = page.getAsDict(PdfName.RESOURCES);
                
                if (resources != null) {
                    PdfDictionary fonts = resources.getAsDict(PdfName.FONT);
                    if (fonts != null) {
                        Set<PdfName> fontNames = fonts.getKeys();
                        
                        if (!fontNames.isEmpty()) {
                            System.out.println("第" + pageNum + "页发现 " + fontNames.size() + " 个字体:");
                            
                            for (PdfName fontName : fontNames) {
                                PdfDictionary font = fonts.getAsDict(fontName);
                                if (font != null) {
                                    // 字体名称
                                    PdfName baseFontName = font.getAsName(PdfName.BASEFONT);
                                    System.out.println("  字体: " + fontName + " -> " + baseFontName);
                                    
                                    // 检查是否有字体描述符（通常表示嵌入字体）
                                    PdfObject fontDescriptor = font.get(PdfName.FONTDESCRIPTOR);
                                    if (fontDescriptor != null && fontDescriptor.isDictionary()) {
                                        PdfDictionary fd = (PdfDictionary) fontDescriptor;
                                        
                                        // 检查嵌入字体标志
                                        boolean hasEmbeddedFont = fd.get(PdfName.FONTFILE) != null || 
                                                                fd.get(PdfName.FONTFILE2) != null || 
                                                                fd.get(PdfName.FONTFILE3) != null;
                                        
                                        if (hasEmbeddedFont) {
                                            foundEmbeddedFont = true;
                                            System.out.println("    ❌ 发现嵌入字体！");
                                            
                                            // 尝试获取字体文件对象信息
                                            PdfObject fontFileObj = fd.get(PdfName.FONTFILE2);
                                            if (fontFileObj == null) fontFileObj = fd.get(PdfName.FONTFILE);
                                            if (fontFileObj == null) fontFileObj = fd.get(PdfName.FONTFILE3);
                                            
                                            if (fontFileObj != null) {
                                                System.out.println("    字体文件对象: " + fontFileObj);
                                            }
                                        } else {
                                            System.out.println("    ✅ 未嵌入字体");
                                        }
                                    } else {
                                        System.out.println("    ✅ 未嵌入字体（无字体描述符）");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if (foundEmbeddedFont) {
                System.out.println("\n❌ 结论：PDF中包含嵌入字体，这可能是37MB的原因！");
            } else {
                System.out.println("\n✅ 结论：PDF中没有发现嵌入字体");
            }
            
        } catch (Exception e) {
            System.out.println("字体分析失败: " + e.getMessage());
        }
    }
    
    private static void provideDiagnosis(long fileSizeMB) {
        System.out.println("\n--- 诊断结论 ---");
        
        if (fileSizeMB > 30) {
            System.out.println("🔍 37MB PDF问题确认！");
            System.out.println("\n可能的原因：");
            System.out.println("1. ❌ 字体被意外嵌入了（最可能）");
            System.out.println("   - 检查代码中是否使用了 BaseFont.EMBEDDED");
            System.out.println("   - 检查是否使用了字体文件路径 + EMBEDDED");
            System.out.println("   - 某些情况下 iText 可能自动嵌入字体");
            
            System.out.println("\n2. ❌ 大量重复内容");
            System.out.println("   - 检查是否有重复的大对象");
            System.out.println("   - 检查是否有未压缩的内容流");
            
            System.out.println("\n3. ❌ 图片或其他资源");
            System.out.println("   - 检查是否包含大图片");
            System.out.println("   - 检查是否有其他大资源");
            
            System.out.println("\n🔧 解决方案：");
            System.out.println("1. 确保使用 BaseFont.NOT_EMBEDDED");
            System.out.println("2. 检查实际的字体创建代码");
            System.out.println("3. 使用我们测试过的最优方案：");
            System.out.println("   BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED)");
            
        } else {
            System.out.println("✅ 文件大小正常");
        }
    }
}
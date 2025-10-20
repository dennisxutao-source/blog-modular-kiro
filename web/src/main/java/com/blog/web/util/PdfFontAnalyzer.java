package com.blog.web.util;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;

import java.io.File;
import java.util.Set;

public class PdfFontAnalyzer {
    
    public static void main(String[] args) {
        try {
            String pdfPath = "/Users/taotao/Downloads/eg.10004730+测韦欣+测新字体 (7).pdf";
            
            System.out.println("=== PDF字体分析工具 ===");
            System.out.println("分析文件: " + pdfPath);
            
            analyzePdfFonts(pdfPath);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void analyzePdfFonts(String pdfPath) {
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
            
            PdfReader reader = new PdfReader(pdfPath);
            
            System.out.println("页数: " + reader.getNumberOfPages());
            System.out.println("PDF版本: " + reader.getPdfVersion());
            
            // 分析字体信息
            analyzeDetailedFonts(reader);
            
            reader.close();
            
            // 给出结论
            provideFontConclusion(fileSizeMB);
            
        } catch (Exception e) {
            System.out.println("分析失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void analyzeDetailedFonts(PdfReader reader) {
        System.out.println("\n--- 详细字体分析 ---");
        
        boolean foundNotoSerif = false;
        boolean foundSimSun = false;
        boolean foundEmbeddedFont = false;
        
        try {
            for (int pageNum = 1; pageNum <= reader.getNumberOfPages(); pageNum++) {
                PdfDictionary page = reader.getPageN(pageNum);
                PdfDictionary resources = page.getAsDict(PdfName.RESOURCES);
                
                if (resources != null) {
                    PdfDictionary fonts = resources.getAsDict(PdfName.FONT);
                    if (fonts != null) {
                        Set<PdfName> fontNames = fonts.getKeys();
                        
                        if (!fontNames.isEmpty()) {
                            System.out.println("\n第" + pageNum + "页发现 " + fontNames.size() + " 个字体:");
                            
                            for (PdfName fontName : fontNames) {
                                PdfDictionary font = fonts.getAsDict(fontName);
                                if (font != null) {
                                    System.out.println("  字体引用: " + fontName);
                                    
                                    // 字体类型
                                    PdfName subtype = font.getAsName(PdfName.SUBTYPE);
                                    System.out.println("    类型: " + subtype);
                                    
                                    // 字体名称
                                    PdfName baseFontName = font.getAsName(PdfName.BASEFONT);
                                    System.out.println("    基础字体: " + baseFontName);
                                    
                                    // 检查是否是思源字体
                                    if (baseFontName != null) {
                                        String fontNameStr = baseFontName.toString();
                                        if (fontNameStr.contains("NotoSerif") || 
                                            fontNameStr.contains("SourceHan") ||
                                            fontNameStr.contains("思源")) {
                                            foundNotoSerif = true;
                                            System.out.println("    ✅ 发现思源字体！");
                                        }
                                        
                                        if (fontNameStr.contains("SimSun")) {
                                            foundSimSun = true;
                                            System.out.println("    ⚠️  发现SimSun字体");
                                        }
                                    }
                                    
                                    // 检查字体描述符和嵌入情况
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
                                            
                                            // 尝试获取嵌入字体的大小信息
                                            PdfObject fontFileObj = fd.get(PdfName.FONTFILE2);
                                            if (fontFileObj == null) fontFileObj = fd.get(PdfName.FONTFILE);
                                            if (fontFileObj == null) fontFileObj = fd.get(PdfName.FONTFILE3);
                                            
                                            if (fontFileObj != null) {
                                                System.out.println("    字体文件对象类型: " + fontFileObj.type());
                                                System.out.println("    这可能是37MB问题的原因！");
                                            }
                                        } else {
                                            System.out.println("    ✅ 未嵌入字体");
                                        }
                                        
                                        // 获取字体标志
                                        PdfObject flags = fd.get(PdfName.FLAGS);
                                        if (flags != null) {
                                            System.out.println("    字体标志: " + flags);
                                        }
                                        
                                        // 获取字体名称信息
                                        PdfObject fontNameObj = fd.get(PdfName.FONTNAME);
                                        if (fontNameObj != null) {
                                            System.out.println("    字体名称: " + fontNameObj);
                                        }
                                        
                                    } else {
                                        System.out.println("    ✅ 未嵌入字体（无字体描述符）");
                                    }
                                    
                                    // 检查编码
                                    PdfObject encoding = font.get(PdfName.ENCODING);
                                    if (encoding != null) {
                                        System.out.println("    编码: " + encoding);
                                    }
                                    
                                    System.out.println(); // 空行分隔
                                }
                            }
                        }
                    }
                }
            }
            
            // 总结发现
            System.out.println("=== 字体发现总结 ===");
            System.out.println("发现思源字体: " + (foundNotoSerif ? "是" : "否"));
            System.out.println("发现SimSun字体: " + (foundSimSun ? "是" : "否"));
            System.out.println("发现嵌入字体: " + (foundEmbeddedFont ? "是" : "否"));
            
        } catch (Exception e) {
            System.out.println("字体分析失败: " + e.getMessage());
        }
    }
    
    private static void provideFontConclusion(long fileSizeMB) {
        System.out.println("\n=== 分析结论 ===");
        
        if (fileSizeMB > 30) {
            System.out.println("🔍 文件确实过大 (" + fileSizeMB + " MB)");
            System.out.println("这表明存在以下问题之一：");
            System.out.println("1. ❌ 字体被嵌入了（尽管可能设置了NOT_EMBEDDED）");
            System.out.println("2. ❌ PDF中包含了大量其他内容");
            System.out.println("3. ❌ PDF处理过程中出现了重复或冗余数据");
        } else if (fileSizeMB > 5) {
            System.out.println("⚠️  文件较大但可能可接受 (" + fileSizeMB + " MB)");
        } else {
            System.out.println("✅ 文件大小正常 (" + fileSizeMB + " MB)");
        }
        
        System.out.println("\n💡 建议：");
        System.out.println("1. 如果发现了嵌入字体，这就是文件过大的主要原因");
        System.out.println("2. 如果显示思源字体但文件仍然很大，说明字体被意外嵌入了");
        System.out.println("3. 如果显示SimSun但代码中使用思源字体，说明字体加载失败了");
        System.out.println("4. 使用优化的PDF创建方法可以解决这个问题");
    }
}
package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;

public class PdfSizeAnalyzer {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== PDF大小问题分析 ===");
            
            analyzeFontEmbedding();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void analyzeFontEmbedding() {
        System.out.println("\n--- 分析字体嵌入行为 ---");
        
        String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
        String testText = "测试中文：你好世界！\nTest English: Hello World!";
        
        try {
            // 测试1：明确指定 NOT_EMBEDDED
            System.out.println("\n1. 测试 BaseFont.NOT_EMBEDDED:");
            BaseFont font1 = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            String pdf1 = "analysis_not_embedded.pdf";
            createTestPdf(pdf1, font1, testText, "NOT_EMBEDDED");
            
            File f1 = new File(pdf1);
            System.out.println("   文件大小: " + f1.length() + " bytes (" + (f1.length() / 1024) + " KB)");
            
            // 测试2：明确指定 EMBEDDED
            System.out.println("\n2. 测试 BaseFont.EMBEDDED:");
            BaseFont font2 = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            String pdf2 = "analysis_embedded.pdf";
            createTestPdf(pdf2, font2, testText, "EMBEDDED");
            
            File f2 = new File(pdf2);
            System.out.println("   文件大小: " + f2.length() + " bytes (" + (f2.length() / 1024) + " KB)");
            
            // 测试3：使用 CACHED（默认行为）
            System.out.println("\n3. 测试 BaseFont.CACHED:");
            BaseFont font3 = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.CACHED);
            String pdf3 = "analysis_cached.pdf";
            createTestPdf(pdf3, font3, testText, "CACHED");
            
            File f3 = new File(pdf3);
            System.out.println("   文件大小: " + f3.length() + " bytes (" + (f3.length() / 1024) + " KB)");
            
            // 分析结果
            System.out.println("\n--- 分析结果 ---");
            System.out.println("NOT_EMBEDDED vs EMBEDDED 差异: " + (f2.length() - f1.length()) + " bytes");
            System.out.println("NOT_EMBEDDED vs CACHED 差异: " + (f3.length() - f1.length()) + " bytes");
            
            if (f1.length() > 1000000) { // 大于1MB
                System.out.println("❌ 问题确认：NOT_EMBEDDED版本仍然很大，说明字体被嵌入了！");
                System.out.println("   可能原因：");
                System.out.println("   1. iText 5.5.11 对 OTF 字体的 NOT_EMBEDDED 支持有问题");
                System.out.println("   2. IDENTITY_H 编码可能强制嵌入字体");
                System.out.println("   3. 字体文件本身的限制");
            } else {
                System.out.println("✅ NOT_EMBEDDED 工作正常，文件大小合理");
            }
            
            // 测试不同编码方式
            testDifferentEncodings(fontPath, testText);
            
        } catch (Exception e) {
            System.out.println("分析失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testDifferentEncodings(String fontPath, String testText) {
        System.out.println("\n--- 测试不同编码方式 ---");
        
        String[] encodings = {
            BaseFont.IDENTITY_H,
            "UniGB-UCS2-H",
            "GBK-EUC-H",
            "GB-EUC-H"
        };
        
        for (String encoding : encodings) {
            try {
                System.out.println("\n测试编码: " + encoding);
                BaseFont font = BaseFont.createFont(fontPath, encoding, BaseFont.NOT_EMBEDDED);
                String pdfName = "encoding_test_" + encoding.replace("-", "_") + ".pdf";
                createTestPdf(pdfName, font, testText, "NOT_EMBEDDED + " + encoding);
                
                File pdfFile = new File(pdfName);
                System.out.println("   文件大小: " + pdfFile.length() + " bytes (" + (pdfFile.length() / 1024) + " KB)");
                
                if (pdfFile.length() < 100000) { // 小于100KB
                    System.out.println("   ✅ 这个编码方式文件较小，可能真的没有嵌入字体");
                } else {
                    System.out.println("   ❌ 文件仍然很大，可能嵌入了字体");
                }
                
            } catch (Exception e) {
                System.out.println("   编码测试失败: " + e.getMessage());
            }
        }
    }
    
    private static void createTestPdf(String filename, BaseFont baseFont, String text, String description) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        
        Font font = new Font(baseFont, 12);
        
        document.add(new Paragraph("PDF大小分析测试", font));
        document.add(new Paragraph("配置: " + description, font));
        document.add(new Paragraph("字体PostScript名称: " + baseFont.getPostscriptFontName(), font));
        document.add(new Paragraph(" ", font));
        document.add(new Paragraph("测试内容:", font));
        document.add(new Paragraph(text, font));
        
        // 添加更多内容来测试字体嵌入
        document.add(new Paragraph(" ", font));
        document.add(new Paragraph("常用中文字符测试:", font));
        document.add(new Paragraph("的一是在不了有和人这中大为上个国我以要他时来用们生到作地于出就分对成会可主发年动同工也能下过子说产种面而方后多定行学法所民得经十三之进着等部度家电力里如水化高自二理起小物现实加量都两体制机当使点从业本去把性好应开它合还因由其些然前外天政四日那社义事平形相全表间样与关各重新线内数正心反你明看原又么利比或但质气第向道命此变条只没结解问意建月公无系军很情者最立代想已通并提直题党程展五果料象员革位入常文总次品式活设及管特件长求老头基资边流路级少图山统接知较将组见计别她手角期根论运农指几九区强放决西被干做必战先回则任取据处队南给色光门即保治北造百规热领七海口东导器压志世金增争济阶油思术极交受联什认六共权收证改清己美再采转更单风切打白教速花带安场身车例真务具万每目至达走积示议声报斗完类八离华名确才科张信马节话米整空元况今集温传土许步群广石记需段研界拉林律叫且究观越织装影算低持音众书布复容儿须际商非验连断深难近矿千周委素技备半办青省列习响约支般史感劳便团往酸历市克何除消构府称太准精值号率族维划选标写存候毛亲快效斯院查江型眼王按格养易置派层片始却专状育厂京识适属圆包火住调满县局照参红细引听该铁价严", font));
        
        document.close();
    }
}
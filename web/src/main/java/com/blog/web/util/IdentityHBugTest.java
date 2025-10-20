package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;

public class IdentityHBugTest {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Identity-H编码Bug测试 ===");
            
            testIdentityHBug();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testIdentityHBug() {
        System.out.println("\n--- 测试Identity-H编码可能的Bug ---");
        
        String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
        String testText = "测试中文：你好世界！测韦欣 eg.10004730";
        
        // 测试1：Identity-H + NOT_EMBEDDED（你的方法）
        testEncodingCombination(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 
                               "Identity-H + NOT_EMBEDDED", testText);
        
        // 测试2：UniGB-UCS2-H + NOT_EMBEDDED（传统方法）
        testEncodingCombination(fontPath, "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED, 
                               "UniGB-UCS2-H + NOT_EMBEDDED", testText);
        
        // 测试3：Identity-H + EMBEDDED（明确嵌入）
        testEncodingCombination(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 
                               "Identity-H + EMBEDDED", testText);
        
        // 测试4：UniGB-UCS2-H + EMBEDDED（传统嵌入）
        testEncodingCombination(fontPath, "UniGB-UCS2-H", BaseFont.EMBEDDED, 
                               "UniGB-UCS2-H + EMBEDDED", testText);
    }
    
    private static void testEncodingCombination(String fontPath, String encoding, 
                                              boolean embedded, String description, String testText) {
        System.out.println("\n测试: " + description);
        
        try {
            BaseFont baseFont = BaseFont.createFont(fontPath, encoding, embedded);
            
            String fileName = "encoding_test_" + description.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
            
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            
            Font font = new Font(baseFont, 12);
            
            document.add(new Paragraph("编码测试: " + description, font));
            document.add(new Paragraph("字体PostScript名称: " + baseFont.getPostscriptFontName(), font));
            document.add(new Paragraph("编码: " + encoding, font));
            document.add(new Paragraph("嵌入设置: " + (embedded ? "EMBEDDED" : "NOT_EMBEDDED"), font));
            document.add(new Paragraph(" ", font));
            document.add(new Paragraph("测试内容: " + testText, font));
            
            // 添加大量中文内容来触发可能的问题
            String largeText = "大量中文测试：" +
                "的一是在不了有和人这中大为上个国我以要他时来用们生到作地于出就分对成会可主发年动同工也能下过子说产种面而方后多定行学法所民得经十三之进着等部度家电力里如水化高自二理起小物现实加量都两体制机当使点从业本去把性好应开它合还因由其些然前外天政四日那社义事平形相全表间样与关各重新线内数正心反你明看原又么利比或但质气第向道命此变条只没结解问意建月公无系军很情者最立代想已通并提直题党程展五果料象员革位入常文总次品式活设及管特件长求老头基资边流路级少图山统接知较将组见计别她手角期根论运农指几九区强放决西被干做必战先回则任取据处队南给色光门即保治北造百规热领七海口东导器压志世金增争济阶油思术极交受联什认六共权收证改清己美再采转更单风切打白教速花带安场身车例真务具万每目至达走积示议声报斗完类八离华名确才科张信马节话米整空元况今集温传土许步群广石记需段研界拉林律叫且究观越织装影算低持音众书布复容儿须际商非验连断深难近矿千周委素技备半办青省列习响约支般史感劳便团往酸历市克何除消构府称太准精值号率族维划选标写存候毛亲快效斯院查江型眼王按格养易置派层片始却专状育厂京识适属圆包火住调满县局照参红细引听该铁价严";
            
            document.add(new Paragraph(largeText, font));
            
            document.close();
            
            File pdfFile = new File(fileName);
            long sizeBytes = pdfFile.length();
            long sizeKB = sizeBytes / 1024;
            long sizeMB = sizeBytes / (1024 * 1024);
            
            System.out.println("字体PostScript名称: " + baseFont.getPostscriptFontName());
            System.out.println("PDF大小: " + sizeBytes + " bytes (" + sizeKB + " KB / " + sizeMB + " MB)");
            
            if (sizeMB > 10) {
                System.out.println("❌ 发现问题：" + description + " 导致文件过大！");
                System.out.println("   这可能就是37MB问题的根源！");
                
                if (!embedded && sizeMB > 10) {
                    System.out.println("   ❌ 严重Bug：设置了NOT_EMBEDDED但文件仍然巨大！");
                    System.out.println("   原因：" + encoding + " 编码可能强制嵌入字体");
                }
                
            } else if (sizeKB > 500) {
                System.out.println("⚠️  文件较大: " + sizeKB + " KB");
            } else {
                System.out.println("✅ 文件大小正常: " + sizeKB + " KB");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 测试失败: " + e.getMessage());
        }
    }
}
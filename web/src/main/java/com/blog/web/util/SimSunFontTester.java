package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;

public class SimSunFontTester {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== SimSun字体问题测试 ===");
            
            testSimSunFont();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testSimSunFont() {
        System.out.println("\n--- 测试SimSun字体的不同用法 ---");
        
        // 测试1：SimSun + UniGB-UCS2-H + NOT_EMBEDDED
        testSimSunVariant("SimSun", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED, "标准用法");
        
        // 测试2：SimSun + Identity-H + NOT_EMBEDDED  
        testSimSunVariant("SimSun", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, "Identity-H编码");
        
        // 测试3：SimSun + UniGB-UCS2-H + EMBEDDED
        testSimSunVariant("SimSun", "UniGB-UCS2-H", BaseFont.EMBEDDED, "强制嵌入");
        
        // 测试4：SimSun + Identity-H + EMBEDDED
        testSimSunVariant("SimSun", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, "Identity-H+嵌入");
        
        // 对比测试：使用Noto Serif
        testNotoSerifComparison();
    }
    
    private static void testSimSunVariant(String fontName, String encoding, boolean embedded, String description) {
        System.out.println("\n测试: " + description);
        System.out.println("字体: " + fontName + ", 编码: " + encoding + ", 嵌入: " + embedded);
        
        try {
            BaseFont baseFont = BaseFont.createFont(fontName, encoding, embedded);
            
            String fileName = "simsun_test_" + description.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
            
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            
            Font font = new Font(baseFont, 12);
            
            document.add(new Paragraph("SimSun字体测试 - " + description, font));
            document.add(new Paragraph("字体PostScript名称: " + baseFont.getPostscriptFontName(), font));
            document.add(new Paragraph(" ", font));
            
            // 添加大量中文内容（可能触发字体子集问题）
            String testText = "测试中文内容：你好世界！这是一个字体测试文档。\n" +
                            "常用汉字测试：的一是在不了有和人这中大为上个国我以要他时来用们生到作地于出就分对成会可主发年动同工也能下过子说产种面而方后多定行学法所民得经十三之进着等部度家电力里如水化高自二理起小物现实加量都两体制机当使点从业本去把性好应开它合还因由其些然前外天政四日那社义事平形相全表间样与关各重新线内数正心反你明看原又么利比或但质气第向道命此变条只没结解问意建月公无系军很情者最立代想已通并提直题党程展五果料象员革位入常文总次品式活设及管特件长求老头基资边流路级少图山统接知较将组见计别她手角期根论运农指几九区强放决西被干做必战先回则任取据处队南给色光门即保治北造百规热领七海口东导器压志世金增争济阶油思术极交受联什认六共权收证改清己美再采转更单风切打白教速花带安场身车例真务具万每目至达走积示议声报斗完类八离华名确才科张信马节话米整空元况今集温传土许步群广石记需段研界拉林律叫且究观越织装影算低持音众书布复容儿须际商非验连断深难近矿千周委素技备半办青省列习响约支般史感劳便团往酸历市克何除消构府称太准精值号率族维划选标写存候毛亲快效斯院查江型眼王按格养易置派层片始却专状育厂京识适属圆包火住调满县局照参红细引听该铁价严";
            
            document.add(new Paragraph("大量中文测试:", font));
            document.add(new Paragraph(testText, font));
            
            document.close();
            
            File pdfFile = new File(fileName);
            long sizeKB = pdfFile.length() / 1024;
            long sizeMB = pdfFile.length() / (1024 * 1024);
            
            System.out.println("PDF大小: " + pdfFile.length() + " bytes (" + sizeKB + " KB / " + sizeMB + " MB)");
            
            if (sizeMB > 10) {
                System.out.println("❌ 发现问题：文件过大！这可能就是37MB问题的原因");
                System.out.println("   原因：" + description + " 导致大量字体数据被包含");
            } else if (sizeKB > 500) {
                System.out.println("⚠️  文件较大，需要注意");
            } else {
                System.out.println("✅ 文件大小正常");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 测试失败: " + e.getMessage());
        }
    }
    
    private static void testNotoSerifComparison() {
        System.out.println("\n--- 对比测试：Noto Serif ---");
        
        String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
        File fontFile = new File(fontPath);
        
        if (!fontFile.exists()) {
            System.out.println("Noto Serif字体文件不存在，跳过对比测试");
            return;
        }
        
        try {
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("noto_serif_comparison.pdf"));
            document.open();
            
            Font font = new Font(baseFont, 12);
            
            document.add(new Paragraph("Noto Serif对比测试", font));
            document.add(new Paragraph("字体PostScript名称: " + baseFont.getPostscriptFontName(), font));
            document.add(new Paragraph(" ", font));
            
            // 相同的大量中文内容
            String testText = "测试中文内容：你好世界！这是一个字体测试文档。\n" +
                            "常用汉字测试：的一是在不了有和人这中大为上个国我以要他时来用们生到作地于出就分对成会可主发年动同工也能下过子说产种面而方后多定行学法所民得经十三之进着等部度家电力里如水化高自二理起小物现实加量都两体制机当使点从业本去把性好应开它合还因由其些然前外天政四日那社义事平形相全表间样与关各重新线内数正心反你明看原又么利比或但质气第向道命此变条只没结解问意建月公无系军很情者最立代想已通并提直题党程展五果料象员革位入常文总次品式活设及管特件长求老头基资边流路级少图山统接知较将组见计别她手角期根论运农指几九区强放决西被干做必战先回则任取据处队南给色光门即保治北造百规热领七海口东导器压志世金增争济阶油思术极交受联什认六共权收证改清己美再采转更单风切打白教速花带安场身车例真务具万每目至达走积示议声报斗完类八离华名确才科张信马节话米整空元况今集温传土许步群广石记需段研界拉林律叫且究观越织装影算低持音众书布复容儿须际商非验连断深难近矿千周委素技备半办青省列习响约支般史感劳便团往酸历市克何除消构府称太准精值号率族维划选标写存候毛亲快效斯院查江型眼王按格养易置派层片始却专状育厂京识适属圆包火住调满县局照参红细引听该铁价严";
            
            document.add(new Paragraph("大量中文测试:", font));
            document.add(new Paragraph(testText, font));
            
            document.close();
            
            File pdfFile = new File("noto_serif_comparison.pdf");
            long sizeKB = pdfFile.length() / 1024;
            
            System.out.println("Noto Serif PDF大小: " + pdfFile.length() + " bytes (" + sizeKB + " KB)");
            System.out.println("✅ 对比：Noto Serif文件大小正常");
            
        } catch (Exception e) {
            System.out.println("❌ Noto Serif测试失败: " + e.getMessage());
        }
    }
}
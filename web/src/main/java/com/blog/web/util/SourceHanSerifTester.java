package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;

public class SourceHanSerifTester {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== 思源宋体测试 ===");
            
            // 测试不同的思源宋体引用方式
            testSourceHanSerif();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testSourceHanSerif() {
        System.out.println("\n--- 测试思源宋体（不嵌入） ---");
        
        // 不同的思源宋体名称尝试
        String[] fontNames = {
            "Source Han Serif SC",      // 简体中文
            "Source Han Serif TC",      // 繁体中文
            "Source Han Serif",         // 通用名称
            "SourceHanSerifSC-Regular", // 具体字重
            "思源宋体",                  // 中文名称
            "NotoSerifCJKsc-Regular"    // Noto 版本
        };
        
        String testText = "这是思源宋体测试：你好世界！\nThis is Source Han Serif test: Hello World!\n数字测试：123456789";
        
        for (String fontName : fontNames) {
            System.out.println("\n尝试字体: " + fontName);
            
            try {
                // 不嵌入字体
                BaseFont baseFont = BaseFont.createFont(fontName, "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                String pdfName = "test_" + fontName.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
                
                createTestPdf(pdfName, baseFont, testText, fontName);
                
                File pdfFile = new File(pdfName);
                System.out.println("✅ 成功创建PDF: " + pdfName);
                System.out.println("   文件大小: " + pdfFile.length() + " bytes");
                System.out.println("   字体PostScript名称: " + baseFont.getPostscriptFontName());
                
            } catch (Exception e) {
                System.out.println("❌ 字体创建失败: " + e.getMessage());
            }
        }
        
        // 测试字体文件路径方式（不嵌入）
        testFontFilePath();
    }
    
    private static void testFontFilePath() {
        System.out.println("\n--- 测试字体文件路径（不嵌入） ---");
        
        // 检查项目中的字体文件
        String[] fontPaths = {
            "src/main/resources/fonts/NotoSerifCJK-Regular.ttc",
            "src/main/resources/fonts/SourceHanSerifSC-Regular.otf",
            "src/main/resources/fonts/思源宋体.ttf"
        };
        
        String testText = "字体文件路径测试：思源宋体显示效果\nFont file path test: Source Han Serif display effect";
        
        for (String fontPath : fontPaths) {
            File fontFile = new File(fontPath);
            if (fontFile.exists()) {
                System.out.println("\n找到字体文件: " + fontPath);
                try {
                    // 使用字体文件但不嵌入
                    BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                    String pdfName = "test_file_" + fontFile.getName().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
                    
                    createTestPdf(pdfName, baseFont, testText, fontPath);
                    
                    File pdfFileResult = new File(pdfName);
                    System.out.println("✅ 成功创建PDF: " + pdfName);
                    System.out.println("   文件大小: " + pdfFileResult.length() + " bytes");
                    System.out.println("   字体PostScript名称: " + baseFont.getPostscriptFontName());
                    
                } catch (Exception e) {
                    System.out.println("❌ 字体文件测试失败: " + e.getMessage());
                }
            } else {
                System.out.println("字体文件不存在: " + fontPath);
            }
        }
    }
    
    private static void createTestPdf(String filename, BaseFont baseFont, String text, String fontInfo) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        
        Font font = new Font(baseFont, 12);
        Font titleFont = new Font(baseFont, 16, Font.BOLD);
        
        document.add(new Paragraph("思源宋体测试文档", titleFont));
        document.add(new Paragraph("Source Han Serif Test Document", titleFont));
        document.add(new Paragraph(" "));
        
        document.add(new Paragraph("字体信息: " + fontInfo, font));
        document.add(new Paragraph("字体PostScript名称: " + baseFont.getPostscriptFontName(), font));
        document.add(new Paragraph("是否嵌入: 否（NOT_EMBEDDED）", font));
        document.add(new Paragraph(" "));
        
        document.add(new Paragraph("测试内容:", font));
        document.add(new Paragraph(text, font));
        document.add(new Paragraph(" "));
        
        // 添加更多中文测试
        document.add(new Paragraph("中文字符测试:", font));
        document.add(new Paragraph("常用汉字：的一是在不了有和人这中大为上个国我以要他时来用们生到作地于出就分对成会可主发年动同工也能下过子说产种面而方后多定行学法所民得经十三之进着等部度家电力里如水化高自二理起小物现实加量都两体制机当使点从业本去把性好应开它合还因由其些然前外天政四日那社义事平形相全表间样与关各重新线内数正心反你明看原又么利比或但质气第向道命此变条只没结解问意建月公无系军很情者最立代想已通并提直题党程展五果料象员革位入常文总次品式活设及管特件长求老头基资边流路级少图山统接知较将组见计别她手角期根论运农指几九区强放决西被干做必战先回则任取据处队南给色光门即保治北造百规热领七海口东导器压志世金增争济阶油思术极交受联什认六共权收证改清己美再采转更单风切打白教速花带安场身车例真务具万每目至达走积示议声报斗完类八离华名确才科张信马节话米整空元况今集温传土许步群广石记需段研界拉林律叫且究观越织装影算低持音众书布复容儿须际商非验连断深难近矿千周委素技备半办青省列习响约支般史感劳便团往酸历市克何除消构府称太准精值号率族维划选标写存候毛亲快效斯院查江型眼王按格养易置派层片始却专状育厂京识适属圆包火住调满县局照参红细引听该铁价严", font));
        
        document.close();
    }
}
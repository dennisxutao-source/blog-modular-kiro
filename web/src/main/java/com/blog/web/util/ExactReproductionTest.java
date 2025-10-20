package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;

public class ExactReproductionTest {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== 精确重现你的情况 ===");
            
            // 使用你说的完全相同的方法
            testExactMethod();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testExactMethod() {
        System.out.println("\n--- 使用你的确切方法 ---");
        
        // 你说使用的确切代码
        String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
        
        try {
            System.out.println("字体文件路径: " + fontPath);
            
            // 检查字体文件
            File fontFile = new File(fontPath);
            if (!fontFile.exists()) {
                System.out.println("❌ 字体文件不存在: " + fontPath);
                return;
            }
            
            System.out.println("✅ 字体文件存在");
            System.out.println("字体文件大小: " + (fontFile.length() / 1024 / 1024) + " MB");
            
            // 使用你说的确切方法
            BaseFont font = BaseFont.createFont(
                fontPath, 
                BaseFont.IDENTITY_H, 
                BaseFont.NOT_EMBEDDED  // 关键：确保不嵌入
            );
            
            System.out.println("✅ 字体创建成功");
            System.out.println("字体PostScript名称: " + font.getPostscriptFontName());
            
            // 创建PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("exact_reproduction_test.pdf"));
            document.open();
            
            Font pdfFont = new Font(font, 12);
            
            document.add(new Paragraph("精确重现测试", pdfFont));
            document.add(new Paragraph("字体路径: " + fontPath, pdfFont));
            document.add(new Paragraph("字体PostScript名称: " + font.getPostscriptFontName(), pdfFont));
            document.add(new Paragraph("嵌入设置: BaseFont.NOT_EMBEDDED", pdfFont));
            document.add(new Paragraph(" ", pdfFont));
            
            // 添加一些中文内容
            document.add(new Paragraph("中文测试：你好世界！", pdfFont));
            document.add(new Paragraph("测试姓名：测韦欣", pdfFont));
            document.add(new Paragraph("测试编号：eg.10004730", pdfFont));
            
            // 添加更多内容，模拟你的使用场景
            String testContent = "这是一个PDF字体测试文档。我们正在测试Noto Serif CJK字体的使用情况。" +
                               "常用汉字：的一是在不了有和人这中大为上个国我以要他时来用们生到作地于出就分对成会可主发年动同工也能下过子说产种面而方后多定行学法所民得经十三之进着等部度家电力里如水化高自二理起小物现实加量都两体制机当使点从业本去把性好应开它合还因由其些然前外天政四日那社义事平形相全表间样与关各重新线内数正心反你明看原又么利比或但质气第向道命此变条只没结解问意建月公无系军很情者最立代想已通并提直题党程展五果料象员革位入常文总次品式活设及管特件长求老头基资边流路级少图山统接知较将组见计别她手角期根论运农指几九区强放决西被干做必战先回则任取据处队南给色光门即保治北造百规热领七海口东导器压志世金增争济阶油思术极交受联什认六共权收证改清己美再采转更单风切打白教速花带安场身车例真务具万每目至达走积示议声报斗完类八离华名确才科张信马节话米整空元况今集温传土许步群广石记需段研界拉林律叫且究观越织装影算低持音众书布复容儿须际商非验连断深难近矿千周委素技备半办青省列习响约支般史感劳便团往酸历市克何除消构府称太准精值号率族维划选标写存候毛亲快效斯院查江型眼王按格养易置派层片始却专状育厂京识适属圆包火住调满县局照参红细引听该铁价严";
            
            document.add(new Paragraph("大量中文内容测试:", pdfFont));
            document.add(new Paragraph(testContent, pdfFont));
            
            document.close();
            
            // 检查结果
            File pdfFile = new File("exact_reproduction_test.pdf");
            long sizeBytes = pdfFile.length();
            long sizeKB = sizeBytes / 1024;
            long sizeMB = sizeBytes / (1024 * 1024);
            
            System.out.println("\n--- 测试结果 ---");
            System.out.println("PDF文件大小: " + sizeBytes + " bytes");
            System.out.println("PDF文件大小: " + sizeKB + " KB");
            System.out.println("PDF文件大小: " + sizeMB + " MB");
            
            if (sizeMB > 10) {
                System.out.println("❌ 重现了问题：文件过大 (" + sizeMB + " MB)");
                System.out.println("这确实是37MB问题！");
                
                // 进一步分析
                analyzeWhyLarge(font, fontPath);
                
            } else if (sizeKB > 500) {
                System.out.println("⚠️  文件较大但可接受 (" + sizeKB + " KB)");
            } else {
                System.out.println("✅ 文件大小正常 (" + sizeKB + " KB)");
                System.out.println("无法重现37MB问题，可能是其他因素导致");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void analyzeWhyLarge(BaseFont font, String fontPath) {
        System.out.println("\n--- 分析为什么文件这么大 ---");
        
        try {
            System.out.println("字体信息:");
            System.out.println("  PostScript名称: " + font.getPostscriptFontName());
            System.out.println("  字体路径: " + fontPath);
            
            // 检查字体文件本身
            File fontFile = new File(fontPath);
            System.out.println("  原始字体文件大小: " + (fontFile.length() / 1024 / 1024) + " MB");
            
            // 可能的原因分析
            System.out.println("\n可能的原因:");
            System.out.println("1. ❌ 尽管设置了NOT_EMBEDDED，但iText仍然嵌入了字体");
            System.out.println("2. ❌ Identity-H编码可能强制要求嵌入字体");
            System.out.println("3. ❌ 字体文件本身有问题");
            System.out.println("4. ❌ iText版本问题");
            
            // 建议解决方案
            System.out.println("\n建议解决方案:");
            System.out.println("1. 尝试使用不同的编码: UniGB-UCS2-H");
            System.out.println("2. 尝试使用系统字体而不是文件路径");
            System.out.println("3. 检查iText版本和配置");
            
        } catch (Exception e) {
            System.out.println("分析失败: " + e.getMessage());
        }
    }
}
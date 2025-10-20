package com.blog.web.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;

public class LargePdfDiagnostic {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== 37MB PDF问题诊断 ===");
            
            // 可能导致大文件的原因测试
            testPossibleCauses();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testPossibleCauses() {
        System.out.println("\n--- 可能导致37MB的原因测试 ---");
        
        String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
        
        // 原因1：大量文本内容
        System.out.println("\n1. 测试大量文本内容:");
        testLargeTextContent(fontPath);
        
        // 原因2：重复创建字体
        System.out.println("\n2. 测试重复创建字体:");
        testRepeatedFontCreation(fontPath);
        
        // 原因3：字体子集问题
        System.out.println("\n3. 测试字体子集问题:");
        testFontSubsetIssue(fontPath);
        
        // 原因4：编码问题
        System.out.println("\n4. 测试编码问题:");
        testEncodingIssue(fontPath);
    }
    
    private static void testLargeTextContent(String fontPath) {
        try {
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("test_large_content.pdf"));
            document.open();
            
            Font font = new Font(baseFont, 12);
            
            // 添加大量重复内容
            String largeText = "这是一个很长的测试文本，用来测试大量内容是否会导致PDF文件变大。";
            for (int i = 0; i < 1000; i++) {
                document.add(new Paragraph("第" + i + "段：" + largeText, font));
            }
            
            document.close();
            
            File file = new File("test_large_content.pdf");
            System.out.println("   大量内容PDF大小: " + file.length() + " bytes (" + (file.length() / 1024 / 1024) + " MB)");
            
        } catch (Exception e) {
            System.out.println("   大量内容测试失败: " + e.getMessage());
        }
    }
    
    private static void testRepeatedFontCreation(String fontPath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("test_repeated_fonts.pdf"));
            document.open();
            
            // 重复创建多个字体实例（错误做法）
            for (int i = 0; i < 10; i++) {
                BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                Font font = new Font(baseFont, 12);
                document.add(new Paragraph("使用第" + i + "个字体实例：测试文本", font));
            }
            
            document.close();
            
            File file = new File("test_repeated_fonts.pdf");
            System.out.println("   重复字体PDF大小: " + file.length() + " bytes (" + (file.length() / 1024) + " KB)");
            
        } catch (Exception e) {
            System.out.println("   重复字体测试失败: " + e.getMessage());
        }
    }
    
    private static void testFontSubsetIssue(String fontPath) {
        try {
            // 测试是否字体子集化有问题
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("test_font_subset.pdf"));
            document.open();
            
            Font font = new Font(baseFont, 12);
            
            // 使用大量不同的中文字符，可能触发字体子集问题
            String allChineseChars = "的一是在不了有和人这中大为上个国我以要他时来用们生到作地于出就分对成会可主发年动同工也能下过子说产种面而方后多定行学法所民得经十三之进着等部度家电力里如水化高自二理起小物现实加量都两体制机当使点从业本去把性好应开它合还因由其些然前外天政四日那社义事平形相全表间样与关各重新线内数正心反你明看原又么利比或但质气第向道命此变条只没结解问意建月公无系军很情者最立代想已通并提直题党程展五果料象员革位入常文总次品式活设及管特件长求老头基资边流路级少图山统接知较将组见计别她手角期根论运农指几九区强放决西被干做必战先回则任取据处队南给色光门即保治北造百规热领七海口东导器压志世金增争济阶油思术极交受联什认六共权收证改清己美再采转更单风切打白教速花带安场身车例真务具万每目至达走积示议声报斗完类八离华名确才科张信马节话米整空元况今集温传土许步群广石记需段研界拉林律叫且究观越织装影算低持音众书布复容儿须际商非验连断深难近矿千周委素技备半办青省列习响约支般史感劳便团往酸历市克何除消构府称太准精值号率族维划选标写存候毛亲快效斯院查江型眼王按格养易置派层片始却专状育厂京识适属圆包火住调满县局照参红细引听该铁价严龙确环球化经济全球化信息化网络化数字化智能化自动化现代化城市化工业化农业化服务化金融化贸易化投资化消费化生产化制造化创新化科技化教育化文化化艺术化体育化娱乐化旅游化医疗化健康化环保化节能化减排化低碳化绿色化可持续化发展化建设化改革化开放化合作化交流化沟通化协调化统筹化规划化管理化监督化评估化考核化奖惩化激励化约束化规范化标准化制度化法治化民主化科学化专业化精细化优质化高效化便民化人性化个性化多样化差异化特色化品牌化国际化";
            
            // 分多段添加，每段使用不同字符
            for (int i = 0; i < allChineseChars.length(); i += 100) {
                int end = Math.min(i + 100, allChineseChars.length());
                String segment = allChineseChars.substring(i, end);
                document.add(new Paragraph("字符段" + (i/100 + 1) + "：" + segment, font));
            }
            
            document.close();
            
            File file = new File("test_font_subset.pdf");
            System.out.println("   字体子集PDF大小: " + file.length() + " bytes (" + (file.length() / 1024) + " KB)");
            
        } catch (Exception e) {
            System.out.println("   字体子集测试失败: " + e.getMessage());
        }
    }
    
    private static void testEncodingIssue(String fontPath) {
        try {
            // 测试强制嵌入是否会导致大文件
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("test_forced_embed.pdf"));
            document.open();
            
            Font font = new Font(baseFont, 12);
            document.add(new Paragraph("强制嵌入字体测试：你好世界！", font));
            document.add(new Paragraph("Forced embed test: Hello World!", font));
            
            document.close();
            
            File file = new File("test_forced_embed.pdf");
            System.out.println("   强制嵌入PDF大小: " + file.length() + " bytes (" + (file.length() / 1024 / 1024) + " MB)");
            
            if (file.length() > 10 * 1024 * 1024) { // 大于10MB
                System.out.println("   ❌ 发现问题：强制嵌入导致文件过大！");
                System.out.println("   解决方案：确保使用 BaseFont.NOT_EMBEDDED");
            } else {
                System.out.println("   ✅ 强制嵌入文件大小正常");
            }
            
        } catch (Exception e) {
            System.out.println("   强制嵌入测试失败: " + e.getMessage());
        }
    }
}
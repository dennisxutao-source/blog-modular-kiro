package com.blog.web.util;

import com.itextpdf.text.pdf.BaseFont;
import java.io.File;

public class FontPathDiagnostic {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== 字体路径诊断工具 ===");
            
            diagnoseFontPaths();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void diagnoseFontPaths() {
        System.out.println("\n--- 诊断字体路径问题 ---");
        
        // 测试不同的路径格式
        String[] possiblePaths = {
            "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
            "web/src/main/resources/fonts/NotoSerifCJKsc-Regular.otf", 
            "./src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
            "./web/src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
            "fonts/NotoSerifCJKsc-Regular.otf",
            "/fonts/NotoSerifCJKsc-Regular.otf",
            "classpath:fonts/NotoSerifCJKsc-Regular.otf"
        };
        
        System.out.println("当前工作目录: " + System.getProperty("user.dir"));
        
        for (String path : possiblePaths) {
            testFontPath(path);
        }
        
        // 测试类路径资源
        testClasspathResource();
        
        // 给出建议
        provideSuggestions();
    }
    
    private static void testFontPath(String path) {
        System.out.println("\n测试路径: " + path);
        
        try {
            // 检查文件是否存在
            File fontFile = new File(path);
            if (fontFile.exists()) {
                System.out.println("  ✅ 文件存在");
                System.out.println("  文件大小: " + (fontFile.length() / 1024 / 1024) + " MB");
                
                // 尝试创建字体
                try {
                    BaseFont font = BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                    System.out.println("  ✅ 字体创建成功");
                    System.out.println("  PostScript名称: " + font.getPostscriptFontName());
                    
                    if (font.getPostscriptFontName().contains("SimSun")) {
                        System.out.println("  ❌ 警告：字体被识别为SimSun，这可能是问题所在！");
                    }
                    
                } catch (Exception e) {
                    System.out.println("  ❌ 字体创建失败: " + e.getMessage());
                }
                
            } else {
                System.out.println("  ❌ 文件不存在");
                
                // 尝试相对于当前目录的路径
                String absolutePath = new File(path).getAbsolutePath();
                System.out.println("  绝对路径: " + absolutePath);
            }
            
        } catch (Exception e) {
            System.out.println("  ❌ 路径测试失败: " + e.getMessage());
        }
    }
    
    private static void testClasspathResource() {
        System.out.println("\n--- 测试类路径资源 ---");
        
        try {
            // 使用类路径资源
            String resourcePath = "/fonts/NotoSerifCJKsc-Regular.otf";
            
            java.io.InputStream is = FontPathDiagnostic.class.getResourceAsStream(resourcePath);
            if (is != null) {
                System.out.println("✅ 类路径资源存在: " + resourcePath);
                
                // 读取字体数据
                byte[] fontData = readAllBytes(is);
                System.out.println("字体数据大小: " + (fontData.length / 1024 / 1024) + " MB");
                
                // 尝试从字节数组创建字体
                BaseFont font = BaseFont.createFont("NotoSerifCJKsc-Regular.otf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, false, fontData, null);
                System.out.println("✅ 从字节数组创建字体成功");
                System.out.println("PostScript名称: " + font.getPostscriptFontName());
                
                is.close();
                
            } else {
                System.out.println("❌ 类路径资源不存在: " + resourcePath);
            }
            
        } catch (Exception e) {
            System.out.println("❌ 类路径资源测试失败: " + e.getMessage());
        }
    }
    
    private static byte[] readAllBytes(java.io.InputStream is) throws java.io.IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
    
    private static void provideSuggestions() {
        System.out.println("\n--- 建议和解决方案 ---");
        
        System.out.println("如果你的PDF显示SimSun而不是NotoSerifCJKsc-Regular，可能的原因：");
        System.out.println();
        
        System.out.println("1. ❌ 字体文件路径错误");
        System.out.println("   - 检查你实际运行代码时的工作目录");
        System.out.println("   - 使用绝对路径或类路径资源");
        
        System.out.println("2. ❌ 运行环境问题");
        System.out.println("   - 在IDE中运行 vs 打包后运行");
        System.out.println("   - 字体文件没有被正确打包");
        
        System.out.println("3. ❌ 字体回退机制");
        System.out.println("   - 字体加载失败时自动回退到SimSun");
        System.out.println("   - 但SimSun可能实际上是一个大字体文件");
        
        System.out.println("4. ❌ 代码分支问题");
        System.out.println("   - 实际运行的代码可能不是你以为的代码");
        System.out.println("   - 检查是否有其他字体创建逻辑");
        
        System.out.println("\n🔧 立即解决方案：");
        System.out.println("1. 在你的代码中添加调试输出：");
        System.out.println("   System.out.println(\"字体PostScript名称: \" + font.getPostscriptFontName());");
        System.out.println("2. 检查字体文件是否真的存在：");
        System.out.println("   File f = new File(\"你的字体路径\"); System.out.println(f.exists());");
        System.out.println("3. 使用类路径资源而不是文件路径");
    }
}
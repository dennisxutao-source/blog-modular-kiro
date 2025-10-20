package com.blog.web.util;

import java.io.File;

public class FontPathDebugger {
    
    public static void main(String[] args) {
        debugFontPath();
    }
    
    public static void debugFontPath() {
        System.out.println("=== 字体路径调试工具 ===");
        
        // 显示当前工作目录
        String currentDir = System.getProperty("user.dir");
        System.out.println("当前工作目录: " + currentDir);
        
        // 测试不同的路径格式
        String[] testPaths = {
            "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
            "./src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
            "web/src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
            "./web/src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
            "../web/src/main/resources/fonts/NotoSerifCJKsc-Regular.otf",
            "fonts/NotoSerifCJKsc-Regular.otf",
            "./fonts/NotoSerifCJKsc-Regular.otf"
        };
        
        System.out.println("\n--- 测试不同路径格式 ---");
        for (String path : testPaths) {
            testPath(path);
        }
        
        // 尝试找到正确的路径
        System.out.println("\n--- 寻找正确路径 ---");
        findCorrectPath();
        
        // 提供解决方案
        System.out.println("\n--- 解决方案 ---");
        provideSolutions();
    }
    
    private static void testPath(String path) {
        File file = new File(path);
        boolean exists = file.exists();
        String absolutePath = file.getAbsolutePath();
        
        System.out.println("路径: " + path);
        System.out.println("  存在: " + exists);
        System.out.println("  绝对路径: " + absolutePath);
        
        if (exists) {
            System.out.println("  ✅ 找到了！文件大小: " + (file.length() / 1024 / 1024) + " MB");
        }
        System.out.println();
    }
    
    private static void findCorrectPath() {
        // 从当前目录开始搜索
        File currentDir = new File(".");
        searchForFontFile(currentDir, 0);
    }
    
    private static void searchForFontFile(File dir, int depth) {
        if (depth > 3) return; // 限制搜索深度
        
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().equals("fonts")) {
                    System.out.println("发现fonts目录: " + file.getAbsolutePath());
                    
                    // 检查fonts目录中的文件
                    File[] fontFiles = file.listFiles();
                    if (fontFiles != null) {
                        for (File fontFile : fontFiles) {
                            if (fontFile.getName().contains("NotoSerif")) {
                                System.out.println("  ✅ 找到字体文件: " + fontFile.getAbsolutePath());
                                System.out.println("  相对路径可能是: " + getRelativePath(fontFile));
                            }
                        }
                    }
                } else {
                    searchForFontFile(file, depth + 1);
                }
            }
        }
    }
    
    private static String getRelativePath(File fontFile) {
        String currentDir = System.getProperty("user.dir");
        String fontPath = fontFile.getAbsolutePath();
        
        if (fontPath.startsWith(currentDir)) {
            String relativePath = fontPath.substring(currentDir.length());
            if (relativePath.startsWith(File.separator)) {
                relativePath = relativePath.substring(1);
            }
            return relativePath.replace(File.separator, "/");
        }
        
        return fontPath;
    }
    
    private static void provideSolutions() {
        System.out.println("💡 解决方案：");
        System.out.println();
        
        System.out.println("方案1：使用绝对路径");
        System.out.println("String fontPath = new File(\"src/main/resources/fonts/NotoSerifCJKsc-Regular.otf\").getAbsolutePath();");
        System.out.println();
        
        System.out.println("方案2：使用类路径资源");
        System.out.println("InputStream fontStream = getClass().getResourceAsStream(\"/fonts/NotoSerifCJKsc-Regular.otf\");");
        System.out.println();
        
        System.out.println("方案3：动态检测工作目录");
        System.out.println("String basePath = System.getProperty(\"user.dir\");");
        System.out.println("if (basePath.endsWith(\"web\")) {");
        System.out.println("    fontPath = \"src/main/resources/fonts/NotoSerifCJKsc-Regular.otf\";");
        System.out.println("} else {");
        System.out.println("    fontPath = \"web/src/main/resources/fonts/NotoSerifCJKsc-Regular.otf\";");
        System.out.println("}");
        System.out.println();
        
        System.out.println("方案4：使用Spring资源加载器（推荐）");
        System.out.println("ClassPathResource resource = new ClassPathResource(\"fonts/NotoSerifCJKsc-Regular.otf\");");
        System.out.println("if (resource.exists()) {");
        System.out.println("    InputStream is = resource.getInputStream();");
        System.out.println("    // 使用输入流创建字体");
        System.out.println("}");
    }
}
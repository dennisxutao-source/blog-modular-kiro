package com.blog.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * PDF配置类
 * 管理PDF生成相关的配置参数
 */
@Configuration
@ConfigurationProperties(prefix = "pdf")
public class PdfConfig {
    
    /**
     * 字体文件路径
     */
    private String fontPath = "src/main/resources/fonts/NotoSerifCJKsc-Regular.otf";
    
    /**
     * 模板文件目录
     */
    private String templateDir = "src/main/resources/templates/pdf/";
    
    /**
     * 输出文件目录
     */
    private String outputDir = "output/pdf/";
    
    /**
     * 默认字体大小
     */
    private int defaultFontSize = 12;
    
    /**
     * 是否启用字体嵌入
     */
    private boolean embedFont = true;
    
    // Getters and Setters
    public String getFontPath() {
        return fontPath;
    }
    
    public void setFontPath(String fontPath) {
        this.fontPath = fontPath;
    }
    
    public String getTemplateDir() {
        return templateDir;
    }
    
    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }
    
    public String getOutputDir() {
        return outputDir;
    }
    
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }
    
    public int getDefaultFontSize() {
        return defaultFontSize;
    }
    
    public void setDefaultFontSize(int defaultFontSize) {
        this.defaultFontSize = defaultFontSize;
    }
    
    public boolean isEmbedFont() {
        return embedFont;
    }
    
    public void setEmbedFont(boolean embedFont) {
        this.embedFont = embedFont;
    }
}
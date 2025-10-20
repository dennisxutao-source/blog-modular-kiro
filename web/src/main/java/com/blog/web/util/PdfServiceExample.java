package com.blog.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * PDF服务使用示例
 * 展示如何在项目中使用优化后的EmbeddedFontPdfFiller
 */
@Service
public class PdfServiceExample {
    
    private static final Logger logger = LoggerFactory.getLogger(PdfServiceExample.class);
    
    @Autowired
    private EmbeddedFontPdfFiller embeddedFontPdfFiller;
    
    /**
     * 生成PDF文档示例
     */
    public void generatePdfDocument(String templatePath, String outputPath) {
        try {
            logger.info("开始生成PDF文档");
            
            // 准备数据
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("name", "张三");
            dataMap.put("company", "某某公司");
            dataMap.put("date", "2025-01-18");
            dataMap.put("amount", "10000.00");
            
            // 使用嵌入字体PDF填充工具
            embeddedFontPdfFiller.createEmbeddedFontPDF(
                dataMap, 
                templatePath, 
                outputPath, 
                null // 无签名图片
            );
            
            logger.info("PDF文档生成成功: {}", outputPath);
            
        } catch (Exception e) {
            logger.error("PDF文档生成失败", e);
            throw new RuntimeException("PDF生成失败", e);
        }
    }
    
    /**
     * 带签名的PDF生成示例
     */
    public void generatePdfWithSignature(String templatePath, String outputPath, String signaturePath) {
        try {
            logger.info("开始生成带签名的PDF文档");
            
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("contractNumber", "HT2025001");
            dataMap.put("partyA", "甲方公司");
            dataMap.put("partyB", "乙方公司");
            dataMap.put("signDate", "2025年1月18日");
            
            embeddedFontPdfFiller.createEmbeddedFontPDF(
                dataMap, 
                templatePath, 
                outputPath, 
                signaturePath
            );
            
            logger.info("带签名PDF文档生成成功: {}", outputPath);
            
        } catch (Exception e) {
            logger.error("带签名PDF文档生成失败", e);
            throw new RuntimeException("PDF生成失败", e);
        }
    }
}
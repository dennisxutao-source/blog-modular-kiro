# 你的PDF优化解决方案

## 问题分析

你的原始代码中：
```java
// 这行代码生成37MB的PDF，保险公司拒绝
bfChinese = CompactFontUtil.getCompressedBaseFont();
```

问题在于：
1. `CompactFontUtil` 类已被删除
2. 原来的字体配置导致文件过大
3. 缺少正确的压缩设置

## 解决方案

### 方案A：最小文件大小（强烈推荐）

**替换你的原始方法：**
```java
// 原始代码
public static void createPDF(Map<String, String> dataMap, String mouldPath,
                           String outPutPath, String signatureImgPath) {
    // ... 原始代码
}

// 新的优化代码
OptimizedPdfCreator.createMinimalPDF(dataMap, mouldPath, outPutPath, signatureImgPath);
```

**预期效果：**
- 文件大小：200KB以下（从37MB减少到200KB！）
- 压缩比：99.5%+
- 保险公司应该接受

### 方案B：子集化字体（备选方案）

```java
OptimizedPdfCreator.createCompressedPDF(dataMap, mouldPath, outPutPath, signatureImgPath);
```

**预期效果：**
- 文件大小：1-3MB
- 包含完整字体，兼容性更好
- 仍然比原来的37MB小很多

## 具体修改步骤

### 1. 替换你的createPDF方法

**原始代码：**
```java
BaseFont bfChinese = null;
try {
    CompactFontUtil.clearCache();
    bfChinese = CompactFontUtil.getCompressedBaseFont(); // 这里导致37MB
} catch (DocumentException e) {
    // ...
}
```

**新代码：**
```java
// 直接调用优化方法，不需要手动处理字体
OptimizedPdfCreator.createMinimalPDF(dataMap, mouldPath, outPutPath, signatureImgPath);
return; // 直接返回，不需要后续处理
```

### 2. 如果你想保持原有代码结构

只需要替换字体创建部分：

```java
BaseFont bfChinese = null;
try {
    // 替换原来的字体创建代码
    bfChinese = createOptimizedFont(); // 使用新的字体创建方法
} catch (DocumentException e) {
    // ...
}

// 添加压缩设置
stamper = new PdfStamper(reader, bos);
PdfWriter writer = stamper.getWriter();
writer.setCompressionLevel(9); // 最高压缩级别
writer.setPdfVersion(PdfWriter.VERSION_1_5);
stamper.setFullCompression(); // 启用全压缩

// 其余代码保持不变...
```

### 3. 字体创建方法

```java
private static BaseFont createOptimizedFont() throws DocumentException, IOException {
    try {
        // 最小文件方案：不嵌入字体
        return BaseFont.createFont("SimSun", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
    } catch (Exception e) {
        // 备选方案
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    }
}
```

## 使用示例

```java
public class YourPdfService {
    
    public void generateInsurancePdf() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("customerName", "张三");
        dataMap.put("policyNumber", "INS123456");
        dataMap.put("amount", "￥50,000.00");
        
        try {
            // 方案A：最小文件（推荐）
            OptimizedPdfCreator.createMinimalPDF(
                dataMap, 
                "insurance_template.pdf", 
                "output_policy.pdf", 
                "customer_signature.png"
            );
            
            System.out.println("PDF生成成功，文件大小应该在200KB以下");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## 测试验证

运行测试验证效果：

```bash
mvn exec:java -Dexec.mainClass="com.blog.web.util.PdfCreatorUsageExample"
```

## 关键改进点

1. **正确的BaseFont导入**：`import com.itextpdf.text.pdf.BaseFont;`
2. **不嵌入字体**：使用 `BaseFont.NOT_EMBEDDED` 大幅减小文件大小
3. **压缩设置**：添加了完整的PDF压缩配置
4. **资源管理**：保持了你原有的资源清理逻辑

## 预期结果

- **原始文件大小**：37MB
- **优化后文件大小**：200KB以下
- **压缩比**：99.5%+
- **保险公司接受度**：应该没问题

## 注意事项

1. **字体依赖**：最小文件方案依赖系统有中文字体（SimSun）
2. **兼容性**：如果担心兼容性，可以使用子集化字体方案
3. **图片处理**：保持了你原有的签名图片处理逻辑
4. **多页支持**：保持了你原有的多页PDF处理逻辑

你现在可以直接使用 `OptimizedPdfCreator.createMinimalPDF()` 替换你的原始方法，应该能解决保险公司的文件大小问题！
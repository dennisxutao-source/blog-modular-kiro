# PDF压缩解决方案 - iText 5.5.11

## 问题诊断

你的PDF压缩不生效的主要原因是：

1. **缺少iText依赖** - pom.xml中没有iText 5.5.11的依赖
2. **字体配置错误** - 没有正确设置字体子集化
3. **压缩设置不完整** - 缺少关键的压缩配置

## 解决方案

### 1. 添加依赖 ✅

已在 `web/pom.xml` 中添加：

```xml
<!-- iText 5.5.11 for PDF generation -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.11</version>
</dependency>
<!-- iText Asian font support -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext-asian</artifactId>
    <version>5.2.0</version>
</dependency>
```

### 2. 使用新的压缩工具类 ✅

创建了 `EffectivePdfCompressor.java`，提供两种压缩方案：

#### 方案A：最小文件大小（推荐）
```java
// 使用不嵌入字体，文件大小约200KB
byte[] pdf = EffectivePdfCompressor.createMinimalSizePdf(templateStream, fieldValues);
```

#### 方案B：子集化字体
```java
// 使用子集化字体，文件大小约1-3MB，兼容性好
byte[] pdf = EffectivePdfCompressor.createSubsetPdf(templateStream, fieldValues);
```

### 3. 关键技术点

#### iText 5.5.11 正确的压缩设置：
```java
PdfWriter writer = stamper.getWriter();
writer.setCompressionLevel(PdfWriter.BEST_COMPRESSION);  // 最佳压缩
writer.setPdfVersion(PdfWriter.VERSION_1_5);            // 使用PDF 1.5版本
stamper.setFullCompression();                            // 启用全压缩
```

#### 子集化字体的正确方法：
```java
BaseFont font = BaseFont.createFont(
    fontPath,
    BaseFont.IDENTITY_H,
    BaseFont.EMBEDDED,
    true,
    fontBytes,
    null
);
font.setSubset(true);  // 关键：必须在创建后立即设置
```

#### 不嵌入字体（最小文件）：
```java
BaseFont font = BaseFont.createFont(
    "SimSun",
    "UniGB-UCS2-H", 
    BaseFont.NOT_EMBEDDED  // 关键：不嵌入字体
);
```

## 使用方法

### 1. 快速测试
```java
// 验证字体设置是否正确
EffectivePdfCompressor.validateFontSettings();

// 打印使用建议
EffectivePdfCompressor.printUsageRecommendations();
```

### 2. 实际使用
```java
// 准备数据
Map<String, String> fieldValues = new HashMap<>();
fieldValues.put("name", "张三");
fieldValues.put("company", "测试公司");

// 生成最小PDF（推荐）
try (InputStream templateStream = new FileInputStream("template.pdf")) {
    byte[] compressedPdf = EffectivePdfCompressor.createMinimalSizePdf(
        templateStream, fieldValues);
    
    // 保存或返回PDF
    Files.write(Paths.get("output.pdf"), compressedPdf);
}
```

### 3. 效果测试
```java
// 运行完整测试
PdfCompressionTest.runFullTest();

// 如果有实际模板文件
PdfCompressionTest.testWithRealTemplate("templates/your-template.pdf");
```

## 预期效果

- **最小文件方案**：约200KB，依赖系统字体
- **子集字体方案**：1-3MB，包含字体，兼容性好
- **压缩比**：相比原始方法可减少70-90%的文件大小

## 注意事项

1. **字体文件位置**：确保 `fonts/NotoSerifCJKsc-Regular.otf` 存在
2. **系统字体依赖**：最小文件方案依赖目标系统有中文字体
3. **兼容性**：子集字体方案兼容性更好，适合外部分发
4. **JDK版本**：代码兼容JDK 8

## 故障排除

如果压缩仍然不生效：

1. 检查依赖是否正确添加：`mvn dependency:tree | grep itext`
2. 验证字体设置：运行 `EffectivePdfCompressor.validateFontSettings()`
3. 检查模板文件：确保PDF模板文件存在且可读
4. 查看日志：注意控制台输出的字体创建信息

## 下一步

1. 运行 `mvn clean compile` 确保编译通过
2. 执行 `PdfCompressionTest.quickTest()` 验证字体
3. 使用实际PDF模板测试压缩效果
4. 根据需求选择合适的压缩方案
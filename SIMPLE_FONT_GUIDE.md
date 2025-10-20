# 简洁字体工具使用指南

## 快速开始

### 推荐使用方式
```java
// 获取优化的BaseFont对象 (文件大小1-3MB)
BaseFont font = CompactFontUtil.getOptimizedBaseFont();

// 在PDF表单中使用
form.setFieldProperty("fieldName", "textfont", font, null);
```

## 三种字体模式

### 1. 优化模式 (推荐)
```java
BaseFont font = CompactFontUtil.getOptimizedBaseFont();
```
- 文件大小: 1-3MB
- 兼容性: 良好
- 适用: 大多数场景

### 2. 小文件模式
```java
BaseFont font = CompactFontUtil.getSmallBaseFont();
```
- 文件大小: 200KB
- 兼容性: 依赖系统字体
- 适用: 内部系统

### 3. 压缩模式
```java
BaseFont font = CompactFontUtil.getCompressedBaseFont();
```
- 文件大小: 800KB-2MB
- 兼容性: 良好
- 适用: 需要最小文件

## 智能选择

```java
// 根据场景自动选择
BaseFont font = CompactFontUtil.getSmartBaseFont("internal");  // 小文件模式
BaseFont font = CompactFontUtil.getSmartBaseFont("compress");  // 压缩模式
BaseFont font = CompactFontUtil.getSmartBaseFont("default");   // 优化模式
```

## 实际使用示例

```java
public byte[] generatePdf(Map<String, String> fieldValues) {
    try {
        // 获取优化字体
        BaseFont font = CompactFontUtil.getOptimizedBaseFont();
        
        // 创建PDF
        PdfReader reader = new PdfReader(templateStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);
        AcroFields form = stamper.getAcroFields();
        
        // 填充字段
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            form.setField(entry.getKey(), entry.getValue());
            form.setFieldProperty(entry.getKey(), "textfont", font, null);
            form.setFieldProperty(entry.getKey(), "textsize", 12f, null);
        }
        
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
        
        return baos.toByteArray();
        
    } catch (Exception e) {
        throw new RuntimeException("PDF生成失败", e);
    }
}
```

## 工具方法

```java
// 验证字体是否可用
boolean isValid = CompactFontUtil.validateFont();

// 获取文件大小信息
String sizeInfo = CompactFontUtil.getFileSizeInfo("optimized");

// 清除缓存
CompactFontUtil.clearCache();

// 显示使用说明
CompactFontUtil.printUsageInfo();
```

## 选择建议

- **一般使用**: `getOptimizedBaseFont()` 
- **内部系统**: `getSmallBaseFont()`
- **文件大小敏感**: `getCompressedBaseFont()`
- **不确定场景**: `getSmartBaseFont("default")`

## 注意事项

1. 字体文件需要放在 `web/src/main/resources/fonts/NotoSerifCJKsc-Regular.otf`
2. 如果字体文件不存在，会自动回退到系统字体
3. 字体对象会被缓存，重复使用不会重复加载
4. 压缩和子集化会在首次使用时应用

---
*简洁高效的字体解决方案*
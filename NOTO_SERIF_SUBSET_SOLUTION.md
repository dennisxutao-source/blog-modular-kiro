# 思源宋体子集化解决方案

## 问题理解

你需要：
1. ✅ 使用思源宋体字体（保持中文显示效果）
2. ✅ 解决38MB文件大小问题
3. ✅ 保险公司能接受的文件大小

## 解决方案

### 核心原理

思源宋体字体文件本身是23MB，如果完整嵌入到PDF中，就会导致文件过大。**子集化**是关键：只嵌入实际使用的字符，而不是整个字体文件。

### 使用方法

**替换你的原始createPDF方法：**

```java
// 原始方法
public static void createPDF(Map<String, String> dataMap, String mouldPath,
                           String outPutPath, String signatureImgPath) {
    // ... 原始代码
}

// 新的子集化方法
NotoSerifSubsetCreator.createPDFWithNotoSerifSubset(dataMap, mouldPath, outPutPath, signatureImgPath);
```

### 关键技术点

1. **正确的子集化设置**：
```java
BaseFont font = BaseFont.createFont(
    NOTO_SERIF_REGULAR,
    BaseFont.IDENTITY_H,
    BaseFont.EMBEDDED,    // 嵌入字体
    true,                 // 缓存字体
    fontBytes,
    null
);
font.setSubset(true);     // 关键：启用子集化
```

2. **避免中间缓存**：
```java
// 直接输出到最终文件，不使用ByteArrayOutputStream和PdfCopy
PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPutPath));
```

3. **最大压缩设置**：
```java
PdfWriter writer = stamper.getWriter();
writer.setCompressionLevel(9);
writer.setPdfVersion(PdfWriter.VERSION_1_5);
stamper.setFullCompression();
```

### 预期效果

- **原始文件大小**：38MB（完整嵌入思源宋体）
- **子集化后大小**：1-5MB（只嵌入使用的字符）
- **压缩比**：85-95%减少
- **字体效果**：保持思源宋体显示效果

### 测试验证

运行测试验证效果：

```java
// 快速测试
NotoSerifTestRunner.quickTest();

// 完整测试
Map<String, String> testData = new HashMap<>();
testData.put("name", "张三");
testData.put("company", "中国人寿保险");
NotoSerifTestRunner.testStandardSubset("template.pdf", "output.pdf", testData);
```

### 三种子集化方案

我提供了三种不同的子集化方案：

1. **标准子集化**（推荐）：
   - 使用：`createPDFWithNotoSerifSubset()`
   - 特点：平衡效果和文件大小

2. **智能子集化**：
   - 使用：`createPDFWithSmartSubset()`
   - 特点：分析实际使用字符，更精确

3. **最小子集化**：
   - 使用：`createPDFWithMinimalSubset()`
   - 特点：最激进的压缩设置

### 故障排除

如果子集化后文件仍然很大：

1. **检查子集化是否生效**：
   - 查看控制台输出："思源宋体子集化状态: 已启用"
   - 如果显示"未启用"，说明子集化失败

2. **尝试不同方案**：
   ```java
   // 如果标准方案不行，尝试最小子集化
   NotoSerifSubsetCreator.testSubsetMethods(dataMap, templatePath, "test_output");
   ```

3. **检查iText版本**：
   - 确保使用iText 5.5.11
   - 确保正确的BaseFont导入路径

### 使用示例

```java
public class YourPdfService {
    
    public void generateInsurancePdf() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("customerName", "张三");
        dataMap.put("policyNumber", "INS123456");
        dataMap.put("amount", "￥50,000.00");
        dataMap.put("company", "中国人寿保险股份有限公司");
        
        try {
            // 使用思源宋体子集化方法
            NotoSerifSubsetCreator.createPDFWithNotoSerifSubset(
                dataMap, 
                "insurance_template.pdf", 
                "output_policy.pdf", 
                "customer_signature.png"
            );
            
            System.out.println("PDF生成成功，使用思源宋体子集");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 关键优势

1. **保持字体效果**：继续使用思源宋体，中文显示效果不变
2. **大幅减小文件**：从38MB减少到1-5MB
3. **保险公司接受**：文件大小在合理范围内
4. **兼容性好**：包含必要的字体信息

### 注意事项

1. **字符覆盖**：子集化只包含实际使用的字符
2. **首次加载**：第一次创建字体可能稍慢
3. **内存使用**：比不嵌入字体方案占用更多内存
4. **文件兼容**：生成的PDF在任何设备上都能正确显示中文

现在你可以使用 `NotoSerifSubsetCreator.createPDFWithNotoSerifSubset()` 来替换原始方法，既保持了思源宋体效果，又解决了文件大小问题！
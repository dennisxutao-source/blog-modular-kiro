# PDF字体嵌入测试指南

## 快速测试你的PDF文件

### 方法1: 直接运行Java类

```bash
# 编译项目
mvn compile

# 运行特定PDF测试
mvn exec:java -Dexec.mainClass="com.blog.web.util.TestSpecificPdf"

# 或者运行快速检查
mvn exec:java -Dexec.mainClass="com.blog.web.util.QuickFontEmbeddingChecker" -Dexec.args="eg.10004730+测韦欣+测新字体 (8).pdf"

# 或者运行详细分析
mvn exec:java -Dexec.mainClass="com.blog.web.util.PdfFontEmbeddingAnalyzer" -Dexec.args="eg.10004730+测韦欣+测新字体 (8).pdf"
```

### 方法2: 通过Spring Boot应用

```bash
# 启动应用并运行字体检查
java -jar target/your-app.jar check-font-embedding

# 或者启动应用后通过API调用
curl http://localhost:8081/api/test/font-embedding
```

### 方法3: 在IDE中直接运行

1. 打开 `TestSpecificPdf.java`
2. 右键选择 "Run main()"
3. 查看控制台输出

## 测试结果解读

### ✅ 如果显示"包含嵌入字体"
- PDF文件已正确嵌入字体
- 在任何系统上都能正确显示
- 文件大小可能较大，但兼容性好

### ❌ 如果显示"未包含嵌入字体"
- PDF文件依赖系统字体
- 在没有相应字体的系统上可能显示异常
- 需要重新生成PDF并嵌入字体

## 常见问题排查

### 问题1: 文件不存在
```
❌ 文件不存在: eg.10004730+测韦欣+测新字体 (8).pdf
```

**解决方案:**
- 确保PDF文件在项目根目录
- 或者修改文件路径为正确的位置

### 问题2: 无法读取PDF
```
读取PDF文件失败: xxx
```

**解决方案:**
- 检查PDF文件是否损坏
- 确保有读取权限
- 尝试用其他PDF阅读器打开验证

### 问题3: 字体信息不完整
```
缺少字体描述符 - 可能是标准字体
```

**解决方案:**
- 这通常表示使用了PDF标准字体
- 标准字体通常不嵌入，依赖系统字体

## 生成嵌入字体的PDF

如果测试发现PDF未嵌入字体，可以使用以下方法重新生成：

### 使用实例方法（推荐）
```java
@Autowired
private EmbeddedFontPdfFiller pdfFiller;

Map<String, String> data = new HashMap<>();
data.put("name", "测韦欣");
data.put("id", "10004730");

pdfFiller.createEmbeddedFontPDF(data, "template.pdf", "output-embedded.pdf", null);
```

### 使用静态方法
```java
Map<String, String> data = new HashMap<>();
data.put("name", "测韦欣");
data.put("id", "10004730");

StaticEmbeddedFontPdfFiller.createEmbeddedFontPDF(data, "template.pdf", "output-embedded.pdf", null);
```

## 验证新生成的PDF

生成新PDF后，再次运行测试验证：

```bash
# 测试新生成的PDF
mvn exec:java -Dexec.mainClass="com.blog.web.util.QuickFontEmbeddingChecker" -Dexec.args="output-embedded.pdf"
```

应该看到类似输出：
```
✅ 发现嵌入字体: F1 (NotoSerifCJKsc-Regular)
✅ 该PDF包含嵌入字体
```

## 批量测试多个PDF

如果有多个PDF文件需要测试：

```java
String[] pdfFiles = {
    "eg.10004730+测韦欣+测新字体 (8).pdf",
    "output-embedded.pdf",
    "other-test.pdf"
};

QuickFontEmbeddingChecker.batchCheckFontEmbedding(pdfFiles);
```

## 性能考虑

- **嵌入字体的PDF**: 文件大小较大（通常增加2-10MB），但兼容性最好
- **非嵌入字体的PDF**: 文件小，但依赖系统字体
- **建议**: 对于需要跨系统分发的PDF，使用嵌入字体

## 总结

通过这些测试工具，你可以：
1. 快速检查PDF是否嵌入字体
2. 详细分析PDF中的字体信息
3. 验证PDF生成工具的效果
4. 确保PDF在不同系统上的兼容性

根据测试结果，选择合适的PDF生成策略，确保最终用户能够正确查看PDF内容。
# 如何测试你的PDF文件字体嵌入情况

## 快速测试方法

### 方法1: 使用Maven运行（推荐）

```bash
# 进入web目录
cd web

# 运行PDF字体检查工具（替换为你的PDF文件路径）
mvn exec:java -Dexec.mainClass="com.blog.web.util.SimplePdfFontChecker" -Dexec.args="你的PDF文件路径"

# 例如，如果PDF文件在桌面：
mvn exec:java -Dexec.mainClass="com.blog.web.util.SimplePdfFontChecker" -Dexec.args="/Users/你的用户名/Desktop/eg.10004730+测韦欣+测新字体 (8).pdf"

# 或者如果PDF文件在当前目录：
mvn exec:java -Dexec.mainClass="com.blog.web.util.SimplePdfFontChecker" -Dexec.args="./eg.10004730+测韦欣+测新字体 (8).pdf"
```

### 方法2: 直接运行Java类

```bash
# 编译项目
cd web
mvn compile

# 运行测试
java -cp target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q) com.blog.web.util.SimplePdfFontChecker "你的PDF文件路径"
```

### 方法3: 在IDE中运行

1. 打开 `SimplePdfFontChecker.java` 文件
2. 修改 main 方法中的测试文件路径：
   ```java
   public static void main(String[] args) {
       // 直接指定你的PDF文件路径
       String pdfPath = "/path/to/your/eg.10004730+测韦欣+测新字体 (8).pdf";
       checkPdfFonts(pdfPath);
   }
   ```
3. 右键运行 main 方法

## 预期输出解读

### 如果PDF嵌入了字体，你会看到：
```
=== PDF字体检查 ===
文件: eg.10004730+测韦欣+测新字体 (8).pdf
✅ 文件存在，大小: 1234 KB
PDF页数: 1
字体 F1: NotoSerifCJKsc-Regular (类型: Type0)
  ✅ 已嵌入

=== 检查结果 ===
总字体数: 1
是否包含嵌入字体: ✅ 是

✅ 该PDF包含嵌入字体，兼容性良好
```

### 如果PDF没有嵌入字体，你会看到：
```
=== PDF字体检查 ===
文件: eg.10004730+测韦欣+测新字体 (8).pdf
✅ 文件存在，大小: 123 KB
PDF页数: 1
字体 F1: SimSun (类型: Type1)
  ❌ 未嵌入

=== 检查结果 ===
总字体数: 1
是否包含嵌入字体: ❌ 否

⚠️  警告: 该PDF未嵌入字体
可能的问题:
- 在没有相应字体的系统上显示异常
- 中文可能显示为方框或乱码
- 依赖目标系统安装的字体

💡 建议:
- 使用 EmbeddedFontPdfFiller 重新生成PDF
- 确保设置 BaseFont.EMBEDDED 参数
```

## 常见问题

### Q: 文件不存在错误
```
❌ 文件不存在: xxx.pdf
```

**解决方案:**
1. 检查文件路径是否正确
2. 使用绝对路径，如：`/Users/用户名/Desktop/文件名.pdf`
3. 确保文件名中的特殊字符正确转义

### Q: 权限错误
**解决方案:**
1. 确保有读取PDF文件的权限
2. 尝试复制PDF文件到项目目录

### Q: 编码问题
如果文件名包含中文，可能需要：
1. 使用英文路径测试
2. 或者将PDF文件重命名为英文名

## 根据测试结果的下一步

### 如果测试显示"未嵌入字体"

你需要使用我们优化的工具重新生成PDF：

```java
// 使用Spring Bean方式（推荐）
@Autowired
private EmbeddedFontPdfFiller pdfFiller;

Map<String, String> data = new HashMap<>();
data.put("name", "测韦欣");
data.put("id", "10004730");

pdfFiller.createEmbeddedFontPDF(data, "template.pdf", "output-with-embedded-font.pdf", null);
```

或者使用静态方法：

```java
Map<String, String> data = new HashMap<>();
data.put("name", "测韦欣");
data.put("id", "10004730");

StaticEmbeddedFontPdfFiller.createEmbeddedFontPDF(data, "template.pdf", "output-with-embedded-font.pdf", null);
```

### 如果测试显示"已嵌入字体"

恭喜！你的PDF已经正确嵌入了字体，可以在任何系统上正确显示。

## 批量测试多个PDF

如果你有多个PDF文件需要测试：

```bash
mvn exec:java -Dexec.mainClass="com.blog.web.util.SimplePdfFontChecker" -Dexec.args="file1.pdf file2.pdf file3.pdf"
```

## 总结

通过这个测试工具，你可以：
1. ✅ 快速检查PDF是否嵌入字体
2. ✅ 了解PDF中使用的字体类型
3. ✅ 获得针对性的修复建议
4. ✅ 验证新生成PDF的字体嵌入效果

请运行测试并告诉我结果，这样我就能确定你的PDF文件是否真的嵌入了字体！
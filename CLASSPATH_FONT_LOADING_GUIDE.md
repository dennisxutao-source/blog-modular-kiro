# Classpath字体加载指南

## 概述

使用 `getClass().getClassLoader()` 方式从项目资源中加载字体文件，可以确保字体文件被打包到JAR中，部署时无需额外配置字体路径。

## 两种实现方式

### 1. 实例方法中使用（推荐）

在非静态方法中，可以直接使用 `getClass().getClassLoader()`：

```java
@Component
public class EmbeddedFontPdfFiller {
    
    private BaseFont createEmbeddedFont() throws DocumentException, IOException {
        // 使用相对路径从classpath加载字体文件
        String fontResourcePath = "fonts/NotoSerifCJKsc-Regular.otf";
        
        // 通过ClassLoader获取资源
        java.net.URL fontUrl = getClass().getClassLoader().getResource(fontResourcePath);
        
        if (fontUrl != null) {
            // 获取字体文件的InputStream
            java.io.InputStream fontStream = getClass().getClassLoader().getResourceAsStream(fontResourcePath);
            
            if (fontStream != null) {
                // 读取字体文件到字节数组
                byte[] fontBytes = readStreamToBytes(fontStream);
                
                // 使用字节数组创建字体
                BaseFont font = BaseFont.createFont(
                        fontResourcePath,
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED,
                        true, // cached
                        fontBytes, // 字体字节数组
                        null
                );
                
                return font;
            }
        }
        
        throw new RuntimeException("字体资源不存在: " + fontResourcePath);
    }
}
```

### 2. 静态方法中使用

在静态方法中，需要使用 `类名.class.getClassLoader()`：

```java
public class StaticEmbeddedFontPdfFiller {
    
    private static BaseFont createEmbeddedFontFromClasspath() throws DocumentException, IOException {
        String fontResourcePath = "fonts/NotoSerifCJKsc-Regular.otf";
        
        // 在静态方法中，需要使用类名.class来获取ClassLoader
        java.net.URL fontUrl = StaticEmbeddedFontPdfFiller.class.getClassLoader().getResource(fontResourcePath);
        
        if (fontUrl != null) {
            java.io.InputStream fontStream = StaticEmbeddedFontPdfFiller.class.getClassLoader().getResourceAsStream(fontResourcePath);
            
            if (fontStream != null) {
                byte[] fontBytes = readStreamToBytes(fontStream);
                
                BaseFont font = BaseFont.createFont(
                        fontResourcePath,
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED,
                        true,
                        fontBytes,
                        null
                );
                
                return font;
            }
        }
        
        throw new RuntimeException("字体资源不存在: " + fontResourcePath);
    }
}
```

## 关键要点

### 1. 资源路径
- 使用相对路径：`"fonts/NotoSerifCJKsc-Regular.otf"`
- 不要以 `/` 开头
- 路径相对于 `src/main/resources/` 目录

### 2. 字体文件位置
确保字体文件位于正确的位置：
```
src/main/resources/fonts/NotoSerifCJKsc-Regular.otf
```

### 3. 读取字节数组
由于iText需要字体的字节数组，需要将InputStream转换为byte[]：

```java
private static byte[] readStreamToBytes(java.io.InputStream inputStream) throws IOException {
    java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
    int nRead;
    byte[] data = new byte[16384];
    
    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
    }
    
    inputStream.close();
    return buffer.toByteArray();
}
```

### 4. 降级处理
建议添加降级处理，当classpath中找不到字体时，尝试使用文件路径：

```java
if (fontUrl != null) {
    // 从classpath加载
    return createFontFromClasspath();
} else {
    // 降级到文件路径
    logger.warn("classpath中未找到字体资源，尝试使用文件路径");
    return createFontFromFilePath();
}
```

## 优势

### 1. 部署便利性
- 字体文件打包到JAR中
- 无需额外配置字体路径
- 跨环境部署一致性

### 2. 路径独立性
- 不依赖绝对路径
- 不受工作目录影响
- 容器化部署友好

### 3. 资源管理
- 利用Java的资源管理机制
- 支持类路径扫描
- 更好的资源封装

## 使用示例

### 实例方法调用
```java
@Service
public class PdfService {
    @Autowired
    private EmbeddedFontPdfFiller pdfFiller;
    
    public void generatePdf() {
        Map<String, String> data = new HashMap<>();
        data.put("name", "张三");
        
        pdfFiller.createEmbeddedFontPDF(data, "template.pdf", "output.pdf", null);
    }
}
```

### 静态方法调用
```java
public class PdfUtil {
    public static void generatePdf() {
        Map<String, String> data = new HashMap<>();
        data.put("name", "张三");
        
        StaticEmbeddedFontPdfFiller.createEmbeddedFontPDF(data, "template.pdf", "output.pdf", null);
    }
}
```

## 测试验证

使用 `ClasspathFontTestRunner` 进行测试：

```bash
# 启动应用时测试
java -jar your-app.jar test-classpath-font

# 或在代码中手动测试
@Autowired
private ClasspathFontTestRunner testRunner;

public void test() {
    testRunner.testFontResourceExists();
    testRunner.manualTest();
}
```

## 注意事项

1. **字体文件大小**: 字体文件会增加JAR包大小
2. **内存使用**: 字体加载到内存中，注意内存使用
3. **缓存机制**: 建议启用字体缓存（`cached = true`）
4. **异常处理**: 添加适当的异常处理和降级机制
5. **日志记录**: 使用logger记录加载过程，便于调试

## 总结

使用 `getClass().getClassLoader()` 方式加载字体资源是现代Java应用的最佳实践，它提供了更好的部署便利性和资源管理能力。结合适当的降级机制，可以确保应用在各种环境下都能正常工作。
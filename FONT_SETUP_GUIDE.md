# 思源宋体字体设置指南

## 📋 概述

本项目使用思源宋体 (Noto Serif CJK SC) 作为PDF生成的中文字体。思源宋体是Google和Adobe联合开发的开源字体，支持简体中文、繁体中文、日文和韩文。

## 🚀 快速设置

### 1. 下载字体文件

访问 GitHub Releases 页面下载字体：
```
https://github.com/googlefonts/noto-cjk/releases
```

下载文件：`NotoSerifCJKsc-Regular.otf`

### 2. 放置字体文件

将下载的字体文件放入项目目录：
```
web/src/main/resources/fonts/NotoSerifCJKsc-Regular.otf
```

### 3. 验证安装

启动应用后，系统会自动验证字体文件。或者手动验证：
```java
boolean isValid = NotoSerifFontUtil.validateNotoSerifFont();
```

## 💻 使用方法

### 基本使用

```java
// 获取 BaseFont 对象
BaseFont baseFont = NotoSerifFontUtil.getNotoSerifBaseFont();

// 创建 Font 对象
Font normalFont = NotoSerifFontUtil.createDefaultNotoSerifFont();
Font boldFont = NotoSerifFontUtil.createNotoSerifBoldFont(16);
```

### PDF 表单填充

```java
// 准备表单数据
Map<String, String> fieldValues = new HashMap<>();
fieldValues.put("name", "张三");
fieldValues.put("company", "测试公司");

// 填充PDF表单
byte[] pdfBytes = PdfFormFontUtil.fillPdfFormWithNotoSerif(inputStream, fieldValues);
```

### 设置单个字段字体

```java
// 在PDF表单中设置单个字段的字体
PdfFormFontUtil.setFieldNotoSerifFont(form, "fieldName", "fieldValue", 12f);
```

## 🔧 工具类说明

### NotoSerifFontUtil

主要的字体工具类，提供以下功能：

- `getNotoSerifBaseFont()` - 获取BaseFont对象
- `createDefaultNotoSerifFont()` - 创建默认字体
- `createNotoSerifBoldFont(size)` - 创建粗体字体
- `validateNotoSerifFont()` - 验证字体文件
- `printSetupGuide()` - 打印设置指南

### PdfFormFontUtil

PDF表单字体工具类：

- `fillPdfFormWithNotoSerif()` - 使用思源宋体填充表单
- `setFieldNotoSerifFont()` - 设置字段字体

## 🐛 故障排除

### 字体文件不存在

**错误信息**：`思源宋体字体文件不存在`

**解决方案**：
1. 确认字体文件已下载
2. 确认文件路径正确：`web/src/main/resources/fonts/NotoSerifCJKsc-Regular.otf`
3. 重新构建项目

### 字体文件太小

**错误信息**：`字体文件太小，可能是错误的文件`

**解决方案**：
1. 重新下载字体文件（正确的文件应该约15-20MB）
2. 确认下载的是.otf文件，不是HTML页面

### 字体加载失败

**错误信息**：`字体验证失败`

**解决方案**：
1. 检查字体文件完整性
2. 确认文件格式正确（.otf）
3. 重启应用

## 📁 项目结构

```
blog-modular/
├── web/src/main/resources/fonts/
│   ├── NotoSerifCJKsc-Regular.otf    ← 思源宋体字体文件
│   ├── README.md                     ← 字体说明
│   └── download-fonts.sh             ← 下载脚本
├── web/src/main/java/com/blog/web/util/
│   ├── NotoSerifFontUtil.java        ← 字体工具类
│   ├── PdfFormFontUtil.java          ← PDF表单工具类
│   └── FontTestAndDemo.java          ← 字体测试类
└── FONT_SETUP_GUIDE.md               ← 本文档
```

## 📄 许可证

思源宋体使用 SIL Open Font License 1.1 许可证，可以自由使用、修改和分发，包括商业用途。

## 🆘 获取帮助

如果遇到问题，可以：

1. 运行字体验证：`NotoSerifFontUtil.validateNotoSerifFont()`
2. 查看设置指南：`NotoSerifFontUtil.printSetupGuide()`
3. 检查应用启动日志中的字体验证信息
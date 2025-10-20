#!/bin/bash

# 思源宋体下载脚本
echo "正在下载思源宋体字体..."

# 方法1：从 GitHub Releases 下载
echo "尝试从 GitHub 下载..."
curl -L -o NotoSerifCJKsc-Regular.otf "https://github.com/googlefonts/noto-cjk/releases/download/Serif2.002/NotoSerifCJKsc-Regular.otf"

# 检查文件是否正确下载
if file NotoSerifCJKsc-Regular.otf | grep -q "OpenType font"; then
    echo "✅ 思源宋体下载成功！"
else
    echo "❌ 下载失败，请手动下载"
    echo "请访问: https://github.com/googlefonts/noto-cjk/releases"
    echo "下载 NotoSerifCJKsc-Regular.otf 并放入此目录"
    rm -f NotoSerifCJKsc-Regular.otf
fi
@echo off
echo ========================================
echo    PaiAgent 前端服务启动
echo ========================================

cd /d %~dp0

echo.
echo 检查 Node.js 环境...
node -v
if %errorlevel% neq 0 (
    echo 错误: 未找到 Node.js，请先安装 Node.js 18+
    pause
    exit /b 1
)

echo.
echo 安装依赖...
call npm install

echo.
echo 启动前端服务...
echo 访问地址: http://localhost:3000
echo.

npm run dev

pause

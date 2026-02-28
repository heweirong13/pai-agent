@echo off
echo ========================================
echo    PaiAgent 后端服务启动
echo ========================================

cd /d %~dp0

echo.
echo 检查 Java 环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到 Java，请先安装 JDK 17+
    pause
    exit /b 1
)

echo.
echo 启动后端服务...
echo 访问地址: http://localhost:8080
echo API 文档: http://localhost:8080/swagger-ui.html
echo.

mvn spring-boot:run

pause

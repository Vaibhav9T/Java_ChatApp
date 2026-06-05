@echo off
echo =========================================
echo  WhatsApp-Style Java Chat Application
echo =========================================
echo.
echo Compiling application...
javac -cp "Backend/lib/mysql-connector-j-9.4.0.jar;Backend/src;Frontend/src" Frontend/src/*.java Backend/src/backend/models/*.java Backend/src/backend/dao/*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Compilation failed! Please check for errors.
    pause
    exit /b 1
)

echo.
echo ✅ Compilation successful!
echo.
echo Starting application...
echo.
echo 💡 Demo Accounts:
echo    admin / password123
echo    john_doe / password123  
echo    jane_smith / password123
echo.
java -cp "Backend/lib/mysql-connector-j-9.4.0.jar;Backend/src;Frontend/src" SimpleChatApp

pause
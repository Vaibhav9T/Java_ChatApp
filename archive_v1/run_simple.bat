@echo off
echo Starting Java Simple Chat Application...

REM Set up classpath
set MYSQL_JAR=Backend\lib\mysql-connector-j-9.4.0.jar
set OUTPUT_DIR=build

REM Check if compiled classes exist
if not exist %OUTPUT_DIR%\SimpleChatApp.class (
    echo Application not compiled! Please run compile.bat first.
    pause
    exit /b 1
)

REM Run the application
java -cp "%MYSQL_JAR%;%OUTPUT_DIR%" SimpleChatApp

pause
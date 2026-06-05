@echo off
echo Starting Java Simple Chat Application...
echo.

REM Set up paths
set MYSQL_JAR=Backend\lib\mysql-connector-j-9.4.0.jar
set BUILD_DIR=build
set MAIN_CLASS=SimpleChatApp

REM Check prerequisites
if not exist "%MYSQL_JAR%" (
    echo ERROR: MySQL connector JAR not found at %MYSQL_JAR%
    echo Please ensure the MySQL connector is in the correct location.
    pause
    exit /b 1
)

if not exist "%BUILD_DIR%\%MAIN_CLASS%.class" (
    echo ERROR: %MAIN_CLASS%.class not found in %BUILD_DIR%
    echo Please compile the application first using compile.bat
    pause
    exit /b 1
)

echo Checking database connection...
java -cp "%MYSQL_JAR%;%BUILD_DIR%" SimpleDBSetup > nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Database connection test failed
    echo The application will still try to run, but you may encounter issues
    echo.
)

echo Starting chat application...
echo Database: MySQL (localhost:3306/chat_app)
echo Available users: admin, john_doe, jane_smith
echo Default password: password123
echo.

REM Run the application
java -cp "%MYSQL_JAR%;%BUILD_DIR%" %MAIN_CLASS%

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with error code %ERRORLEVEL%
    echo.
    echo Troubleshooting:
    echo 1. Make sure MySQL server is running
    echo 2. Check database credentials in DatabaseManager.java
    echo 3. Ensure all files are compiled properly
    echo 4. Try running: compile.bat first
)

pause
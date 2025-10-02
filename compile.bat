@echo off
echo Compiling Java Chat Application...
echo.

REM Set up classpath
set MYSQL_JAR=Backend\lib\mysql-connector-j-9.4.0.jar
set BACKEND_SRC=Backend\src
set FRONTEND_SRC=Frontend\src
set OUTPUT_DIR=build

REM Check if MySQL JAR exists
if not exist "%MYSQL_JAR%" (
    echo ERROR: MySQL connector JAR not found at %MYSQL_JAR%
    echo Please ensure mysql-connector-j-9.4.0.jar is in Backend\lib\
    pause
    exit /b 1
)

REM Create output directory
if not exist %OUTPUT_DIR% mkdir %OUTPUT_DIR%

echo Compiling backend database classes...
javac -cp "%MYSQL_JAR%" -d %OUTPUT_DIR% "%BACKEND_SRC%\backend\database\DatabaseManager.java"
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to compile DatabaseManager.java
    pause
    exit /b 1
)

echo Compiling backend model classes...
javac -cp "%MYSQL_JAR%;%OUTPUT_DIR%" -d %OUTPUT_DIR% "%BACKEND_SRC%\backend\models\*.java"
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to compile model classes
    pause
    exit /b 1
)

echo Compiling backend DAO classes...
javac -cp "%MYSQL_JAR%;%OUTPUT_DIR%" -d %OUTPUT_DIR% "%BACKEND_SRC%\backend\dao\*.java"
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to compile DAO classes
    pause
    exit /b 1
)

echo Compiling frontend classes...
javac -cp "%MYSQL_JAR%;%OUTPUT_DIR%" -d %OUTPUT_DIR% "%FRONTEND_SRC%\SimpleChatApp.java"
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to compile SimpleChatApp.java
    pause
    exit /b 1
)

javac -cp "%MYSQL_JAR%;%OUTPUT_DIR%" -d %OUTPUT_DIR% "%FRONTEND_SRC%\RegisterDialog.java"
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Failed to compile RegisterDialog.java (this is optional)
)

echo Compiling utility classes...
javac -cp "%MYSQL_JAR%;%OUTPUT_DIR%" -d %OUTPUT_DIR% "SimpleDBSetup.java"
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Failed to compile SimpleDBSetup.java
)

echo.
echo ✅ Compilation successful!
echo.
echo Available applications:
echo   • SimpleChatApp - Main chat application
echo   • SimpleDBSetup - Database setup utility
echo.
echo To run: use run_chat.bat or run_simple.bat
echo Manual run: java -cp "%MYSQL_JAR%;%OUTPUT_DIR%" SimpleChatApp

pause
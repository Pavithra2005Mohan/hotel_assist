@echo off
echo ==========================================
echo   Building Hostel Assist Application
echo ==========================================
echo.

if not exist bin mkdir bin

echo Compiling Java files...
javac -d bin -encoding UTF-8 src/main/**/*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==========================================
    echo   Build Successful!
    echo ==========================================
    echo.
    echo To run the application:
    echo   java -cp bin main.Main
    echo.
) else (
    echo.
    echo ==========================================
    echo   Build Failed!
    echo ==========================================
    echo.
    pause
)

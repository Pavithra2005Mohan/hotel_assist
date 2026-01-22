@echo off
echo ==========================================
echo   Deploying to GitHub Pages
echo ==========================================
echo.

REM Create deploy directory
if not exist deploy mkdir deploy

REM Copy web files
echo Copying web files...
xcopy /E /I /Y /Q HostelAssist\src\web\* deploy\

REM Copy to root (if deploying from root)
echo Copying to root...
xcopy /E /I /Y /Q HostelAssist\src\web\* .

echo.
echo ==========================================
echo   Files ready for GitHub Pages!
echo ==========================================
echo.
echo Next steps:
echo 1. git add .
echo 2. git commit -m "Deploy to GitHub Pages"
echo 3. git push origin main
echo.
echo Then enable GitHub Pages in repository settings.
echo.
pause

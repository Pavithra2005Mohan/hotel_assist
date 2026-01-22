# Quick Start: Deploy to GitHub Pages

## 🚀 Fastest Way (3 Steps)

### Step 1: Prepare Files
Run the deployment script:
```bash
# Windows
deploy-gh-pages.bat

# Or manually copy:
# Copy contents of HostelAssist/src/web/ to repository root
```

### Step 2: Push to GitHub
```bash
git add .
git commit -m "Deploy to GitHub Pages"
git push origin main
```

### Step 3: Enable GitHub Pages
1. Go to your repository on GitHub
2. Click **Settings** → **Pages**
3. Under **Source**, select:
   - **Branch**: `main`
   - **Folder**: `/ (root)`
4. Click **Save**

## ✅ Done!

Your site will be live at:
```
https://YOUR_USERNAME.github.io/REPO_NAME/
```

## 📝 Important Notes

- **Simulation Mode**: The app will automatically run in simulation mode (backend won't work on GitHub Pages)
- **All Features Work**: All 5 modules will function in simulation mode
- **No Backend Needed**: The frontend works completely standalone

## 🔄 Update Your Site

Just push changes:
```bash
git add .
git commit -m "Update site"
git push origin main
```

GitHub Pages will automatically update in a few minutes.

## 🎯 What Works in Simulation Mode

✅ **Notices** - Post and view notices (stored in browser)  
✅ **Complaints** - Submit complaints (simulated)  
✅ **Room Info** - Search rooms (pre-loaded data)  
✅ **Mess Feedback** - Vote and see stats (local storage)  
✅ **P2P Sharing** - View and download files (simulated)  

Everything works exactly like localhost, just without the Java backend!

# Deployment Guide - Hostel Assist

## Option 1: GitHub Pages (Frontend Only - Simulation Mode)

GitHub Pages can only host static files (HTML/CSS/JS). The Java backend won't run, but the app will work in **Simulation Mode** which demonstrates all features.

### Steps:

1. **Create a GitHub Repository**
   ```bash
   # Initialize git (if not already done)
   git init
   git add .
   git commit -m "Initial commit"
   ```

2. **Create a `gh-pages` branch or use `main` branch**
   ```bash
   # Option A: Use main branch
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/hostel-assist.git
   git push -u origin main
   ```

3. **Prepare files for GitHub Pages**
   - Copy the `src/web` folder contents to the root of your repository
   - Or create a `docs` folder and put web files there

4. **Enable GitHub Pages**
   - Go to your repository on GitHub
   - Click **Settings** → **Pages**
   - Under **Source**, select:
     - **Branch: main** (or `gh-pages`)
     - **Folder: / (root)** or `/docs` if you used docs folder
   - Click **Save**

5. **Your site will be live at:**
   ```
   https://YOUR_USERNAME.github.io/hostel-assist/
   ```

### Quick Setup Script

Create a file `deploy-gh-pages.sh`:

```bash
#!/bin/bash
# Copy web files to root for GitHub Pages
cp -r HostelAssist/src/web/* .
git add .
git commit -m "Deploy to GitHub Pages"
git push origin main
```

Or for Windows, create `deploy-gh-pages.bat`:

```batch
@echo off
xcopy /E /I /Y HostelAssist\src\web\* .
git add .
git commit -m "Deploy to GitHub Pages"
git push origin main
```

---

## Option 2: Full Stack Deployment (Backend + Frontend)

For full functionality, you need to deploy the Java backend separately.

### Backend Hosting Options:

#### A. Railway (Recommended - Free tier available)
1. Go to [railway.app](https://railway.app)
2. Sign up with GitHub
3. Click **New Project** → **Deploy from GitHub repo**
4. Select your repository
5. Railway will auto-detect Java
6. Add build command: `javac -d bin -encoding UTF-8 -sourcepath src src/main/Main.java`
7. Add start command: `java -cp bin main.Main`
8. Railway will provide a URL like: `https://your-app.railway.app`
9. Update frontend to use this URL instead of `localhost:8080`

#### B. Render (Free tier available)
1. Go to [render.com](https://render.com)
2. Sign up and create a **Web Service**
3. Connect your GitHub repository
4. Set:
   - **Build Command**: `javac -d bin -encoding UTF-8 -sourcepath src src/main/Main.java`
   - **Start Command**: `java -cp bin main.Main`
5. Render will provide a URL

#### C. Heroku (Paid, but has free alternatives)
1. Create `Procfile`:
   ```
   web: java -cp bin main.Main
   ```
2. Deploy via Heroku CLI or GitHub integration

#### D. Self-Hosted VPS
- Deploy on any VPS (DigitalOcean, AWS EC2, etc.)
- Install Java
- Run the application
- Use Nginx as reverse proxy

---

## Option 3: Hybrid Approach (Recommended for Demo)

1. **Frontend on GitHub Pages** (Free, always online)
   - Deploy `src/web` folder to GitHub Pages
   - App works in Simulation Mode

2. **Backend on Railway/Render** (When needed)
   - Deploy Java backend separately
   - Update frontend to point to backend URL
   - Or keep simulation mode as fallback

### Update Frontend for Remote Backend

Edit `src/web/js/app.js` and replace `localhost:8080` with your backend URL:

```javascript
// Change this:
const BACKEND_URL = 'http://localhost:8080';

// To this:
const BACKEND_URL = 'https://your-backend.railway.app';

// Then update all fetch calls:
fetch(`${BACKEND_URL}/api/notices`)
```

Or use environment detection:

```javascript
// Auto-detect backend URL
const BACKEND_URL = window.location.hostname === 'localhost' 
    ? 'http://localhost:8080' 
    : 'https://your-backend.railway.app';
```

---

## File Structure for GitHub Pages

```
your-repo/
├── index.html          (from src/web/index.html)
├── css/
│   └── style.css      (from src/web/css/style.css)
├── js/
│   └── app.js         (from src/web/js/app.js)
└── README.md
```

---

## Testing Before Deployment

1. **Test locally in simulation mode:**
   - Don't start the Java server
   - Open `index.html` in browser
   - All features should work in simulation mode

2. **Test with backend:**
   - Start Java server: `java -cp bin main.Main`
   - Open `http://localhost:8080`
   - Verify all modules work

---

## Troubleshooting

### GitHub Pages not loading
- Check file paths are correct
- Ensure `index.html` is in root or specified folder
- Check browser console for errors

### CORS Errors
- Backend needs CORS headers (already included in code)
- Or use a CORS proxy for development

### Backend Connection Failed
- App automatically switches to Simulation Mode
- This is expected behavior when backend is offline

---

## Quick Deploy Commands

### For GitHub Pages:
```bash
# Navigate to project
cd hotel_assist

# Copy web files to a deploy folder
mkdir -p deploy
cp -r HostelAssist/src/web/* deploy/

# Or if deploying from root:
cp -r HostelAssist/src/web/* .

# Commit and push
git add .
git commit -m "Deploy to GitHub Pages"
git push origin main
```

### For Railway/Render:
```bash
# Just push your code
git push origin main

# Railway/Render will auto-deploy
```

---

## Notes

- **GitHub Pages**: Free, but backend won't run (Simulation Mode)
- **Railway/Render**: Free tier available, full functionality
- **Simulation Mode**: Demonstrates all features without backend
- **Hybrid**: Best of both worlds - always-available frontend + optional backend

Choose the option that best fits your needs!

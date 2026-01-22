# Quick Start Guide - Hostel Assist

## Prerequisites
- Java JDK 8 or higher installed
- Web browser (Chrome, Firefox, or Edge)

## Step 1: Compile the Project

### Option A: Using build script (Windows)
```bash
cd hotel_assist/HostelAssist
build.bat
```

### Option B: Manual compilation
```bash
cd hotel_assist/HostelAssist
mkdir bin
javac -d bin -encoding UTF-8 src/main/**/*.java
```

## Step 2: Run the Application

### Option A: Using run script (Windows)
```bash
run.bat
```

### Option B: Manual execution
```bash
java -cp bin main.Main
```

## Step 3: Access the Application

1. Open your web browser
2. Navigate to: **http://localhost:8080**
3. You should see the Hostel Assist dashboard

## Step 4: Test Each Module

### Module 1: Complaints (Socket Programming)
- Click "Complaints" in sidebar
- Fill in room number, category, and description
- Click "Submit Complaint"
- Verify acknowledgment message

### Module 2: Rooms (RMI)
- Click "Rooms" in sidebar
- Enter room number (try: 101, 102, 201, 202, 301, 302)
- Click "Search Registry"
- Verify room details and warden contact

### Module 3: Notices (REST API)
- Click "Notices" in sidebar
- View existing notices
- Enter title and message
- Click "Post Notice"
- Verify new notice appears

### Module 4: P2P Sharing
- Click "P2P Sharing" in sidebar
- View available files
- Click "Download" to download a file
- Click "Refresh File List" to update

### Module 5: Mess Feedback (Shared Memory)
- Click "Mess" in sidebar
- Click any feedback button (Good/Average/Poor)
- Verify counter updates
- Open multiple tabs and vote simultaneously to see synchronization

## Troubleshooting

### Port Already in Use
If you see "Address already in use":
- Close other applications using ports 5000, 8080, 1099, 6000, or 8081
- Or modify port numbers in the source code

### Cannot Connect to Server
- Ensure all servers started successfully
- Check console output for error messages
- Verify firewall is not blocking ports

### Files Not Loading
- Check browser console (F12) for errors
- Verify `src/web` directory exists with all files
- Ensure file paths are correct

## Expected Console Output

When running successfully, you should see:
```
==========================================
   HOSTEL ASSIST - DISTRIBUTED BACKEND
==========================================
[Module 3] Notice Controller running on Port 8080
[Module 1] Complaint Socket Server running on Port 5000
[Module 2] RMI Registry running on Port 1099
[Module 4] P2P Peer Node listening on Port 6000
[Module 5] Mess Feedback Controller running on Port 8081

[SUCCESS] All systems running.
>>> Local Dashboard: http://localhost:8080
```

## Stopping the Application

Press `Ctrl+C` in the terminal/command prompt to stop all servers.

## Next Steps

- Read the full README.md for detailed architecture information
- Explore the source code to understand each module
- Modify and extend functionality as needed

---

**Happy Coding! 🚀**

# hotel_assist
Git Development Strategy: Hostel Assist

1. Repository Architecture

To avoid "Merge Conflicts" (where two people edit the same file and Git gets confused), we will strictly follow this directory isolation rule. Only edit files inside your assigned folder.

HostelAssist/

├── src/

│   ├── main/

│   │   ├── Main.java              <-- MASTER FILE 

│   │   ├── modules/

│   │   │   ├── noticeboard/       <--  (REST)

│   │   │   ├── complaints/        <--  (Sockets)

│   │   │   ├── p2p/               <--  (P2P)

│   │   │   ├── roominfo/          <-- (RMI)

│   │   │   └── mess/              <-- (Shared Mem)

│   └── web/                       <-- (UI)



2. Branching Strategy

We will use a Feature Branch Workflow. Do not push directly to the main branch until your module is working.

main: The stable, working version of the project.

dev-member-1 (or feature/ui-rest): For UI, Main.java, and REST API.

dev-member-2 (or feature/networking): For Sockets and P2P modules.

dev-member-3 (or feature/systems): For RMI and Shared Memory modules.

3. Workflow for Team Members

Step 1: Setup

Clone the repository and switch to your branch.

git clone <repo-url>
git checkout -b dev-member-X  # Replace X with your number/name


Step 2: Daily Development

Work only in your specific folder (e.g., src/main/modules/complaints/).

git add .
git commit -m "Implemented Socket Server logic"
git push origin dev-member-X


Step 3: Integration (The Critical Part)

When a member finishes a module (e.g., Member 2 finishes Sockets), they should request a merge into main.

Member 1 (Lead) will handle the merge:

Switch to main: git checkout main

Pull the member's branch: git merge dev-member-2

Update Main.java: Uncomment the lines in Main.java that start the new module.

Test if the system starts.

Push the updated main: git push origin main

4. Rules to Avoid Conflicts

Don't touch Main.java unless you are Member 1.

If Member 2 needs to test: Create a temporary TestRunner.java inside your own module folder. Don't modify the global Main.java.

Don't touch index.html unless you are Member 1.

If Member 2/3 needs UI changes: Send the HTML snippet to Member 1 via chat, or add a comment in your Pull Request.

Pull before you Push.

Always run git pull origin main into your branch before working to make sure you have the latest updates from the team.

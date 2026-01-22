// --- SMART SIMULATION LOGIC ---
// If Backend is offline (like on Vercel), we switch to simulation mode
let IS_SIMULATION = false;

function show(id) {
    document.querySelectorAll('.card').forEach(c => c.classList.remove('active'));
    document.getElementById(id).classList.add('active');
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    const navItems = document.querySelectorAll('.nav-item');
    for (let item of navItems) {
        if (item.textContent.toLowerCase().includes(id) || 
           (id === 'p2p' && item.textContent.includes('P2P'))) {
            item.classList.add('active');
            break;
        }
    }
    
    // Load data when switching tabs
    if (id === 'p2p') {
        refreshP2PFiles();
    }
}

// --- NOTICES ---
async function loadNotices() {
    try {
        const res = await fetch('http://localhost:8080/api/notices');
        if (!res.ok) throw new Error("Offline");
        const data = await res.json();
        renderNotices(data);
        hideSimStatus();
    } catch (e) {
        enableSimulation();
        renderNotices(simulatedNotices);
    }
}

async function postNotice() {
    const title = document.getElementById('noticeTitle').value;
    const message = document.getElementById('noticeMessage').value;
    if (!title && !message) return;
    
    try {
        const body = JSON.stringify({title: title || "Notice", message: message || ""});
        await fetch('http://localhost:8080/api/notices', { 
            method: 'POST', 
            headers: {'Content-Type': 'application/json'},
            body: body 
        });
        loadNotices();
        document.getElementById('noticeTitle').value = "";
        document.getElementById('noticeMessage').value = "";
    } catch (e) {
        simulatedNotices.push({title: title, message: message, date: Date.now()});
        renderNotices(simulatedNotices);
        alert("Notice posted (Simulation Mode)");
    }
}

function renderNotices(list) {
    if (!Array.isArray(list)) return;
    const noticeListDiv = document.getElementById('noticeList');
    if (list.length === 0) {
        noticeListDiv.innerHTML = '<div style="padding:20px; text-align:center; color:#64748b;">No notices yet. Be the first to post one!</div>';
        return;
    }
    noticeListDiv.innerHTML = list.map(n => {
        if (typeof n === 'string') {
            return `<div><b>Notice:</b> ${n}</div>`;
        }
        const date = new Date(n.date || Date.now());
        return `<div>
            <b>${n.title || 'Notice'}</b>
            <div style="color:#64748b; font-size:0.9rem; margin-top:8px; line-height:1.6">${n.message || ''}</div>
            <div style="color:#94a3b8; font-size:0.75rem; margin-top:8px">🕒 ${date.toLocaleString()}</div>
        </div>`;
    }).join('');
}

// --- MESS ---
async function vote(type) {
    try {
        await fetch('http://localhost:8081/mess/feedback', { 
            method: 'POST', 
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'type=' + type 
        });
        loadStats();
    } catch (e) {
        simMessStats[type === 'good' ? 0 : type === 'average' ? 1 : 2]++;
        renderStats(simMessStats[0], simMessStats[1], simMessStats[2]);
    }
}

async function loadStats() {
    try {
        const res = await fetch('http://localhost:8081/mess/feedback');
        if (!res.ok) throw new Error("Offline");
        const data = await res.json();
        renderStats(data.good, data.average, data.poor);
        hideSimStatus();
    } catch (e) {
        enableSimulation();
        renderStats(simMessStats[0], simMessStats[1], simMessStats[2]);
    }
}

function renderStats(g, a, p) {
    document.getElementById('statGood').textContent = g;
    document.getElementById('statAverage').textContent = a;
    document.getElementById('statPoor').textContent = p;
}

// --- COMPLAINTS (Socket via Proxy) ---
let simulatedComplaints = [];

async function submitComplaint() {
    const room = document.getElementById('complaintRoom').value;
    const category = document.getElementById('complaintCategory').value;
    const desc = document.getElementById('complaintDesc').value;
    const statusDiv = document.getElementById('complaintStatus');
    
    if (!room || !category || !desc) {
        statusDiv.innerHTML = '<div style="color:#dc2626; background:#fef2f2; padding:15px; border-radius:10px; border:2px solid #ef4444; font-weight:500">Please fill all fields</div>';
        return;
    }
    
    statusDiv.innerHTML = '<div style="color:#3b82f6; padding:15px; background:#eff6ff; border-radius:10px; border:2px solid #3b82f6; font-weight:500">Connecting to Socket Server...</div>';
    
    try {
        const response = await fetch('http://localhost:8080/api/complaint', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({room: room, category: category, description: desc})
        });
        
        const data = await response.json();
        if (data.status === 'success') {
            statusDiv.innerHTML = `<div style="color:#059669; background:#ecfdf5; padding:15px; border-radius:10px; border:2px solid #10b981; font-weight:500">
                ✅ ${data.message}
            </div>`;
            // Clear form
            document.getElementById('complaintRoom').value = "";
            document.getElementById('complaintCategory').value = "";
            document.getElementById('complaintDesc').value = "";
        } else {
            statusDiv.innerHTML = `<div style="color:#dc2626; background:#fef2f2; padding:15px; border-radius:10px; border:2px solid #ef4444; font-weight:500">❌ ${data.message}</div>`;
        }
    } catch (e) {
        // Simulation mode
        enableSimulation();
        const ticketId = Math.floor(Math.random() * 10000);
        simulatedComplaints.push({room, category, desc, ticketId, date: Date.now()});
        statusDiv.innerHTML = `<div style="color:#059669; background:#ecfdf5; padding:15px; border-radius:10px; border:2px solid #10b981; font-weight:500">
            ✅ ACK: Complaint Registered. Ticket ID #${ticketId} | Room: ${room} | Category: ${category} (Simulation Mode)
        </div>`;
        // Clear form
        document.getElementById('complaintRoom').value = "";
        document.getElementById('complaintCategory').value = "";
        document.getElementById('complaintDesc').value = "";
    }
}

// --- ROOM INFO (RMI via Proxy) ---
const simulatedRooms = {
    "101": {occupants: "John Doe, Jane Smith", warden: "Mr. Smith", phone: "9876543210", email: "smith@hostel.edu"},
    "102": {occupants: "Alice Johnson, Bob Williams", warden: "Mr. Smith", phone: "9876543210", email: "smith@hostel.edu"},
    "201": {occupants: "Charlie Brown, David Lee", warden: "Ms. Johnson", phone: "9876543211", email: "johnson@hostel.edu"},
    "202": {occupants: "Emma Wilson, Frank Miller", warden: "Ms. Johnson", phone: "9876543211", email: "johnson@hostel.edu"},
    "301": {occupants: "Grace Taylor, Henry Davis", warden: "Mr. Anderson", phone: "9876543212", email: "anderson@hostel.edu"},
    "302": {occupants: "Iris White, Jack Black", warden: "Mr. Anderson", phone: "9876543212", email: "anderson@hostel.edu"}
};

async function searchRoom() {
    const room = document.getElementById('roomInput').value || "101";
    const resultDiv = document.getElementById('rmiResult');
    
    resultDiv.innerHTML = '<div style="color:#3b82f6; padding:15px; background:#eff6ff; border-radius:10px; border:2px solid #3b82f6; font-weight:500">Invoking RMI Remote Method...</div>';
    
    // Simulate network delay
    await new Promise(resolve => setTimeout(resolve, 800));
    
    try {
        const response = await fetch(`http://localhost:8080/api/room?room=${room}`);
        const data = await response.json();
        
        if (data.details) {
            resultDiv.innerHTML = `
                <h4>Room ${data.room} Details</h4>
                <div><b>Details:</b> ${data.details}</div>
                <div style="margin-top:10px"><b>Warden Contact:</b> ${data.contact}</div>
            `;
        } else {
            resultDiv.innerHTML = `<div style="color:#ef4444; padding:15px; background:#fef2f2; border-radius:10px;">❌ ${data.message || 'Room not found'}</div>`;
        }
    } catch (e) {
        // Simulation mode
        enableSimulation();
        const roomData = simulatedRooms[room];
        if (roomData) {
            resultDiv.innerHTML = `
                <h4>Room ${room} Details</h4>
                <div><b>Details:</b> Room ${room}: Occupied by ${roomData.occupants}. Warden: ${roomData.warden}.</div>
                <div style="margin-top:10px"><b>Warden Contact:</b> Warden: ${roomData.warden} | Phone: ${roomData.phone} | Email: ${roomData.email}</div>
                <div style="margin-top:10px; padding:8px; background:#f0fdf4; border-radius:6px; font-size:0.85rem; color:#166534">ℹ️ Running in Simulation Mode</div>
            `;
        } else {
            resultDiv.innerHTML = `<div style="color:#ef4444; padding:15px; background:#fef2f2; border-radius:10px;">❌ Room ${room} not found. No warden contact available.</div>`;
        }
    }
}

// --- P2P FILE SHARING ---
const simulatedFiles = ["Lab_Manual.pdf", "Notes.txt", "Assignment_Guide.pdf", "Study_Material.docx"];

async function refreshP2PFiles() {
    const fileListDiv = document.getElementById('p2pFileList');
    fileListDiv.innerHTML = 'Loading files...';
    
    try {
        const response = await fetch('http://localhost:8080/api/p2p?action=list');
        const data = await response.json();
        
        if (data.files && data.files.startsWith('FILES:')) {
            const files = data.files.substring(6).split(',').filter(f => f.trim());
            if (files.length === 0) {
                fileListDiv.innerHTML = '<div style="padding:20px; text-align:center; color:#64748b;">No files available</div>';
            } else {
                fileListDiv.innerHTML = files.map(f => 
                    `<div>📄 <b>${f.trim()}</b> 
                        <button class="btn btn-primary btn-inline" style="padding:8px 16px; font-size:0.85rem;" onclick="downloadP2PFile('${f.trim()}')">⬇️ Download</button>
                    </div>`
                ).join('');
            }
        } else {
            fileListDiv.innerHTML = '<div>No files available</div>';
        }
    } catch (e) {
        // Simulation mode
        enableSimulation();
        if (simulatedFiles.length === 0) {
            fileListDiv.innerHTML = '<div style="padding:20px; text-align:center; color:#64748b;">No files available</div>';
        } else {
            fileListDiv.innerHTML = simulatedFiles.map(f => 
                `<div>📄 <b>${f}</b> 
                    <button class="btn btn-primary btn-inline" style="padding:8px 16px; font-size:0.85rem;" onclick="downloadP2PFile('${f}')">⬇️ Download</button>
                </div>`
            ).join('') + '<div style="margin-top:10px; padding:8px; background:#fef3c7; border-radius:6px; font-size:0.85rem; color:#92400e">ℹ️ Running in Simulation Mode</div>';
        }
    }
}

async function downloadP2PFile(filename) {
    try {
        const response = await fetch(`http://localhost:8080/api/p2p?action=download&file=${encodeURIComponent(filename)}`);
        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        } else {
            alert('Failed to download file');
        }
    } catch (e) {
        // Simulation mode - create a dummy file
        enableSimulation();
        const content = `This is a simulated file: ${filename}\n\nGenerated in Simulation Mode.\nFile content would be downloaded from P2P node in production.`;
        const blob = new Blob([content], { type: 'text/plain' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    }
}

async function uploadP2PFile() {
    const fileInput = document.getElementById('p2pFileInput');
    const file = fileInput.files[0];
    
    if (!file) {
        alert('Please select a file');
        return;
    }
    
    try {
        // Read file as array buffer
        const arrayBuffer = await file.arrayBuffer();
        const bytes = new Uint8Array(arrayBuffer);
        
        // Connect to P2P server via socket (we'll need to do this via a proxy endpoint)
        // For now, show a message
        alert('File upload via P2P requires direct socket connection. This feature can be enhanced with a WebSocket proxy.');
        
        // Refresh file list
        refreshP2PFiles();
    } catch (e) {
        alert('Error uploading file: ' + e.message);
    }
}

// --- SIMULATION DATA ---
let simulatedNotices = [
    {title: "System", message: "Simulation Mode Active (Backend Offline)", date: Date.now()},
    {title: "Admin", message: "Java Backend not detected.", date: Date.now()}
];
let simMessStats = [12, 4, 1];

// Initialize stats display
function initStats() {
    document.getElementById('statGood').textContent = '-';
    document.getElementById('statAverage').textContent = '-';
    document.getElementById('statPoor').textContent = '-';
}

function enableSimulation() {
    if (!IS_SIMULATION) {
        IS_SIMULATION = true;
        document.getElementById('status-indicator').style.display = 'inline-block';
    }
}

function hideSimStatus() {
    document.getElementById('status-indicator').style.display = 'none';
}

// Init
document.addEventListener('DOMContentLoaded', () => {
    initStats();
    loadNotices();
    loadStats();
    refreshP2PFiles();
    // Poll every 5 seconds unless in simulation mode
    setInterval(() => { 
        if (!IS_SIMULATION) { 
            loadNotices(); 
            loadStats(); 
        } 
    }, 5000);
});

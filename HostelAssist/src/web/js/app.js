// --- SMART SIMULATION LOGIC ---
// If Backend is offline (like on Vercel), we switch to simulation mode
let IS_SIMULATION = false;

function show(id) {
    document.querySelectorAll('.card').forEach(c => c.classList.remove('active'));
    document.getElementById(id).classList.add('active');
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    // Find the nav item that called this function and activate it
    // Note: Since 'event' is deprecated, we rely on the click handler context
    const navItems = document.querySelectorAll('.nav-item');
    for (let item of navItems) {
        if (item.textContent.toLowerCase().includes(id) || 
           (id === 'p2p' && item.textContent.includes('P2P'))) {
            item.classList.add('active');
            break;
        }
    }
}

// --- NOTICES ---
async function loadNotices() {
    try {
        // Try Local Java Backend first
        const res = await fetch('http://localhost:8080/api/notices');
        if (!res.ok) throw new Error("Offline");
        const data = await res.json();
        renderNotices(data);
        hideSimStatus();
    } catch (e) {
        // If failed (Vercel), switch to simulation
        enableSimulation();
        renderNotices(simulatedNotices);
    }
}

async function postNotice() {
    const txt = document.getElementById('noticeInput').value;
    if (!txt) return;
    try {
        await fetch('http://localhost:8080/api/notices', { method: 'POST', body: txt });
        loadNotices();
    } catch (e) {
        // FALLBACK
        simulatedNotices.push(txt);
        renderNotices(simulatedNotices);
        alert("Notice posted (Simulation Mode)");
    }
    document.getElementById('noticeInput').value = "";
}

function renderNotices(list) {
    document.getElementById('noticeList').innerHTML = list.map(n => `<div>${n}</div>`).join('');
}

// --- MESS ---
async function vote(type) {
    try {
        await fetch('http://localhost:8081/mess/feedback', { method: 'POST', body: 'type=' + type });
        loadStats();
    } catch (e) {
        // FALLBACK
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
    document.getElementById('messStats').innerHTML = `Good: ${g} &nbsp;|&nbsp; Average: ${a} &nbsp;|&nbsp; Poor: ${p}`;
}

// --- SIMULATION DATA ---
let simulatedNotices = ["System: Simulation Mode Active (Vercel)", "Admin: Java Backend not detected."];
let simMessStats = [12, 4, 1];

function enableSimulation() {
    if (!IS_SIMULATION) {
        IS_SIMULATION = true;
        document.getElementById('status-indicator').style.display = 'inline-block';
    }
}

function hideSimStatus() {
    document.getElementById('status-indicator').style.display = 'none';
}

// --- VISUAL SIMULATIONS FOR SOCKETS/RMI ---
// Browsers cannot do raw TCP sockets or RMI, so these are always simulations in a web UI context
function simulateSocket(btn) {
    let orig = btn.innerText;
    btn.innerText = "Connecting...";
    btn.disabled = true;
    setTimeout(() => {
        btn.innerText = "Sending Data...";
        setTimeout(() => {
            alert("Server ACK: Complaint Registered via TCP.");
            btn.innerText = orig;
            btn.disabled = false;
        }, 800);
    }, 800);
}

function simulateRMI() {
    let room = document.getElementById('roomInput').value || "101";
    document.getElementById('rmiResult').innerHTML = `<i>Invoking Remote Method on Registry...</i>`;
    setTimeout(() => {
        document.getElementById('rmiResult').innerHTML =
            `<div style="background:#e0e7ff; padding:10px; border-radius:6px; color:#3730a3">
                <b>Result:</b> Room ${room}: Occupied. Warden: Mr. Smith.
             </div>`;
    }, 1000);
}

// Init
document.addEventListener('DOMContentLoaded', () => {
    loadNotices();
    loadStats();
    // Poll every 5 seconds unless in simulation mode
    setInterval(() => { if (!IS_SIMULATION) { loadNotices(); loadStats(); } }, 5000);
});
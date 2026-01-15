
function showSection(id) {
    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.nav-links li').forEach(l => l.classList.remove('active'));
    
    document.getElementById(id).classList.add('active');
    
    const titles = {
        'dashboard': 'Dashboard Overview',
        'notices': 'Notice Board (REST API)',
        'complaints': 'Complaints (Socket)',
        'rooms': 'Room Details (RPC)',
        'resources': 'P2P File Sharing',
        'mess': 'Mess Feedback (Shared Mem)'
    };
    document.getElementById('page-title').innerText = titles[id] || 'Dashboard';
}


const API_URL = "/api/notices"; 

async function fetchNotices() {
    try {
        const response = await fetch(API_URL);
        const notices = await response.json();
        
        const container = document.getElementById('notice-list-container');
        container.innerHTML = ''; 
        [...notices].reverse().forEach(text => {
            const div = document.createElement('div');
            div.className = 'notice-item';
            div.innerHTML = `<p>${text}</p>`;
            container.appendChild(div);
        });
    } catch (error) {
        console.error("Error fetching notices:", error);
    }
}

async function postNotice() {
    const input = document.getElementById('noticeInput');
    const text = input.value;
    if (!text) return;

    try {
        await fetch(API_URL, {
            method: 'POST',
            body: text
        });
        input.value = ''; 
        fetchNotices();  
    } catch (error) {
        alert("Failed to connect to Python backend.");
    }
}

document.addEventListener('DOMContentLoaded', () => {
    fetchNotices();
    setInterval(fetchNotices, 5000);
});

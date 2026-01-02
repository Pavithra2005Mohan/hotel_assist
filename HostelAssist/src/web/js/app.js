
function showSection(sectionId) {

    document.querySelectorAll('.content-section').forEach(sec => {
        sec.classList.remove('active');
    });
 
    document.querySelectorAll('.nav-links li').forEach(li => {
        li.classList.remove('active');
    });

    document.getElementById(sectionId).classList.add('active');

    const titles = {
        'dashboard': 'Dashboard Overview',
        'notices': 'Notice Board (REST API)',
        'complaints': 'Complaints (Socket)',
        'rooms': 'Room Details (RMI)',
        'resources': 'Resource Sharing (P2P)',
        'mess': 'Mess Feedback (Shared Mem)'
    };
    document.getElementById('page-title').innerText = titles[sectionId];
}
-
const REST_API_URL = "http://localhost:8080/api/notices";

async function postNotice() {
    const input = document.getElementById('noticeInput');
    const text = input.value;
    if (!text) return;

    addNoticeToUI(text, "Sending...");

    try {
        await fetch(REST_API_URL, {
            method: "POST",
            body: text
        });
        fetchNotices(); 
        input.value = ""; 
    } catch (e) {
        alert("Error connecting to REST Server. Is Java Module 3 running?");
    }
}

async function fetchNotices() {
    try {
        const res = await fetch(REST_API_URL);
        const notices = await res.json();
        
        const container = document.getElementById('notice-list-container');
        container.innerHTML = ""; // Clear list
        
        notices.forEach(n => addNoticeToUI(n, "Rest API"));
    } catch (e) {
        console.log("REST API offline or CORS error");
    }
}

function addNoticeToUI(text, source) {
    const container = document.getElementById('notice-list-container');
    const div = document.createElement('div');
    div.className = 'notice-item';
    div.innerHTML = `
        <span class="notice-date">Source: ${source}</span>
        <p>${text}</p>
    `;
    container.prepend(div);
}


function submitComplaint(event) {
    event.preventDefault();

    const box = document.getElementById('socket-response');
    box.style.display = 'block';
    box.style.background = '#dcfce7'; 
    box.style.color = '#166534';
    box.style.padding = '10px';
    box.style.borderRadius = '6px';
    box.innerHTML = `<i class="fa-solid fa-check"></i> <strong>Simulation:</strong> Data sent to Port 5000. Waiting for ACK...`;
}


function fetchRoomInfo() {
    const room = document.getElementById('roomSearch').value;
    const display = document.getElementById('rmi-result');
    
    display.innerHTML = `<p style="color: blue"><i class="fa-solid fa-spinner fa-spin"></i> Invoking Remote Method: getRoomDetails('${room}')...</p>`;
 
    setTimeout(() => {
        display.innerHTML = `
            <div style="background: #f1f5f9; padding: 15px; border-radius: 8px;">
                <h4><i class="fa-solid fa-door-open"></i> Room ${room}</h4>
                <p><strong>Warden:</strong> Mr. Sharma (RMI Object #44)</p>
                <p><strong>Occupants:</strong> [John Doe, Jane Smith]</p>
                <p><em>Fetched from Java RMI Registry</em></p>
            </div>
        `;
    }, 1500);
}


function sendVote(type) {

    alert(`Vote '${type}' sent to Shared Memory Segment!`);

    const green = document.querySelector('.bar-segment.green');
    green.style.width = (parseInt(green.style.width) + 5) + "%"; 
}
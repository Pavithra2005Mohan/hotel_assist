# Hostel Assist - Distributed Systems Lab Project

## Project Overview

**Hostel Assist** is a distributed web application designed to demonstrate various distributed communication models. The application provides hostel management utilities including complaint management, room information services, notice board, peer-to-peer file sharing, and mess feedback systems.

## Group Information

- **Course**: 23CSE312-DS (Distributed Systems Lab)
- **Project Type**: Distributed Web Application
- **Technology Stack**: Java (Backend), HTML/CSS/JavaScript (Frontend)

---

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Web Browser (Client)                       │
│                  http://localhost:8080                        │
└──────────────────────────┬────────────────────────────────────┘
                           │
                           │ HTTP Requests
                           │
┌──────────────────────────▼────────────────────────────────────┐
│              Main Server (Port 8080)                          │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  NoticeController (REST API)                            │  │
│  │  - Serves Web UI                                       │  │
│  │  - REST API Endpoints                                  │  │
│  │  - Proxy Handlers (Socket/RMI/P2P)                     │  │
│  └────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Socket Server│  │ RMI Registry │  │ P2P Node     │
│ Port 5000    │  │ Port 1099    │  │ Port 6000   │
└──────────────┘  └──────────────┘  └──────────────┘
        │                  │                  │
        ▼                  ▼                  ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Mess Server  │  │              │  │              │
│ Port 8081    │  │              │  │              │
└──────────────┘  └──────────────┘  └──────────────┘
```

### Module Communication Models

#### Module 1: Socket Programming (Port 5000)
```
Client → TCP Socket → ComplaintServer → ComplaintService → In-Memory List
```

#### Module 2: Java RMI (Port 1099)
```
Client → HTTP Proxy → RMI Registry → RoomInfoImpl → In-Memory Map
```

#### Module 3: REST API (Port 8080)
```
Client → HTTP GET/POST → NoticeController → NoticeService → In-Memory List
```

#### Module 4: Peer-to-Peer (Port 6000)
```
Peer A ←→ TCP Socket ←→ Peer B
Each peer acts as both client and server
```

#### Module 5: Shared Memory (Port 8081)
```
Multiple Clients → HTTP → MessController → SharedMemoryStore (Synchronized)
```

---

## Modules Description

### Module 1: Socket Programming - Complaint Management System

**Communication Model**: TCP Socket Programming

**Functionality**:
- Students submit hostel complaints via web UI
- Complaints include: Room number, Category (Water/Electricity/Cleanliness/Other), Description
- Server receives complaints using socket programming
- Server sends acknowledgment to client

**Technical Details**:
- **Port**: 5000
- **Protocol**: TCP Socket
- **Storage**: In-memory `ArrayList<Complaint>`
- **Concurrency**: Multi-threaded server (one thread per client)

**Why In-Memory?**
- Complaints are temporary and don't require persistence
- System restart clears old complaints (acceptable for demo)
- Focus is on socket communication, not data persistence

**Files**:
- `ComplaintServer.java` - Socket server implementation
- `ComplaintService.java` - Business logic and in-memory storage

---

### Module 2: Java RMI - Room Information Service

**Communication Model**: Remote Method Invocation (RMI)

**Functionality**:
- Students search for hostel room details
- Returns: Room number, Occupant names, Warden contact details
- Uses Java RMI for remote method calls

**Technical Details**:
- **Port**: 1099 (RMI Registry)
- **Protocol**: Java RMI
- **Storage**: In-memory `HashMap<String, RoomData>`
- **Remote Methods**:
  1. `getRoomDetails(String roomNo)` - Returns room and occupant info
  2. `getWardenContact(String roomNo)` - Returns warden contact details

**Why In-Memory?**
- Room data is relatively static
- Changes infrequently (only during semester changes)
- Suitable for demo purposes

**Files**:
- `HostelInterface.java` - RMI remote interface
- `RoomInfoImpl.java` - RMI implementation
- `RoomInfoServer.java` - RMI registry setup

---

### Module 3: RPC (REST API) - Notice Board System

**Communication Model**: RESTful API (HTTP-based RPC)

**Functionality**:
- Admin adds hostel notices
- Students view notices via UI
- Notice details: Title, Message, Date

**Technical Details**:
- **Port**: 8080
- **Protocol**: HTTP REST
- **Endpoints**:
  - `GET /api/notices` - Retrieve all notices
  - `POST /api/notices` - Add new notice
- **Storage**: In-memory `List<Notice>`
- **Stateless**: Each request is independent

**Why In-Memory?**
- Notices are temporary announcements
- Old notices can be cleared on restart
- Focus on stateless REST principles

**Files**:
- `NoticeController.java` - HTTP server and REST endpoints
- `NoticeService.java` - Business logic
- `NoticeStore.java` - In-memory storage

---

### Module 4: Peer-to-Peer (P2P) - Resource Sharing System

**Communication Model**: Decentralized Peer-to-Peer

**Functionality**:
- Students share academic resources (PDFs, notes)
- Each peer can upload and download files
- No centralized file storage server
- Direct peer-to-peer communication

**Technical Details**:
- **Port**: 6000
- **Protocol**: TCP Socket (P2P)
- **Commands**:
  - `LIST` - List available files
  - `GET <filename>` - Download file
  - `UPLOAD <filename> <size> <type>` - Upload file
- **Storage**: In-memory `ConcurrentHashMap<String, byte[]>`

**Why In-Memory?**
- Files are temporary shares
- Focus on P2P communication model
- Demonstrates decentralized architecture

**Files**:
- `PeerNode.java` - P2P node implementation (client and server)

---

### Module 5: Shared Memory - Mess Feedback Live Counter

**Communication Model**: Shared Memory with Synchronization

**Functionality**:
- Students submit mess feedback (Good/Average/Poor)
- Live feedback counts displayed on UI
- Multiple users update simultaneously
- Proper synchronization prevents race conditions

**Technical Details**:
- **Port**: 8081
- **Protocol**: HTTP REST
- **Storage**: Shared static variables with `synchronized` methods
- **Synchronization**: Java `synchronized` keyword (acts as mutex)
- **Thread Safety**: All access methods are synchronized

**Why In-Memory?**
- Feedback counts are temporary statistics
- Resets on server restart (acceptable for demo)
- Demonstrates shared memory and synchronization concepts

**Files**:
- `MessController.java` - HTTP server
- `MessFeedbackService.java` - Business logic
- `SharedMemoryStore.java` - Synchronized shared memory

---

## In-Memory Data Design

### Data Structures Used

1. **Complaints Module**: `ArrayList<Complaint>` (thread-safe via synchronization)
2. **Room Info Module**: `HashMap<String, RoomData>` (read-only after initialization)
3. **Notice Board Module**: `List<Notice>` (synchronized list)
4. **P2P Module**: `ConcurrentHashMap<String, byte[]>` (thread-safe)
5. **Mess Feedback Module**: Static `int` variables with `synchronized` methods

### Why In-Memory Storage is Appropriate

1. **Temporary Data**: All modules handle temporary, session-based data
2. **Demo Focus**: Emphasis on distributed communication, not persistence
3. **Real-World Analogy**: Similar to caching layers, session stores, live dashboards
4. **Restart Behavior**: Data loss on restart is acceptable for this demo

### Impact of Server Restart

- **Complaints**: All complaints are lost (acceptable - new session starts)
- **Room Info**: Pre-populated data is lost (re-initialized on startup)
- **Notices**: All notices are lost (acceptable - temporary announcements)
- **P2P Files**: All shared files are lost (acceptable - temporary sharing)
- **Mess Feedback**: Counts reset to initial values (acceptable - daily feedback)

---

## Setup and Compilation

### Prerequisites

- Java JDK 8 or higher
- Web browser (Chrome, Firefox, Edge)

### Compilation Steps

1. **Navigate to project directory**:
   ```bash
   cd hotel_assist/HostelAssist
   ```

2. **Compile Java files**:
   ```bash
   javac -d bin src/main/**/*.java
   ```
   
   Or compile individually:
   ```bash
   javac -d bin src/main/Main.java
   javac -d bin src/main/modules/**/*.java
   ```

3. **Run the application**:
   ```bash
   java -cp bin main.Main
   ```

### Expected Output

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

### Accessing the Application

1. Open web browser
2. Navigate to: `http://localhost:8080`
3. All modules are accessible via the sidebar navigation

---

## Running Individual Modules

### Module 1: Socket Server (Standalone)
```bash
java -cp bin main.modules.complaints.ComplaintServer
```

### Module 2: RMI Server (Standalone)
```bash
java -cp bin main.modules.roominfo.RoomInfoServer
```

### Module 3: REST Server (Standalone)
```bash
java -cp bin main.modules.noticeboard.NoticeController
```

### Module 4: P2P Node (Standalone)
```bash
java -cp bin main.modules.p2p.PeerNode
```

### Module 5: Mess Server (Standalone)
```bash
java -cp bin main.modules.mess.MessController
```

---

## Testing the Application

### Module 1: Socket Programming
1. Navigate to "Complaints" tab
2. Enter room number (e.g., "101")
3. Select category (Water/Electricity/Cleanliness/Other)
4. Enter description
5. Click "Submit Complaint"
6. Verify acknowledgment message

### Module 2: Java RMI
1. Navigate to "Rooms" tab
2. Enter room number (e.g., "101", "102", "201")
3. Click "Search Registry"
4. Verify room details and warden contact

### Module 3: REST API
1. Navigate to "Notices" tab
2. View existing notices
3. Enter title and message
4. Click "Post Notice"
5. Verify notice appears in list

### Module 4: P2P File Sharing
1. Navigate to "P2P Sharing" tab
2. View available files
3. Click "Download" on any file
4. Verify file download

### Module 5: Shared Memory
1. Navigate to "Mess" tab
2. Click any feedback button (Good/Average/Poor)
3. Verify live counter updates
4. Open multiple browser tabs and vote simultaneously
5. Verify synchronized updates

---

## Architecture Diagrams

### Communication Flow - Socket Module
```
┌──────────┐         ┌──────────┐         ┌──────────┐
│ Browser │ ─HTTP──>│  Proxy  │ ─TCP──> │  Socket │
│  (UI)   │         │ Handler │         │ Server  │
└──────────┘         └──────────┘         └──────────┘
                                              │
                                              ▼
                                        ┌──────────┐
                                        │Complaint │
                                        │ Service  │
                                        └──────────┘
```

### Communication Flow - RMI Module
```
┌──────────┐         ┌──────────┐         ┌──────────┐
│ Browser │ ─HTTP──>│  Proxy  │ ─RMI──> │ Registry │
│  (UI)   │         │ Handler │         │  (1099)  │
└──────────┘         └──────────┘         └──────────┘
                                              │
                                              ▼
                                        ┌──────────┐
                                        │RoomInfo  │
                                        │   Impl   │
                                        └──────────┘
```

### Communication Flow - REST Module
```
┌──────────┐         ┌──────────┐
│ Browser │ ─HTTP──>│  REST    │
│  (UI)   │         │  Server  │
└──────────┘         └──────────┘
                          │
                          ▼
                    ┌──────────┐
                    │ Notice   │
                    │ Service  │
                    └──────────┘
```

### Communication Flow - P2P Module
```
┌──────────┐         ┌──────────┐
│ Browser │ ─HTTP──>│  Proxy   │
│  (UI)   │         │ Handler  │
└──────────┘         └──────────┘
                          │
                          ▼
                    ┌──────────┐
                    │  P2P     │◄──TCP Socket──►│  P2P     │
                    │  Node A  │                │  Node B  │
                    └──────────┘                └──────────┘
```

### Communication Flow - Shared Memory Module
```
┌──────────┐                    ┌──────────┐
│ Browser │ ───HTTP POST──────>│   Mess   │
│  (UI)   │                    │ Controller│
└──────────┘                    └──────────┘
                                      │
                                      ▼
                            ┌──────────────────┐
                            │ SharedMemoryStore│
                            │  (Synchronized)  │
                            └──────────────────┘
                                      ▲
                                      │
                            ┌──────────┐
                            │ Browser  │
                            │  (UI)    │
                            └──────────┘
```

---

## File Structure

```
HostelAssist/
├── src/
│   ├── main/
│   │   ├── Main.java                    # Main entry point
│   │   └── modules/
│   │       ├── complaints/              # Module 1: Socket Programming
│   │       │   ├── ComplaintServer.java
│   │       │   └── ComplaintService.java
│   │       ├── roominfo/                # Module 2: Java RMI
│   │       │   ├── HostelInterface.java
│   │       │   ├── RoomInfoImpl.java
│   │       │   └── RoomInfoServer.java
│   │       ├── noticeboard/             # Module 3: REST API
│   │       │   ├── NoticeController.java
│   │       │   ├── NoticeService.java
│   │       │   └── NoticeStore.java
│   │       ├── p2p/                     # Module 4: Peer-to-Peer
│   │       │   └── PeerNode.java
│   │       └── mess/                    # Module 5: Shared Memory
│   │           ├── MessController.java
│   │           ├── MessFeedbackService.java
│   │           └── SharedMemoryStore.java
│   └── web/                             # Frontend
│       ├── index.html
│       ├── css/
│       │   └── style.css
│       └── js/
│           └── app.js
└── README.md
```

---

## Troubleshooting

### Port Already in Use
If you get "Address already in use" error:
- **Port 5000**: Stop any application using this port
- **Port 8080**: Change port in `NoticeController.java`
- **Port 1099**: RMI registry might already be running
- **Port 6000**: Another P2P node might be active
- **Port 8081**: Change port in `MessController.java`

### RMI Connection Refused
- Ensure RMI registry is started before clients connect
- Check firewall settings
- Verify port 1099 is not blocked

### Socket Connection Failed
- Ensure `ComplaintServer` is running
- Check if port 5000 is accessible
- Verify proxy handler in `NoticeController`

### Files Not Loading
- Ensure all servers are running
- Check browser console for errors
- Verify file paths in `WebHandler`

---

## Key Learning Outcomes

### Distributed Communication Models
1. **Socket Programming**: Direct TCP/IP communication
2. **RMI**: Remote method invocation in Java
3. **REST API**: Stateless HTTP-based communication
4. **P2P**: Decentralized peer-to-peer architecture
5. **Shared Memory**: Synchronized concurrent access

### System Design Concepts
- Client-server architecture
- Multi-threaded server design
- Stateless vs stateful services
- Synchronization and concurrency
- Proxy patterns for protocol bridging

### Practical Skills
- Java networking programming
- Web application development
- API design and implementation
- Concurrent programming
- System integration

---

## Future Enhancements

1. **Database Integration**: Replace in-memory storage with persistent database
2. **Authentication**: Add user login and authorization
3. **WebSocket Support**: Real-time updates for notices and feedback
4. **File Upload UI**: Complete P2P file upload via web interface
5. **Distributed Deployment**: Deploy modules on separate machines
6. **Load Balancing**: Multiple instances for scalability
7. **Message Queue**: Asynchronous processing for complaints

---

## Contributors

- Group Members: [Add your group member names and roll numbers]
- Group Leader: [Add group leader name]

---

## License

This project is created for educational purposes as part of the Distributed Systems Lab course.

---

## Contact

For questions or issues, contact the group leader or course instructor.

---

**Note**: This application is designed for demonstration purposes. In a production environment, proper security measures, database persistence, and error handling should be implemented.

import http.server
import socketserver
import json
import os
import sys

NOTICES = [
    "System: Python Backend Initialized.",
    "Admin: Welcome to the Distributed Hostel Dashboard."
]

class NoticeHandler(http.server.SimpleHTTPRequestHandler):
    
    def do_GET(self):
        if self.path == '/api/notices':
            self.send_response(200)
            self.send_header('Content-type', 'application/json')
            self.send_header('Access-Control-Allow-Origin', '*')
            self.end_headers()
            self.wfile.write(json.dumps(NOTICES).encode())
            return

        if self.path == '/':
            self.path = '/web/index.html'
        
        elif not self.path.startswith('/web/'):
            self.path = '/web' + self.path
        return http.server.SimpleHTTPRequestHandler.do_GET(self)

    def do_POST(self):
        if self.path == '/api/notices':
            try:
                content_length = int(self.headers['Content-Length'])
                post_data = self.rfile.read(content_length)
                new_notice = post_data.decode('utf-8')
                
                if new_notice.strip():
                    NOTICES.append(new_notice)
                    if len(NOTICES) > 20: 
                        NOTICES.pop(0)

                    self.send_response(200)
                    self.send_header('Content-type', 'application/json')
                    self.send_header('Access-Control-Allow-Origin', '*')
                    self.end_headers()
                    self.wfile.write(json.dumps({"status": "success", "message": "Notice added"}).encode())
                else:
                    self.send_error(400, "Empty notice")
            except Exception as e:
                print(f"Error in POST: {e}")
                self.send_error(500, "Server Error")

    def do_OPTIONS(self):
        self.send_response(200, "ok")
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header("Access-Control-Allow-Headers", "X-Requested-With, Content-Type")
        self.end_headers()

def start_notice_server(port):
    os.chdir(os.path.dirname(os.path.abspath(sys.argv[0])))
    
    handler = NoticeHandler
    socketserver.TCPServer.allow_reuse_address = True
    
    with socketserver.TCPServer(("", port), handler) as httpd:
        print(f"[Module 3] REST API & Web Server running on http://localhost:{port}")
        httpd.serve_forever()

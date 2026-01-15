import multiprocessing
import time
import sys

from modules.notice_board import start_notice_server



def main():
    print("==========================================")
    print("   HOSTEL ASSIST DISTRIBUTED SYSTEM")
    print("   (Python Implementation)")
    print("==========================================")

    processes = []

    try:

        p_notice = multiprocessing.Process(target=start_notice_server, args=(8080,))
        p_notice.start()
        processes.append(p_notice)
        print("[INIT] Module 3 (REST API & Web UI) started on Port 8080")
        print("\n[SUCCESS] System is running!")
        print(">>> Open Dashboard: http://localhost:8080")
        
        while True:
            time.sleep(1)

    except KeyboardInterrupt:
        print("\n[SHUTDOWN] Stopping all distributed modules...")
        for p in processes:
            p.terminate()
        sys.exit(0)

if __name__ == "__main__":
    main()

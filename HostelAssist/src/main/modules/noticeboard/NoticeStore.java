package main.modules.noticeboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoticeStore {
    private static List<Notice> notices = Collections.synchronizedList(new ArrayList<>());

    public static class Notice {
        private String title;
        private String message;
        private long date;
        
        public Notice(String title, String message) {
            this.title = title;
            this.message = message;
            this.date = System.currentTimeMillis();
        }
        
        public Notice(String title, String message, long date) {
            this.title = title;
            this.message = message;
            this.date = date;
        }
        
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public long getDate() { return date; }
    }

    static {
        notices.add(new Notice("System Initialized", "Java Backend is now running."));
        notices.add(new Notice("Welcome", "Welcome to Hostel Assist - Your Digital Hostel Management System."));
    }

    public static void add(String title, String message) {
        notices.add(new Notice(title, message));
        if(notices.size() > 20) notices.remove(0);
    }
    
    public static void add(Notice notice) {
        notices.add(notice);
        if(notices.size() > 20) notices.remove(0);
    }

    public static List<Notice> getAll() {
        return new ArrayList<>(notices);
    }
}
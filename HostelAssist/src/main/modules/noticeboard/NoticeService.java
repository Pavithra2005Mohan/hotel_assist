package main.modules.noticeboard;

import java.util.List;

public class NoticeService {
    
    public static void addNotice(String title, String message) {
        if(title != null && !title.trim().isEmpty() && 
           message != null && !message.trim().isEmpty()) {
            NoticeStore.add(title, message);
        }
    }
    
    // For backward compatibility - parse simple text as title
    public static void addNotice(String text) {
        if(text != null && !text.trim().isEmpty()) {
            // If text contains "|", split into title and message
            if (text.contains("|")) {
                String[] parts = text.split("\\|", 2);
                addNotice(parts[0].trim(), parts.length > 1 ? parts[1].trim() : "");
            } else {
                // Use text as title, empty message
                NoticeStore.add(text, "");
            }
        }
    }

    public static String getNoticesAsJSON() {
        List<NoticeStore.Notice> list = NoticeStore.getAll();
        StringBuilder json = new StringBuilder("[");
        synchronized (list) {
            for (int i = 0; i < list.size(); i++) {
                NoticeStore.Notice notice = list.get(i);
                json.append("{");
                json.append("\"title\":\"").append(escapeJson(notice.getTitle())).append("\",");
                json.append("\"message\":\"").append(escapeJson(notice.getMessage())).append("\",");
                json.append("\"date\":").append(notice.getDate());
                json.append("}");
                if (i < list.size() - 1) json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
    
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
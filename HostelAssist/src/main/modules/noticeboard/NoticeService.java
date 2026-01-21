package main.modules.noticeboard;

import java.util.List;

public class NoticeService {
    
    public static void addNotice(String text) {
        if(text != null && !text.trim().isEmpty()) {
            NoticeStore.add(text);
        }
    }

    public static String getNoticesAsJSON() {
        List<String> list = NoticeStore.getAll();
        StringBuilder json = new StringBuilder("[");
        synchronized (list) {
            for (int i = 0; i < list.size(); i++) {
                json.append("\"").append(list.get(i).replace("\"", "\\\"")).append("\"");
                if (i < list.size() - 1) json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
}
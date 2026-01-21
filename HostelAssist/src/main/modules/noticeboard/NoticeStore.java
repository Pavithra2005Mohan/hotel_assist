package main.modules.noticeboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoticeStore {
    private static List<String> notices = Collections.synchronizedList(new ArrayList<>());

    static {
        notices.add("System: Java Backend Initialized.");
        notices.add("Admin: Welcome to Hostel Assist.");
    }

    public static void add(String notice) {
        notices.add(notice);
        if(notices.size() > 20) notices.remove(0);
    }

    public static List<String> getAll() {
        return notices;
    }
}
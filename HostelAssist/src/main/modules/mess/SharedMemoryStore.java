package main.modules.mess;

public class SharedMemoryStore {

    private static int goodCount = 5;
    private static int averageCount = 2;
    private static int poorCount = 1;

    public static synchronized void addFeedback(String type) {
        if (type == null) return;
        switch (type.toLowerCase()) {
            case "good": goodCount++; break;
            case "average": averageCount++; break;
            case "poor": poorCount++; break;
        }
    }

    public static synchronized int[] getCounts() {
        return new int[]{goodCount, averageCount, poorCount};
    }
}
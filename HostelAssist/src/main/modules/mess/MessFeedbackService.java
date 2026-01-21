package main.modules.mess;

public class MessFeedbackService {

    public static void submitFeedback(String feedbackType) {
        if (feedbackType != null && !feedbackType.isEmpty()) {
            SharedMemoryStore.addFeedback(feedbackType);
        }
    }

    public static String getFeedbackJSON() {
        int[] c = SharedMemoryStore.getCounts();
        return "{\"good\":" + c[0] + ",\"average\":" + c[1] + ",\"poor\":" + c[2] + "}";
    }
}
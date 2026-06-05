package app.websocket;

public class ChatRoomUtil {

    // Always produces the same ID regardless of who calls it
    // e.g. menteeId=3, mentorId=7 → "3_7" and mentorId=7, menteeId=3 → "3_7"
    public static String getRoomId(String menteeId, String mentorId) {
        if (menteeId == null || mentorId == null) {
            return "";
        }

        String first = menteeId.trim();
        String second = mentorId.trim();
        if (first.compareTo(second) <= 0) {
            return first + "_" + second;
        }

        return second + "_" + first;
    }
}
package app.websocket;

public class ChatRoomUtil {

    // Always produces the same ID regardless of who calls it
    // e.g. menteeId=3, mentorId=7 → "3_7"
    public static String getRoomId(String menteeId, String mentorId) {

        return menteeId + "_" + mentorId;
    }
}

package app.websocket;

import jakarta.websocket.server.*;
import app.utility.logging.AppLogger;
import jakarta.websocket.*;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.websocket.Session;

@ServerEndpoint("/chat/{roomId}")
public class ChatEndpoint {

    private static final Logger logger = AppLogger.getLogger(ChatEndpoint.class);

    // roomId → connected sessions (thread-safe)
    private static final Map<String, Set<Session>> rooms = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId) {
        rooms.computeIfAbsent(roomId, k -> Collections.synchronizedSet(new HashSet<>()))
                .add(session);

        // tag session with its room for cleanup on close
        session.getUserProperties().put("roomId", roomId);

        int totalInRoom = rooms.get(roomId).size();
        logger.info("[ChatEndpoint] Client joined room: {} | total sessions: {}", roomId, totalInRoom);
    }

    @OnMessage
    public void onMessage(String message, Session sender, @PathParam("roomId") String roomId) {
        Set<Session> room = rooms.get(roomId);
        if (room == null) {
            logger.warn("[ChatEndpoint] Room {} not found for message broadcast", roomId);
            return;
        }

        logger.debug("[ChatEndpoint] Broadcasting message in room {} to {} session(s)", roomId, room.size());

        // broadcast to everyone in the room except sender
        for (Session s : room) {
            if (s.isOpen() && !s.getId().equals(sender.getId())) {
                try {
                    s.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    logger.error("[ChatEndpoint] Failed to send message in room {}", roomId, e);
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("roomId") String roomId) {
        Set<Session> room = rooms.get(roomId);
        if (room != null) {
            room.remove(session);
            if (room.isEmpty()) {
                rooms.remove(roomId);
                logger.info("[ChatEndpoint] Room {} closed (empty)", roomId);
            } else {
                logger.info("[ChatEndpoint] Client left room: {} | remaining: {}", roomId, room.size());
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("[ChatEndpoint] WebSocket error", error);
    }
}



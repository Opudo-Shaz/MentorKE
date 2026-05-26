package app.action;

import app.bean.MenteeBean;
import app.bean.MentorBean;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.model.Mentee;
import app.model.Mentor;
import app.utility.logging.AppLogger;
import app.websocket.ChatRoomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ApplicationScoped
@Action(value = "conversations", label = "Conversations API")
@RolesAllowed({"mentor","mentee","admin"})
public class Conversations extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(Conversations.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Inject
    private MenteeBean menteeBean;

    @Inject
    private MentorBean mentorBean;


     // GET /app/conversations/list
     // Returns JSON list of conversations for the current user
    @ActionGetMethod("list")
    public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) {
            sendJsonError(response, "Not authenticated", 401);
            return;
        }

        try {
            String userId = getUserId(request);
            String role = getRole(request);

            logger.debug("[Conversations] Loading conversations for user {} (role: {})", userId, role);

            List<Map<String, String>> conversations = new ArrayList<>();

            if ("mentor".equalsIgnoreCase(role)) {
                // Mentor: get all mentees assigned to this mentor
                try {
                    List<Mentee> mentees = menteeBean.findByMentorId(userId);
                    logger.debug("[Conversations] Found {} mentees for mentor {}", mentees.size(), userId);

                    for (Mentee mentee : mentees) {
                        if (mentee != null && mentee.getId() != null) {String menteeId = String.valueOf(mentee.getId());
                            String roomId = ChatRoomUtil.getRoomId(menteeId, userId);

                            Map<String, String> conv = new HashMap<>();
                            conv.put("roomId", roomId);
                            conv.put("partnerName", mentee.getUsername() != null ? mentee.getUsername() : "Unknown");
                            conv.put("partnerRole", "mentee");
                            conv.put("lastActivity", "No messages");

                            conversations.add(conv);
                        }
                    }
                } catch (Exception e) {
                    logger.error("[Conversations] Error loading mentees for mentor {}", userId, e);
                }

            } else if ("mentee".equalsIgnoreCase(role)) {
                // Mentee: get their assigned mentor (if any)
                try {
                    Mentee mentee = menteeBean.getByUserId(userId);
                    if (mentee != null && mentee.getMentorId() != null) {
                        String mentorId = mentee.getMentorId();
                        try {
                            Mentor mentor = mentorBean.getById(mentorId);
                            if (mentor != null) {
                                String roomId = ChatRoomUtil.getRoomId(userId, mentorId);

                                Map<String, String> conv = new HashMap<>();
                                conv.put("roomId", roomId);
                                conv.put("partnerName", mentor.getUsername() != null ? mentor.getUsername() : "Unknown");
                                conv.put("partnerRole", "mentor");
                                conv.put("lastActivity", "No messages");

                                conversations.add(conv);
                                logger.debug("[Conversations] Mentee {} has mentor conversation with {}", userId, mentorId);
                            }
                        } catch (Exception e) {
                            logger.error("[Conversations] Error loading mentor {} for mentee {}", mentorId, userId, e);
                        }
                    } else {
                        logger.debug("[Conversations] Mentee {} has no assigned mentor", userId);
                    }
                } catch (Exception e) {
                    logger.error("[Conversations] Error loading mentee for user {}", userId, e);
                }
            }

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("conversations", conversations);
            responseMap.put("total", conversations.size());

            sendJsonResponse(response, responseMap, 200);
            logger.info("[Conversations] Returned {} conversations for user {} (role: {})", conversations.size(), userId, role);

        } catch (Exception e) {
            logger.error("[Conversations] Unexpected error", e);
            sendJsonError(response, "Failed to load conversations: " + e.getMessage(), 500);
        }
    }


     // Send JSON response

    private void sendJsonResponse(HttpServletResponse response, Object data, int status) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(mapper.writeValueAsString(data));
        out.flush();
    }


     // Send JSON error response

    private void sendJsonError(HttpServletResponse response, String message, int status) throws Exception {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        sendJsonResponse(response, error, status);
    }


     // Get role from session

    private String getRole(HttpServletRequest request) {
        Object roleObj = request.getSession().getAttribute("role");
        return roleObj != null ? roleObj.toString() : "user";
    }
}


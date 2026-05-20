<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.util.List" %>
<%@ page import="app.model.Message" %>
<%
    String ctx = request.getContextPath();
    HttpSession currentSession = request.getSession(false);
    String userId = currentSession != null ? String.valueOf(currentSession.getAttribute("userId")) : null;
    String username = currentSession != null ? (String) currentSession.getAttribute("username") : null;
    String otherUserId = (String) request.getAttribute("otherUserId");
    Object otherUser = request.getAttribute("otherUser");
    List<Message> messages = (List<Message>) request.getAttribute("messages");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Conversation - MentorKE</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background: #f6f8fb; color: #1e293b; }
        .wrap { max-width: 980px; margin: 0 auto; padding: 28px 20px 48px; }
        .hero, .card { background: #fff; border: 1px solid #e2e8f0; border-radius: 14px; box-shadow: 0 8px 28px rgba(15, 23, 42, 0.06); }
        .hero { padding: 22px 24px; margin-bottom: 16px; display: flex; justify-content: space-between; gap: 16px; align-items: flex-start; }
        .hero h1 { margin: 0 0 8px; font-size: 28px; }
        .hero p { margin: 0; color: #64748b; }
        .btn { display: inline-block; background: #0d47a1; color: #fff; text-decoration: none; border-radius: 10px; padding: 10px 14px; font-weight: 700; border: 0; cursor: pointer; }
        .btn.secondary { background: #e2e8f0; color: #0f172a; }
        .error { padding: 12px 14px; border-radius: 10px; margin-bottom: 12px; background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }
        .thread { display: flex; flex-direction: column; gap: 12px; padding: 20px; }
        .bubble { max-width: 76%; padding: 12px 14px; border-radius: 16px; }
        .bubble.mine { align-self: flex-end; background: #0d47a1; color: #fff; border-bottom-right-radius: 6px; }
        .bubble.theirs { align-self: flex-start; background: #e2e8f0; color: #0f172a; border-bottom-left-radius: 6px; }
        .meta { font-size: 11px; opacity: 0.8; margin-top: 6px; }
        .composer { padding: 20px; border-top: 1px solid #e2e8f0; display: grid; gap: 10px; }
        .composer textarea { width: 100%; min-height: 110px; resize: vertical; padding: 12px; border-radius: 12px; border: 1px solid #cbd5e1; font-family: Arial, sans-serif; }
        .composer input[type="hidden"] { display: none; }
        .empty { padding: 38px 18px; color: #64748b; text-align: center; }
        .topline { display: flex; gap: 10px; flex-wrap: wrap; align-items: center; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="hero">
        <div>
            <h1>Conversation</h1>
            <p>Chat with <strong><%= otherUserId != null ? otherUserId : "the selected user" %></strong><% if (otherUser != null) { %> (<%= otherUser.toString() %>)<% } %>.</p>
        </div>
        <div class="topline">
            <a class="btn secondary" href="<%= ctx %>/app/messaging/list-conversations">Back to inbox</a>
            <a class="btn secondary" href="<%= ctx %>/app/mentor-dashboard/">Dashboard</a>
        </div>
    </div>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="error"><%= errorMessage %></div>
    <% } %>

    <div class="card">
        <div class="thread">
            <% if (messages != null && !messages.isEmpty()) {
                   for (Message message : messages) {
                       boolean mine = userId != null && userId.equals(String.valueOf(message.getSenderId()));
            %>
            <div class="bubble <%= mine ? "mine" : "theirs" %>">
                <div><%= message.getMessage() %></div>
                <div class="meta"><%= message.getCreatedAt() != null ? message.getCreatedAt() : "" %></div>
            </div>
            <%   }
               } else { %>
            <div class="empty">
                <h3>No messages yet</h3>
                <p>Start the conversation below.</p>
            </div>
            <% } %>
        </div>

        <form class="composer" method="post" action="<%= ctx %>/app/messaging/send-message">
            <input type="hidden" name="recipientId" value="<%= otherUserId != null ? otherUserId : "" %>">
            <label for="message" style="font-weight:700;">Send a message from <%= username != null ? username : "your account" %></label>
            <textarea id="message" name="message" placeholder="Write your message here..." required></textarea>
            <div>
                <button type="submit" class="btn">Send message</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.util.List" %>
<%@ page import="app.model.Message" %>
<%
    String ctx = request.getContextPath();
    HttpSession currentSession = request.getSession(false);
    String userId = currentSession != null ? String.valueOf(currentSession.getAttribute("userId")) : null;
    String username = currentSession != null ? (String) currentSession.getAttribute("username") : null;
    List<Message> conversations = (List<Message>) request.getAttribute("conversations");
    Integer unreadCount = (Integer) request.getAttribute("unreadCount");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Messages - MentorKE</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background: #f6f8fb; color: #1e293b; }
        .wrap { max-width: 1180px; margin: 0 auto; padding: 28px 20px 48px; }
        .hero, .card { background: #fff; border: 1px solid #e2e8f0; border-radius: 14px; box-shadow: 0 8px 28px rgba(15, 23, 42, 0.06); }
        .hero { padding: 22px 24px; margin-bottom: 16px; display: flex; justify-content: space-between; gap: 16px; align-items: flex-start; }
        .hero h1 { margin: 0 0 8px; font-size: 28px; }
        .hero p { margin: 0; color: #64748b; }
        .btn { display: inline-block; background: #0d47a1; color: #fff; text-decoration: none; border-radius: 10px; padding: 10px 14px; font-weight: 700; border: 0; }
        .btn.secondary { background: #e2e8f0; color: #0f172a; }
        .badge { display: inline-block; padding: 6px 10px; border-radius: 999px; background: #e0f2fe; color: #075985; font-weight: 700; font-size: 12px; }
        .error { padding: 12px 14px; border-radius: 10px; margin-bottom: 12px; background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 10px; border-bottom: 1px solid #e2e8f0; text-align: left; vertical-align: top; }
        th { background: #f8fafc; font-size: 13px; }
        .empty { padding: 40px 18px; text-align: center; color: #64748b; }
        .topline { display: flex; gap: 10px; flex-wrap: wrap; align-items: center; }
        .snippet { color: #475569; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="hero">
        <div>
            <h1>Messages</h1>
            <p>Conversation threads and unread message counts for <strong><%= username != null ? username : "your account" %></strong>.</p>
        </div>
        <div class="topline">
            <span class="badge">Unread: <%= unreadCount != null ? unreadCount : 0 %></span>
            <a class="btn secondary" href="<%= ctx %>/app/mentor-dashboard/">Back to dashboard</a>
        </div>
    </div>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="error"><%= errorMessage %></div>
    <% } %>

    <div class="card">
        <div style="padding:18px 20px; border-bottom:1px solid #e2e8f0;">
            <h2 style="margin:0 0 4px;">Recent conversations</h2>
            <div style="color:#64748b; font-size:13px;">Tap a row to open the thread.</div>
        </div>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Partner</th>
                    <th>Last message</th>
                    <th>Time</th>
                    <th>Open</th>
                </tr>
                </thead>
                <tbody>
                <% if (conversations != null && !conversations.isEmpty() && userId != null) {
                       for (Message message : conversations) {
                           String partnerId = userId.equals(String.valueOf(message.getSenderId())) ? message.getRecipientId() : message.getSenderId();
                %>
                <tr>
                    <td><%= partnerId %></td>
                    <td class="snippet"><%= message.getMessage() != null ? (message.getMessage().length() > 90 ? message.getMessage().substring(0, 90) + "..." : message.getMessage()) : "-" %></td>
                    <td><%= message.getCreatedAt() != null ? message.getCreatedAt() : "-" %></td>
                    <td><a class="btn" href="<%= ctx %>/app/messaging/conversation?userId=<%= partnerId %>">Open</a></td>
                </tr>
                <%   }
                   } else { %>
                <tr>
                    <td colspan="4">
                        <div class="empty">
                            <h3>No conversations yet</h3>
                            <p>When mentors and mentees start messaging, threads will appear here.</p>
                        </div>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
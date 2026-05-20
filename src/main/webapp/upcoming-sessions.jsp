<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.util.List" %>
<%@ page import="app.model.Session" %>
<%
    String ctx = request.getContextPath();
    HttpSession currentSession = request.getSession(false);
    String role = currentSession != null ? (String) currentSession.getAttribute("role") : null;
    String dashboardUrl = "mentor".equalsIgnoreCase(role) ? ctx + "/app/mentor-dashboard/" : ctx + "/app/mentee-dashboard/";
    List<Session> sessions = (List<Session>) request.getAttribute("sessions");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
    Integer sessionCount = (Integer) request.getAttribute("sessionCount");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upcoming Sessions - MentorKE</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background: #f6f8fb; color: #1e293b; }
        .wrap { max-width: 1180px; margin: 0 auto; padding: 28px 20px 48px; }
        .hero, .card { background: #fff; border: 1px solid #e2e8f0; border-radius: 14px; box-shadow: 0 8px 28px rgba(15, 23, 42, 0.06); }
        .hero { padding: 22px 24px; margin-bottom: 16px; display: flex; justify-content: space-between; gap: 16px; align-items: flex-start; }
        .hero h1 { margin: 0 0 8px; font-size: 28px; }
        .hero p { margin: 0; color: #64748b; }
        .btn { display: inline-block; background: #0d47a1; color: #fff; text-decoration: none; border-radius: 10px; padding: 10px 14px; font-weight: 700; border: 0; }
        .btn.secondary { background: #e2e8f0; color: #0f172a; }
        .messages { margin-bottom: 14px; }
        .message { padding: 12px 14px; border-radius: 10px; margin-bottom: 10px; }
        .success { background: #ecfdf5; color: #166534; border: 1px solid #bbf7d0; }
        .error { background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 10px; border-bottom: 1px solid #e2e8f0; text-align: left; vertical-align: top; }
        th { background: #f8fafc; font-size: 13px; }
        .pill { display: inline-block; padding: 5px 10px; border-radius: 999px; font-size: 12px; font-weight: 700; }
        .pill.pending { background: #fef3c7; color: #92400e; }
        .pill.approved { background: #dcfce7; color: #166534; }
        .pill.completed { background: #e0f2fe; color: #075985; }
        .pill.cancelled { background: #fee2e2; color: #991b1b; }
        .actions { display: flex; gap: 8px; flex-wrap: wrap; }
        .empty { padding: 40px 18px; text-align: center; color: #64748b; }
        .table-wrap { overflow-x: auto; }
        .topline { display: flex; gap: 10px; flex-wrap: wrap; align-items: center; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="hero">
        <div>
            <h1>Upcoming Sessions</h1>
            <p>Sessions scheduled with your mentorship match.</p>
        </div>
        <div class="topline">
            <a class="btn secondary" href="<%= dashboardUrl %>">Back to dashboard</a>
            <a class="btn" href="<%= ctx %>/app/sessions/completed">Completed sessions</a>
        </div>
    </div>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="message success"><%= successMessage %></div>
    <% } %>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="message error"><%= errorMessage %></div>
    <% } %>

    <div class="card">
        <div style="padding:18px 20px; border-bottom:1px solid #e2e8f0; display:flex; justify-content:space-between; gap:10px; align-items:center;">
            <div>
                <h2 style="margin:0 0 4px;">Scheduled sessions</h2>
                <div style="color:#64748b; font-size:13px;"><%= sessionCount != null ? sessionCount : (sessions != null ? sessions.size() : 0) %> session(s) found</div>
            </div>
        </div>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Mentor</th>
                    <th>Mentee</th>
                    <th>Date</th>
                    <th>Duration</th>
                    <th>Topic</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <% if (sessions != null && !sessions.isEmpty()) {
                       for (Session scheduledSession : sessions) { %>
                <tr>
                    <td><%= scheduledSession.getId() %></td>
                    <td><%= scheduledSession.getMentorId() %></td>
                    <td><%= scheduledSession.getMenteeId() %></td>
                    <td><%= scheduledSession.getScheduledDate() != null ? scheduledSession.getScheduledDate() : "-" %></td>
                    <td><%= scheduledSession.getDurationMinutes() != null ? scheduledSession.getDurationMinutes() + " min" : "-" %></td>
                    <td><%= scheduledSession.getTopic() != null ? scheduledSession.getTopic() : "-" %></td>
                    <td><span class="pill <%= scheduledSession.getStatus() != null ? scheduledSession.getStatus().toLowerCase() : "pending" %>"><%= scheduledSession.getStatus() %></span></td>
                    <td>
                        <div class="actions">
                            <a class="btn secondary" href="<%= ctx %>/app/sessions/view?sessionId=<%= scheduledSession.getId() %>">View</a>
                            <form method="post" action="<%= ctx %>/app/sessions/cancel" style="display:inline;">
                                <input type="hidden" name="sessionId" value="<%= scheduledSession.getId() %>">
                                <button class="btn" type="submit" style="background:#fee2e2; color:#991b1b;">Cancel</button>
                            </form>
                        </div>
                    </td>
                </tr>
                <% } } else { %>
                <tr>
                    <td colspan="8">
                        <div class="empty">
                            <h3>No upcoming sessions</h3>
                            <p>When a mentorship match is scheduled, it will appear here.</p>
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
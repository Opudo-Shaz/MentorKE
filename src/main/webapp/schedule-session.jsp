<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="app.model.Mentee" %>
<%
    String ctx = request.getContextPath();
    String mentorId = request.getAttribute("mentorId") != null ? (String) request.getAttribute("mentorId") : request.getParameter("mentorId");
    String menteeId = request.getAttribute("menteeId") != null ? (String) request.getAttribute("menteeId") : request.getParameter("menteeId");
    List<Mentee> mentees = (List<Mentee>) request.getAttribute("mentees");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <title>Schedule Session</title>
    <style>
        body { font-family: Arial, Helvetica, sans-serif; background:#f6f8fa; color:#1f2937; padding:24px; }
        .card { max-width:700px; margin:20px auto; background:white; padding:20px; border-radius:8px; box-shadow:0 4px 14px rgba(16,24,40,0.06); }
        .field { margin-bottom:12px; }
        label { display:block; font-size:13px; margin-bottom:6px; color:#374151; }
        input[type="text"], input[type="datetime-local"], select { width:100%; padding:10px; border:1px solid #e6e9ef; border-radius:6px; }
        .row { display:flex; gap:10px; }
        .col { flex:1; }
        .actions { display:flex; gap:8px; justify-content:flex-end; margin-top:14px; }
        .btn { padding:8px 12px; border-radius:6px; text-decoration:none; display:inline-block; font-weight:600; }
        .btn-primary { background:#0d47a1; color:white; border:none; }
        .btn-outline { background:white; color:#0d47a1; border:1px solid #dbeafe; }
        .alert { padding:10px 12px; border-radius:6px; margin-bottom:12px; }
        .alert-error { background:#fff1f2; color:#991b1b; }
        .alert-success { background:#ecfdf5; color:#065f46; }
    </style>
</head>
<body>
    <div class="card">
        <h2>Schedule a new session</h2>
        <p>Fill out the details below to schedule a session for a mentee.</p>

        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="alert alert-error"><%= errorMessage %></div>
        <% } %>
        <% if (successMessage != null && !successMessage.isEmpty()) { %>
            <div class="alert alert-success"><%= successMessage %></div>
        <% } %>

                <% if (mentees == null || mentees.isEmpty()) { %>
            <div class="alert alert-error">
                No mentees assigned to you yet. You cannot schedule a session without an assigned mentee.
            </div>
        <% } %>

        <form method="post" action="<%= ctx %>/app/sessions/create-session">
            <input type="hidden" name="mentorId" value="<%= mentorId != null ? mentorId : "" %>">

            <div class="field">
                <label for="menteeId">Mentee</label>
                <select id="menteeId" name="menteeId" required>
                    <option value="">-- select mentee --</option>
                    <% if (mentees != null) {
                           for (Mentee m : mentees) { %>
                        <option value="<%= m.getId() %>" <%= (menteeId != null && menteeId.equals(String.valueOf(m.getId()))) ? "selected" : "" %>><%= m.getUsername() %> (<%= m.getId() %>)</option>
                    <%   }
                       } %>
                </select>
            </div>

            <div class="field row">
                <div class="col">
                    <label for="scheduledDate">Date & Time</label>
                    <input type="datetime-local" id="scheduledDate" name="scheduledDate" required />
                </div>
                <div class="col">
                    <label for="duration">Duration (minutes)</label>
                    <input type="text" id="duration" name="duration" placeholder="60" required />
                </div>
            </div>

            <div class="field">
                <label for="topic">Topic</label>
                <input type="text" id="topic" name="topic" placeholder="Session topic or brief description" />
            </div>

            <div class="actions">
                <a class="btn btn-outline" href="<%= ctx %>/app/sessions/upcoming">Cancel</a>
                <button type="submit" class="btn btn-primary">Schedule session</button>
            </div>
        </form>
    </div>
</body>
</html>

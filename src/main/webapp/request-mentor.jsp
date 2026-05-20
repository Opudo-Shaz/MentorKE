<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="app.model.Mentor" %>
<%
    String ctx = request.getContextPath();
    Mentor mentor = (Mentor) request.getAttribute("mentor");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String specialization = mentor != null ? mentor.getSpecialization() : (String) request.getAttribute("specialization");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Request Mentorship - MentorKE</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background: #f6f8fb; color: #1e293b; }
        .wrap { max-width: 860px; margin: 0 auto; padding: 28px 20px 48px; }
        .card { background: #fff; border: 1px solid #e2e8f0; border-radius: 14px; box-shadow: 0 8px 28px rgba(15, 23, 42, 0.06); padding: 24px; }
        .message { margin-bottom: 16px; padding: 12px 14px; border-radius: 10px; background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }
        .meta { color: #475569; line-height: 1.7; margin: 14px 0 18px; }
        .btn { background: #0d47a1; color: #fff; border: 0; border-radius: 10px; padding: 11px 14px; text-decoration: none; cursor: pointer; display: inline-block; }
        .secondary { background: #e2e8f0; color: #0f172a; }
        .actions { display: flex; gap: 10px; flex-wrap: wrap; margin-top: 16px; }
    </style>
</head>
<body>
<div class="wrap">
    <div style="margin-bottom: 18px;"><a href="<%= ctx %>/app/mentee-sessions/browse">&larr; Back to mentors</a></div>

    <div class="card">
        <h1>Request Mentorship</h1>
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="message"><%= errorMessage %></div>
        <% } %>

        <% if (mentor != null) { %>
        <div class="meta">
            <div><strong>Mentor:</strong> <%= mentor.getUsername() %></div>
            <div><strong>Specialization:</strong> <%= mentor.getSpecialization() %></div>
            <div><strong>Expertise:</strong> <%= mentor.getExpertise() != null ? mentor.getExpertise() : "-" %></div>
            <div><strong>Experience:</strong> <%= mentor.getYearsOfExperience() != null ? mentor.getYearsOfExperience() : 0 %> years</div>
            <div><strong>Status:</strong> <%= mentor.getStatus() %></div>
        </div>

        <form method="post" action="<%= ctx %>/app/mentee-sessions/request-mentor">
            <input type="hidden" name="mentorId" value="<%= mentor.getId() %>">
            <input type="hidden" name="specialization" value="<%= specialization != null ? specialization : "" %>">
            <div class="actions">
                <button class="btn" type="submit">Send request</button>
                <a class="btn secondary" href="<%= ctx %>/app/mentee-sessions/browse">Cancel</a>
            </div>
        </form>
        <% } else { %>
        <p>No mentor was selected.</p>
        <% } %>
    </div>
</div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="app.model.Mentor" %>
<%
    String ctx = request.getContextPath();
    Mentor mentor = (Mentor) request.getAttribute("mentor");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mentor Profile - MentorKE</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background: #f6f8fb; color: #1e293b; }
        .wrap { max-width: 860px; margin: 0 auto; padding: 28px 20px 48px; }
        .card { background: #fff; border: 1px solid #e2e8f0; border-radius: 14px; box-shadow: 0 8px 28px rgba(15, 23, 42, 0.06); padding: 24px; }
        .message { margin-bottom: 16px; padding: 12px 14px; border-radius: 10px; background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }
        .meta { color: #475569; line-height: 1.8; margin: 14px 0 18px; }
        .btn { background: #0d47a1; color: #fff; border: 0; border-radius: 10px; padding: 11px 14px; text-decoration: none; cursor: pointer; display: inline-block; }
        .secondary { background: #e2e8f0; color: #0f172a; }
        .actions { display: flex; gap: 10px; flex-wrap: wrap; margin-top: 16px; }
    </style>
</head>
<body>
<div class="wrap">
    <div style="margin-bottom: 18px;"><a href="<%= ctx %>/app/mentee-sessions/browse">&larr; Back to mentors</a></div>

    <div class="card">
        <h1>Mentor Profile</h1>
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="message"><%= errorMessage %></div>
        <% } %>

        <% if (mentor != null) { %>
        <div class="meta">
            <div><strong>Username:</strong> <%= mentor.getUsername() %></div>
            <div><strong>Specialization:</strong> <%= mentor.getSpecialization() %></div>
            <div><strong>Expertise:</strong> <%= mentor.getExpertise() != null ? mentor.getExpertise() : "-" %></div>
            <div><strong>Years of Experience:</strong> <%= mentor.getYearsOfExperience() != null ? mentor.getYearsOfExperience() : 0 %></div>
            <div><strong>Bio:</strong> <%= mentor.getBio() != null ? mentor.getBio() : "-" %></div>
            <div><strong>Qualifications:</strong> <%= mentor.getQualifications() != null ? mentor.getQualifications() : "-" %></div>
            <div><strong>Status:</strong> <%= mentor.getStatus() %></div>
        </div>
        <div class="actions">
            <a class="btn" href="<%= ctx %>/app/mentee-sessions/request?mentorId=<%= mentor.getId() %>">Request mentorship</a>
            <a class="btn secondary" href="<%= ctx %>/app/mentee-sessions/browse">Browse more mentors</a>
        </div>
        <% } else { %>
        <p>Mentor not found.</p>
        <% } %>
    </div>
</div>
</body>
</html>

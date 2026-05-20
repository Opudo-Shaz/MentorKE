<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="app.model.Mentor" %>
<%
    String ctx = request.getContextPath();
    String selectedSpecialization = (String) request.getAttribute("selectedSpecialization");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
    List<Mentor> mentors = (List<Mentor>) request.getAttribute("mentors");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Browse Mentors - MentorKE</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background: #f6f8fb; color: #1e293b; }
        .wrap { max-width: 1120px; margin: 0 auto; padding: 28px 20px 48px; }
        .top { display: flex; justify-content: space-between; align-items: center; gap: 16px; margin-bottom: 20px; }
        .top a { color: #0d47a1; text-decoration: none; }
        .card { background: #fff; border: 1px solid #e2e8f0; border-radius: 14px; box-shadow: 0 8px 28px rgba(15, 23, 42, 0.06); padding: 20px; }
        .message { margin-bottom: 16px; padding: 12px 14px; border-radius: 10px; }
        .success { background: #ecfdf5; color: #166534; border: 1px solid #bbf7d0; }
        .error { background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }
        .filters { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 18px; }
        .filters input { padding: 11px 12px; border: 1px solid #cbd5e1; border-radius: 10px; min-width: 260px; }
        .filters button, .btn { background: #0d47a1; color: #fff; border: 0; border-radius: 10px; padding: 11px 14px; text-decoration: none; cursor: pointer; display: inline-block; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 16px; }
        .mentor { padding: 18px; border: 1px solid #e2e8f0; border-radius: 14px; background: #fff; }
        .mentor h3 { margin: 0 0 8px; }
        .meta { color: #475569; font-size: 14px; line-height: 1.5; margin-bottom: 12px; }
        .actions { display: flex; gap: 10px; flex-wrap: wrap; }
        .secondary { background: #e2e8f0; color: #0f172a; }
        .empty { padding: 40px 18px; text-align: center; color: #64748b; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="top">
        <div>
            <h1>Browse Mentors</h1>
            <p>Showing mentors matched to your specialization when available.</p>
        </div>
        <a href="<%= ctx %>/app/mentee-dashboard/">Back to dashboard</a>
    </div>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="message success"><%= successMessage %></div>
    <% } %>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="message error"><%= errorMessage %></div>
    <% } %>

    <div class="card">
        <form class="filters" method="get" action="<%= ctx %>/app/mentee-sessions/browse">
            <input type="text" name="specialization" value="<%= selectedSpecialization != null ? selectedSpecialization : "" %>" placeholder="Filter by specialization">
            <button type="submit">Filter mentors</button>
            <a class="btn secondary" href="<%= ctx %>/app/mentee-sessions/browse">Show all</a>
        </form>

        <% if (mentors != null && !mentors.isEmpty()) { %>
        <div class="grid">
            <% for (Mentor mentor : mentors) { %>
            <div class="mentor">
                <h3><%= mentor.getUsername() %></h3>
                <div class="meta">
                    <div><strong>Specialization:</strong> <%= mentor.getSpecialization() %></div>
                    <div><strong>Experience:</strong> <%= mentor.getYearsOfExperience() != null ? mentor.getYearsOfExperience() : 0 %> years</div>
                    <div><strong>Status:</strong> <%= mentor.getStatus() %></div>
                </div>
                <div class="actions">
                    <a class="btn" href="<%= ctx %>/app/mentee-sessions/request?mentorId=<%= mentor.getId() %>&specialization=<%= java.net.URLEncoder.encode(mentor.getSpecialization() != null ? mentor.getSpecialization() : "", java.nio.charset.StandardCharsets.UTF_8) %>">Request mentorship</a>
                    <a class="btn secondary" href="<%= ctx %>/app/mentee-sessions/view-mentor?mentorId=<%= mentor.getId() %>">View profile</a>
                </div>
            </div>
            <% } %>
        </div>
        <% } else { %>
        <div class="empty">
            <h3>No mentors found</h3>
            <p>Try a different specialization or check back later.</p>
        </div>
        <% } %>
    </div>
</div>
</body>
</html>

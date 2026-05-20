<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="app.model.MatchRequest" %>
<%@ page import="app.model.Mentee" %>
<%
    String ctx = request.getContextPath();
    List<MatchRequest> requests = (List<MatchRequest>) request.getAttribute("requests");
    MatchRequest approvedMatch = (MatchRequest) request.getAttribute("approvedMatch");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Requests - MentorKE</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background: #f6f8fb; color: #1e293b; }
        .wrap { max-width: 1100px; margin: 0 auto; padding: 28px 20px 48px; }
        .card { background: #fff; border: 1px solid #e2e8f0; border-radius: 14px; box-shadow: 0 8px 28px rgba(15, 23, 42, 0.06); padding: 20px; margin-bottom: 18px; }
        .message { margin-bottom: 16px; padding: 12px 14px; border-radius: 10px; }
        .success { background: #ecfdf5; color: #166534; border: 1px solid #bbf7d0; }
        .error { background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 10px; border-bottom: 1px solid #e2e8f0; text-align: left; vertical-align: top; }
        th { background: #f8fafc; font-size: 14px; }
        .pill { display: inline-block; padding: 5px 10px; border-radius: 999px; font-size: 12px; font-weight: 700; }
        .pending { background: #fef3c7; color: #92400e; }
        .approved { background: #dcfce7; color: #166534; }
        .rejected { background: #fee2e2; color: #991b1b; }
        .btn { background: #0d47a1; color: #fff; border: 0; border-radius: 10px; padding: 10px 14px; text-decoration: none; cursor: pointer; display: inline-block; }
        .secondary { background: #e2e8f0; color: #0f172a; }
        .danger { background: #fee2e2; color: #991b1b; }
        .actions { display: flex; gap: 8px; flex-wrap: wrap; }
        .empty { padding: 30px 18px; color: #64748b; text-align: center; }
    </style>
</head>
<body>
<div class="wrap">
    <div style="margin-bottom: 18px;"><a href="<%= ctx %>/app/mentee-dashboard/">&larr; Back to dashboard</a></div>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="message success"><%= successMessage %></div>
    <% } %>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="message error"><%= errorMessage %></div>
    <% } %>

    <% if (approvedMatch != null) { %>
    <div class="card">
        <h2>Your assigned mentor</h2>
        <p><strong>Request ID:</strong> <%= approvedMatch.getId() %></p>
        <p><strong>Mentor ID:</strong> <%= approvedMatch.getMentorId() %></p>
        <p><strong>Specialization:</strong> <%= approvedMatch.getRequestedSpecialization() %></p>
        <span class="pill approved">Approved</span>
    </div>
    <% } %>

    <div class="card">
        <h2>All Requests</h2>
        <% if (requests != null && !requests.isEmpty()) { %>
        <table>
            <thead>
            <tr>
                <th>Request ID</th>
                <th>Mentor ID</th>
                <th>Specialization</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <% for (MatchRequest matchRequest : requests) { %>
            <tr>
                <td><%= matchRequest.getId() %></td>
                <td><%= matchRequest.getMentorId() != null ? matchRequest.getMentorId() : "-" %></td>
                <td><%= matchRequest.getRequestedSpecialization() != null ? matchRequest.getRequestedSpecialization() : "-" %></td>
                <td>
                    <span class="pill <%= "APPROVED".equalsIgnoreCase(matchRequest.getStatus()) ? "approved" : ("REJECTED".equalsIgnoreCase(matchRequest.getStatus()) ? "rejected" : "pending") %>"><%= matchRequest.getStatus() %></span>
                </td>
                <td>
                    <div class="actions">
                        <% if ("PENDING".equalsIgnoreCase(matchRequest.getStatus())) { %>
                        <form method="post" action="<%= ctx %>/app/mentee-sessions/cancel-request">
                            <input type="hidden" name="requestId" value="<%= matchRequest.getId() %>">
                            <button class="btn danger" type="submit">Cancel</button>
                        </form>
                        <% } else { %>
                        <span class="secondary">No actions</span>
                        <% } %>
                    </div>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
        <% } else { %>
        <div class="empty">
            <h3>No requests yet</h3>
            <p>Browse mentors and send your first mentorship request.</p>
            <p><a class="btn" href="<%= ctx %>/app/mentee-sessions/browse">Browse mentors</a></p>
        </div>
        <% } %>
    </div>
</div>
</body>
</html>

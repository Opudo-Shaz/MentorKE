<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.util.List" %>
<%@ page import="app.model.MatchRequest" %>
<%
    HttpSession currentSession = request.getSession(false);
    String username = currentSession != null ? (String) currentSession.getAttribute("username") : null;
    if (username == null) username = "Mentor";

    List<MatchRequest> pendingRequests = (List<MatchRequest>) request.getAttribute("pendingRequests");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pending Mentorship Requests — MentorKE</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        :root {
            --bg: #f5f7fb;
            --card: #ffffff;
            --text: #1f2937;
            --muted: #6b7280;
            --line: #e5e7eb;
            --blue: #1565c0;
            --blue-50: #e8f1ff;
            --green: #15803d;
            --green-50: #ecfdf5;
            --red: #b91c1c;
            --red-50: #fef2f2;
            --shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
            --radius: 16px;
        }
        body {
            font-family: 'DM Sans', sans-serif;
            background: linear-gradient(180deg, #f8fbff 0%, var(--bg) 100%);
            color: var(--text);
            min-height: 100vh;
        }
        .page {
            max-width: 1180px;
            margin: 0 auto;
            padding: 28px 22px 44px;
        }
        .hero {
            background: var(--card);
            border: 1px solid var(--line);
            border-radius: 24px;
            box-shadow: var(--shadow);
            padding: 22px 24px;
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 16px;
            margin-bottom: 18px;
        }
        .hero h1 { font-size: 28px; line-height: 1.1; margin-bottom: 8px; }
        .hero p { color: var(--muted); font-size: 14px; max-width: 720px; }
        .hero-actions { display: flex; gap: 10px; flex-wrap: wrap; }
        .btn {
            display: inline-flex; align-items: center; justify-content: center;
            border-radius: 999px; padding: 10px 16px; font-weight: 600;
            text-decoration: none; border: 1px solid transparent; cursor: pointer;
            font-size: 14px; transition: transform 0.15s ease, background 0.15s ease, border-color 0.15s ease;
        }
        .btn:hover { transform: translateY(-1px); }
        .btn-primary { background: var(--blue); color: white; }
        .btn-outline { background: white; color: var(--blue); border-color: #cfe0fb; }
        .alert {
            border-radius: 14px; padding: 14px 16px; margin-bottom: 16px;
            font-size: 14px; border: 1px solid transparent;
        }
        .alert-success { background: var(--green-50); color: var(--green); border-color: #bbf7d0; }
        .alert-error { background: var(--red-50); color: var(--red); border-color: #fecaca; }
        .grid {
            display: grid;
            grid-template-columns: repeat(12, 1fr);
            gap: 16px;
        }
        .table-card {
            grid-column: 1 / -1;
            background: var(--card);
            border: 1px solid var(--line);
            border-radius: 20px;
            box-shadow: var(--shadow);
            overflow: hidden;
        }
        .table-head {
            padding: 18px 20px;
            border-bottom: 1px solid var(--line);
            display: flex;
            justify-content: space-between;
            gap: 12px;
            align-items: center;
        }
        .table-head h2 { font-size: 18px; margin-bottom: 4px; }
        .table-head span { color: var(--muted); font-size: 13px; }
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 16px 20px; border-bottom: 1px solid var(--line); text-align: left; vertical-align: top; }
        th { font-size: 12px; text-transform: uppercase; letter-spacing: .05em; color: var(--muted); background: #fcfdff; }
        td { font-size: 14px; }
        .pill {
            display: inline-flex; align-items: center; padding: 6px 10px; border-radius: 999px;
            font-size: 12px; font-weight: 700; letter-spacing: .02em;
        }
        .pill-pending { background: #fff7ed; color: #c2410c; }
        .pill-approved { background: var(--green-50); color: var(--green); }
        .pill-rejected { background: var(--red-50); color: var(--red); }
        .actions { display: flex; gap: 8px; flex-wrap: wrap; }
        .action-form { display: inline; }
        .btn-small {
            padding: 9px 13px; border-radius: 12px; font-size: 13px; font-weight: 700;
            border: 1px solid transparent; cursor: pointer;
        }
        .btn-approve { background: var(--green); color: white; }
        .btn-reject { background: white; color: var(--red); border-color: #fecaca; }
        .empty {
            padding: 54px 24px; text-align: center; color: var(--muted);
        }
        .empty h3 { color: var(--text); margin-bottom: 8px; }
        @media (max-width: 760px) {
            .hero { flex-direction: column; }
            .hero h1 { font-size: 24px; }
            th, td { padding: 14px 16px; }
        }
    </style>
</head>
<body>
<div class="page">
    <div class="hero">
        <div>
            <h1>Pending Mentorship Requests</h1>
            <p>Review new mentee requests, then approve to accept the match or reject to decline it. Requests default to <strong>PENDING</strong> until you choose.</p>
        </div>
        <div class="hero-actions">
            <a class="btn btn-outline" href="/MentorKE/mentor-dashboard.jsp">Back to Dashboard</a>
            <a class="btn btn-primary" href="/MentorKE/mentor-dashboard.jsp">View My Mentees</a>
        </div>
    </div>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="alert alert-success"><%= successMessage %></div>
    <% } %>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="alert alert-error"><%= errorMessage %></div>
    <% } %>

    <div class="table-card">
        <div class="table-head">
            <div>
                <h2>Requests awaiting your response</h2>
                <span><%= pendingRequests != null ? pendingRequests.size() : 0 %> request(s) found</span>
            </div>
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Request ID</th>
                    <th>Mentee ID</th>
                    <th>Requested Specialization</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <% if (pendingRequests != null && !pendingRequests.isEmpty()) {
                       for (MatchRequest req : pendingRequests) { %>
                <tr>
                    <td><%= req.getId() %></td>
                    <td><%= req.getMenteeId() %></td>
                    <td><%= req.getRequestedSpecialization() != null ? req.getRequestedSpecialization() : "-" %></td>
                    <td>
                        <span class="pill pill-pending"><%= req.getStatus() %></span>
                    </td>
                    <td>
                        <div class="actions">
                            <form class="action-form" action="/MentorKE/app/mentor-requests/approve" method="post">
                                <input type="hidden" name="action" value="approve">
                                <input type="hidden" name="requestId" value="<%= req.getId() %>">
                                <button type="submit" class="btn-small btn-approve">Approve</button>
                            </form>
                            <form class="action-form" action="/MentorKE/app/mentor-requests/reject" method="post">
                                <input type="hidden" name="action" value="reject">
                                <input type="hidden" name="requestId" value="<%= req.getId() %>">
                                <button type="submit" class="btn-small btn-reject">Reject</button>
                            </form>
                        </div>
                    </td>
                </tr>
                <% } } else { %>
                <tr>
                    <td colspan="5">
                        <div class="empty">
                            <h3>No pending requests</h3>
                            <p>You don’t have any mentorship requests waiting for approval right now.</p>
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
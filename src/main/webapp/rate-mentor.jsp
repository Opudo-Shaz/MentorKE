<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="app.model.Session" %>
<%
    String ctx = request.getContextPath();
    Session ratedSession = (Session) request.getAttribute("session");
    String mentorId = (String) request.getAttribute("mentorId");
    String errorMessage = (String) request.getAttribute("errorMessage");

    String sessionId = ratedSession != null && ratedSession.getId() != null ? String.valueOf(ratedSession.getId()) : "";
    String mentorDisplay = mentorId != null ? mentorId : "";
    String topic = ratedSession != null && ratedSession.getTopic() != null ? ratedSession.getTopic() : "Mentorship Session";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rate Mentor - MentorKE</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
        :root {
            --blue-800: #0d47a1;
            --blue-700: #1565c0;
            --blue-100: #bbdefb;
            --blue-50: #e3f2fd;
            --white: #ffffff;
            --gray-50: #f8fafc;
            --gray-200: #e2e8f0;
            --gray-400: #94a3b8;
            --gray-700: #334155;
            --gray-800: #1e293b;
            --red-50: #fef2f2;
            --red-700: #b91c1c;
            --radius-md: 10px;
            --radius-lg: 14px;
            --shadow: 0 8px 30px rgba(15, 23, 42, 0.08);
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: 'DM Sans', sans-serif;
            background: linear-gradient(150deg, #f0f7ff 0%, #f8fafc 45%, #eef2ff 100%);
            color: var(--gray-800);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .card {
            width: min(640px, 100%);
            background: var(--white);
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-lg);
            box-shadow: var(--shadow);
            overflow: hidden;
        }

        .header {
            padding: 20px 24px;
            border-bottom: 1px solid var(--gray-200);
            background: var(--blue-50);
        }

        .header h1 {
            margin: 0;
            font-size: 20px;
            font-weight: 700;
            color: var(--blue-800);
        }

        .header p {
            margin: 6px 0 0;
            font-size: 14px;
            color: var(--gray-700);
        }

        .body {
            padding: 24px;
            display: grid;
            gap: 16px;
        }

        .meta {
            background: var(--gray-50);
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-md);
            padding: 12px;
            font-size: 14px;
            color: var(--gray-700);
        }

        .alert {
            background: var(--red-50);
            color: var(--red-700);
            border: 1px solid #fecaca;
            border-radius: var(--radius-md);
            padding: 10px 12px;
            font-size: 14px;
        }

        label {
            display: block;
            font-size: 13px;
            font-weight: 600;
            color: var(--gray-700);
            margin-bottom: 6px;
        }

        select,
        textarea {
            width: 100%;
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-md);
            padding: 10px 12px;
            font: inherit;
            color: var(--gray-800);
        }

        textarea {
            min-height: 120px;
            resize: vertical;
        }

        .actions {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .btn {
            border: none;
            border-radius: var(--radius-md);
            padding: 10px 14px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            font-family: inherit;
        }

        .btn-primary {
            background: var(--blue-800);
            color: var(--white);
        }

        .btn-primary:hover {
            background: var(--blue-700);
        }

        .btn-secondary {
            border: 1px solid var(--gray-200);
            background: var(--white);
            color: var(--gray-700);
        }

        .hint {
            color: var(--gray-400);
            font-size: 12px;
            margin-top: -8px;
        }
    </style>
</head>
<body>
<div class="card">
    <div class="header">
        <h1>Rate Your Mentor</h1>
        <p>Your feedback helps surface top-rated mentors for future mentee searches.</p>
    </div>

    <div class="body">
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="alert"><%= errorMessage %></div>
        <% } %>

        <div class="meta">
            <strong>Session:</strong> <%= topic %><br>
            <strong>Mentor ID:</strong> <%= mentorDisplay %>
        </div>

        <form method="post" action="<%= ctx %>/app/sessions/submit-rating">
            <input type="hidden" name="sessionId" value="<%= sessionId %>">
            <input type="hidden" name="mentorId" value="<%= mentorDisplay %>">

            <label for="rating">Rating</label>
            <select id="rating" name="rating" required>
                <option value="">Select rating</option>
                <option value="5">5 - Excellent</option>
                <option value="4">4 - Very Good</option>
                <option value="3">3 - Good</option>
                <option value="2">2 - Fair</option>
                <option value="1">1 - Poor</option>
            </select>

            <label for="feedback" style="margin-top: 12px;">Feedback (optional)</label>
            <textarea id="feedback" name="feedback" maxlength="1000" placeholder="What went well? What can improve?"></textarea>
            <div class="hint">Max 1000 characters</div>

            <div class="actions" style="margin-top:14px;">
                <button class="btn btn-primary" type="submit">Submit rating</button>
                <a class="btn btn-secondary" href="<%= ctx %>/app/sessions/completed">Back to completed sessions</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>

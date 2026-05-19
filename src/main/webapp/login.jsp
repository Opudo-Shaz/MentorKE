<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>

<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String username = request.getParameter("username");
    if (username == null) username = "";
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login — MentorKE</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --blue-900: #0a2e6e;
            --blue-800: #0d47a1;
            --blue-700: #1565c0;
            --blue-600: #1976d2;
            --blue-100: #bbdefb;
            --blue-50:  #e3f2fd;
            --blue-25:  #f0f7ff;
            --white:    #ffffff;
            --gray-50:  #f8fafc;
            --gray-100: #f1f5f9;
            --gray-200: #e2e8f0;
            --gray-400: #94a3b8;
            --gray-600: #475569;
            --gray-800: #1e293b;
            --red-50:   #fef2f2;
            --red-200:  #fecaca;
            --red-700:  #b91c1c;
            --green-50: #f0fdf4;
            --green-700:#15803d;
            --radius-md: 8px;
            --radius-lg: 12px;
        }

        body {
            font-family: 'DM Sans', sans-serif;
            background: var(--gray-50);
            color: var(--gray-800);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        /* ── NAVBAR ── */
        .navbar {
            background: var(--white);
            border-bottom: 1px solid var(--gray-200);
            padding: 0 60px; height: 64px;
            display: flex; align-items: center; justify-content: space-between;
        }
        .nav-brand { display: flex; align-items: center; gap: 10px; text-decoration: none; }
        .nav-brand-icon {
            width: 34px; height: 34px; background: var(--blue-800);
            border-radius: var(--radius-md);
            display: flex; align-items: center; justify-content: center;
        }
        .nav-brand-icon svg { width: 18px; height: 18px; }
        .nav-brand-name { font-size: 18px; font-weight: 700; color: var(--blue-800); letter-spacing: -0.3px; }
        .nav-links { display: flex; align-items: center; gap: 6px; }
        .nav-links a {
            padding: 7px 14px; border-radius: 7px;
            font-size: 14px; font-weight: 500; color: var(--gray-600);
            text-decoration: none; transition: background 0.15s, color 0.15s;
        }
        .nav-links a:hover { background: var(--gray-100); color: var(--gray-800); }
        .nav-links .btn-primary {
            background: var(--blue-800); color: var(--white); padding: 7px 18px;
        }
        .nav-links .btn-primary:hover { background: var(--blue-700); color: var(--white); }

        /* ── SPLIT LAYOUT ── */
        .page {
            flex: 1;
            display: grid;
            grid-template-columns: 1fr 1fr;
            min-height: calc(100vh - 64px);
        }

        /* ── LEFT PANEL ── */
        .left-panel {
            background: linear-gradient(160deg, var(--blue-900) 0%, var(--blue-800) 55%, var(--blue-700) 100%);
            padding: 60px 50px;
            display: flex; flex-direction: column; justify-content: center;
            position: relative; overflow: hidden;
        }
        .left-panel::before {
            content: '';
            position: absolute; inset: 0;
            background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.03'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
        }
        .left-content { position: relative; z-index: 1; }

        .left-tag {
            display: inline-flex; align-items: center; gap: 7px;
            background: rgba(255,255,255,0.12); border: 1px solid rgba(255,255,255,0.2);
            border-radius: 20px; padding: 5px 14px 5px 10px;
            font-size: 13px; font-weight: 500; color: rgba(255,255,255,0.9);
            margin-bottom: 28px;
        }
        .left-tag-dot { width: 7px; height: 7px; border-radius: 50%; background: #4ade80; }

        .left-panel h2 {
            font-size: 34px; font-weight: 700; color: var(--white);
            line-height: 1.2; letter-spacing: -0.5px; margin-bottom: 16px;
        }
        .left-panel h2 span { color: var(--blue-100); }
        .left-panel > .left-content > p {
            font-size: 15px; color: rgba(255,255,255,0.65);
            line-height: 1.8; margin-bottom: 40px; max-width: 380px;
        }

        /* role cards on left */
        .role-cards { display: flex; flex-direction: column; gap: 12px; }
        .role-card {
            display: flex; align-items: center; gap: 14px;
            background: rgba(255,255,255,0.08);
            border: 1px solid rgba(255,255,255,0.12);
            border-radius: var(--radius-lg); padding: 14px 16px;
        }
        .role-card-icon {
            width: 38px; height: 38px; border-radius: 9px;
            background: rgba(255,255,255,0.15);
            display: flex; align-items: center; justify-content: center; flex-shrink: 0;
        }
        .role-card-icon svg { width: 18px; height: 18px; }
        .role-card-title { font-size: 14px; font-weight: 600; color: var(--white); }
        .role-card-sub   { font-size: 12px; color: rgba(255,255,255,0.5); margin-top: 1px; }

        /* ── RIGHT PANEL ── */
        .right-panel {
            display: flex; align-items: center; justify-content: center;
            padding: 40px 50px; background: var(--white);
        }
        .form-wrap { width: 100%; max-width: 380px; }

        .form-header { margin-bottom: 28px; }
        .form-header h1 {
            font-size: 26px; font-weight: 700; color: var(--gray-800);
            letter-spacing: -0.4px; margin-bottom: 6px;
        }
        .form-header p { font-size: 14px; color: var(--gray-400); }

        /* ── ERROR ALERT ── */
        .alert-error {
            display: flex; align-items: center; gap: 10px;
            padding: 12px 14px;
            background: var(--red-50); color: var(--red-700);
            border: 1px solid var(--red-200);
            border-radius: var(--radius-md);
            font-size: 13px; margin-bottom: 20px;
        }
        .alert-error svg { width: 16px; height: 16px; flex-shrink: 0; }

        /* ── FORM FIELDS ── */
        .form-group { display: flex; flex-direction: column; gap: 6px; margin-bottom: 14px; }
        .form-group label { font-size: 13px; font-weight: 500; color: var(--gray-600); }

        .input-wrap { position: relative; }
        .input-icon {
            position: absolute; left: 12px; top: 50%; transform: translateY(-50%);
            width: 16px; height: 16px; color: var(--gray-400); pointer-events: none;
        }
        .input-icon svg { width: 16px; height: 16px; }

        .form-group input,
        .form-group select {
            width: 100%; padding: 10px 12px 10px 38px;
            border: 1px solid var(--gray-200); border-radius: var(--radius-md);
            font-size: 14px; font-family: 'DM Sans', sans-serif;
            color: var(--gray-800); background: var(--white);
            transition: border-color 0.15s, box-shadow 0.15s;
        }
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: var(--blue-600);
            box-shadow: 0 0 0 3px rgba(25,118,210,0.12);
        }
        .form-group input::placeholder { color: var(--gray-400); }

        /* password toggle */
        .pw-toggle {
            position: absolute; right: 12px; top: 50%; transform: translateY(-50%);
            background: none; border: none; cursor: pointer;
            color: var(--gray-400); padding: 0; display: flex; align-items: center;
        }
        .pw-toggle:hover { color: var(--gray-600); }
        .pw-toggle svg { width: 16px; height: 16px; }

        /* role pill selector */
        .role-pills {
            display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px;
            margin-bottom: 20px;
        }
        .role-pill-radio { display: none; }
        .role-pill-label {
            display: flex; align-items: center; justify-content: center; gap: 6px;
            padding: 9px 8px;
            border: 1.5px solid var(--gray-200); border-radius: var(--radius-md);
            font-size: 13px; font-weight: 500; color: var(--gray-600);
            cursor: pointer; transition: border-color 0.15s, background 0.15s, color 0.15s;
        }
        .role-pill-label svg { width: 15px; height: 15px; }
        .role-pill-label:hover { border-color: var(--blue-200); background: var(--blue-25); color: var(--blue-800); }
        .role-pill-radio:checked + .role-pill-label {
            border-color: var(--blue-800); background: var(--blue-25); color: var(--blue-800);
        }

        /* submit */
        .btn-submit {
            width: 100%; padding: 11px;
            background: var(--blue-800); color: var(--white);
            border: none; border-radius: var(--radius-md);
            font-size: 15px; font-weight: 600; font-family: 'DM Sans', sans-serif;
            cursor: pointer; margin-top: 4px;
            display: flex; align-items: center; justify-content: center; gap: 8px;
            transition: background 0.15s, transform 0.15s;
        }
        .btn-submit:hover { background: var(--blue-700); transform: translateY(-1px); }
        .btn-submit svg { width: 16px; height: 16px; }

        /* footer links */
        .divider {
            display: flex; align-items: center; gap: 10px;
            margin: 18px 0; color: var(--gray-400); font-size: 12px;
        }
        .divider::before, .divider::after { content: ''; flex: 1; height: 1px; background: var(--gray-200); }

        .form-footer { text-align: center; font-size: 13px; color: var(--gray-400); }
        .form-footer a { color: var(--blue-800); font-weight: 500; text-decoration: none; }
        .form-footer a:hover { text-decoration: underline; }

        /* debug */
        .debug-box {
            margin-top: 20px; padding: 10px 14px;
            background: var(--gray-50); border: 1px solid var(--gray-200);
            border-radius: var(--radius-md); font-size: 11px; color: var(--gray-400);
            line-height: 1.7;
        }

        /* footer */
        footer {
            background: var(--blue-900); color: rgba(255,255,255,0.4);
            text-align: center; font-size: 13px; padding: 16px;
        }
    </style>
</head>
<body>

    <!-- ══════════ NAVBAR ══════════ -->
    <nav class="navbar">
        <a href="<%= request.getContextPath() %>" class="nav-brand">
            <div class="nav-brand-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
            </div>
            <span class="nav-brand-name">MentorKE</span>
        </a>
        <div class="nav-links">
            <a href="<%= request.getContextPath() %>">Home</a>
            <a href="<%= request.getContextPath() %>/app/about/">About</a>
            <a href="<%= request.getContextPath() %>/register" class="btn-primary">Register</a>
        </div>
    </nav>

    <!-- ══════════ PAGE ══════════ -->
    <div class="page">

        <!-- LEFT PANEL -->
        <div class="left-panel">
            <div class="left-content">
                <div class="left-tag">
                    <span class="left-tag-dot"></span>
                    Welcome back
                </div>
                <h2>Sign in to your<br><span>MentorKE</span><br>account</h2>
                <p>Access your dashboard, connect with your mentor or mentees, and continue your growth journey.</p>

                <div class="role-cards">
                    <div class="role-card">
                        <div class="role-card-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/><polyline points="16 11 18 13 22 9"/></svg>
                        </div>
                        <div>
                            <div class="role-card-title">Mentees</div>
                            <div class="role-card-sub">View your mentor, track progress and goals</div>
                        </div>
                    </div>
                    <div class="role-card">
                        <div class="role-card-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="16"/><line x1="10" y1="14" x2="14" y2="14"/></svg>
                        </div>
                        <div>
                            <div class="role-card-title">Mentors</div>
                            <div class="role-card-sub">Manage your mentees and profile</div>
                        </div>
                    </div>
                    <div class="role-card">
                        <div class="role-card-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                        </div>
                        <div>
                            <div class="role-card-title">Admins</div>
                            <div class="role-card-sub">Manage users, mentors and mentees</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- RIGHT PANEL -->
        <div class="right-panel">
            <div class="form-wrap">

                <div class="form-header">
                    <h1>Welcome back</h1>
                    <p>Sign in to continue to your dashboard.</p>
                </div>

                <!-- Error message -->
                <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                <div class="alert-error">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                    <%= errorMessage %>
                </div>
                <% } %>

                <!-- Role pill selector -->
                <div style="margin-bottom:6px;">
                    <div style="font-size:12px; font-weight:500; color:var(--gray-600); margin-bottom:10px;">Sign in as</div>
                    <div class="role-pills">
                        <div>
                            <input type="radio" name="role_ui" id="pill_mentee" value="mentee" class="role-pill-radio" checked>
                            <label for="pill_mentee" class="role-pill-label" onclick="setRole('mentee')">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                                Mentee
                            </label>
                        </div>
                        <div>
                            <input type="radio" name="role_ui" id="pill_mentor" value="mentor" class="role-pill-radio">
                            <label for="pill_mentor" class="role-pill-label" onclick="setRole('mentor')">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>
                                Mentor
                            </label>
                        </div>
                        <div>
                            <input type="radio" name="role_ui" id="pill_admin" value="admin" class="role-pill-radio">
                            <label for="pill_admin" class="role-pill-label" onclick="setRole('admin')">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                                Admin
                            </label>
                        </div>
                    </div>
                </div>

                <!-- Login form -->
                <form action="<%= request.getContextPath() %>/app/login/" method="post">

                    <input type="hidden" name="role" id="hiddenRole" value="mentee">

                    <div class="form-group">
                        <label for="username">Username</label>
                        <div class="input-wrap">
                            <span class="input-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                            </span>
                            <input
                                type="text"
                                id="username"
                                name="username"
                                placeholder="Enter your username"
                                value="<%= username %>"
                                required
                                autocomplete="username"
                            >
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="password">Password</label>
                        <div class="input-wrap">
                            <span class="input-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                            </span>
                            <input
                                type="password"
                                id="password"
                                name="password"
                                placeholder="Enter your password"
                                required
                                autocomplete="current-password"
                            >
                            <button type="button" class="pw-toggle" onclick="togglePassword()" aria-label="Show/hide password">
                                <svg id="eyeIcon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                            </button>
                        </div>
                    </div>

                    <button type="submit" class="btn-submit">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/><polyline points="10 17 15 12 10 7"/><line x1="15" y1="12" x2="3" y2="12"/></svg>
                        Sign in
                    </button>

                </form>

                <div class="divider">or</div>

                <div class="form-footer">
                    Don't have an account? <a href="<%= request.getContextPath() %>/register">Create one free</a>
                    &nbsp;·&nbsp;
                    <a href="<%= request.getContextPath() %>">Back to home</a>
                </div>

                <!-- Debug info (dev only — remove in production) -->
                <div class="debug-box">
                    Session ID: <%= session.getId() %><br>
                    Remote IP: <%= request.getRemoteAddr() %>
                </div>

            </div>
        </div>

    </div><!-- /page -->

    <footer>
        &copy; 2026 MentorKE. All rights reserved. Built in Kenya.
    </footer>

    <script>
        function setRole(val) {
            document.getElementById('hiddenRole').value = val;
        }

        function togglePassword() {
            var input = document.getElementById('password');
            var icon  = document.getElementById('eyeIcon');
            if (input.type === 'password') {
                input.type = 'text';
                icon.innerHTML = '<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/>';
            } else {
                input.type = 'password';
                icon.innerHTML = '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>';
            }
        }
    </script>

</body>
</html>
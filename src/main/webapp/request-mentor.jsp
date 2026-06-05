<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="app.model.Mentor" %>
<%
    String ctx = request.getContextPath();
    String username = (String) session.getAttribute("username");
    if (username == null) username = "Mentee";

    Mentor mentor = (Mentor) request.getAttribute("mentor");
    String errorMessage  = (String) request.getAttribute("errorMessage");
    String specialization = mentor != null ? mentor.getSpecialization()
                          : (String) request.getAttribute("specialization");

    String mentorName   = mentor != null && mentor.getUsername()          != null ? mentor.getUsername()          : "Unknown Mentor";
    String mentorSpec   = mentor != null && mentor.getSpecialization()    != null ? mentor.getSpecialization()    : "—";
    String mentorExp2   = mentor != null && mentor.getExpertise()         != null ? mentor.getExpertise()         : null;
    String mentorStatus = mentor != null && mentor.getStatus()            != null ? mentor.getStatus()            : "Active";
    int    mentorYears  = mentor != null && mentor.getYearsOfExperience() != null ? mentor.getYearsOfExperience() : 0;
    boolean isActive    = "Active".equalsIgnoreCase(mentorStatus);
    String initials     = mentorSpec.length() > 0 ? mentorSpec.substring(0, 1).toUpperCase() : "M";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Request Mentorship – MentorKE</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --blue-800: #0d47a1;
            --blue-700: #1565c0;
            --blue-200: #90caf9;
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
            --green-50: #f0fdf4;
            --green-200:#bbf7d0;
            --green-700:#15803d;
            --amber-50: #fffbeb;
            --amber-200:#fde68a;
            --amber-700:#b45309;
            --red-50:   #fef2f2;
            --red-200:  #fecaca;
            --red-700:  #b91c1c;
            --sidebar-w: 230px;
            --radius-md: 8px;
            --radius-lg: 12px;
            --shadow-sm: 0 1px 3px rgba(0,0,0,0.07);
        }

        body {
            font-family: 'DM Sans', sans-serif;
            background: var(--gray-50);
            color: var(--gray-800);
            min-height: 100vh;
            display: flex;
        }

        /* ── SIDEBAR ── */
        .sidebar { width: var(--sidebar-w); background: var(--blue-800); height: 100vh; position: fixed; left: 0; top: 0; display: flex; flex-direction: column; z-index: 50; }
        .sidebar-brand { padding: 22px 18px 18px; border-bottom: 1px solid rgba(255,255,255,0.12); }
        .logo { display: flex; align-items: center; gap: 10px; }
        .logo-icon { width: 34px; height: 34px; background: rgba(255,255,255,0.18); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; }
        .logo-text { font-size: 17px; font-weight: 600; color: var(--white); }
        .logo-sub  { font-size: 11px; color: rgba(255,255,255,0.5); margin-top: 1px; }
        .sidebar-nav { flex: 1; padding: 14px 10px; overflow-y: auto; }
        .nav-section-label { font-size: 10px; font-weight: 600; letter-spacing: 0.08em; color: rgba(255,255,255,0.4); text-transform: uppercase; padding: 12px 8px 6px; }
        .nav-link { display: flex; align-items: center; gap: 10px; padding: 9px 12px; border-radius: var(--radius-md); color: rgba(255,255,255,0.72); font-size: 14px; text-decoration: none; margin-bottom: 2px; transition: background 0.15s, color 0.15s; }
        .nav-link svg { flex-shrink: 0; width: 18px; height: 18px; }
        .nav-link:hover  { background: rgba(255,255,255,0.1);  color: var(--white); }
        .nav-link.active { background: rgba(255,255,255,0.18); color: var(--white); font-weight: 500; }
        .sidebar-footer { padding: 14px 16px; border-top: 1px solid rgba(255,255,255,0.12); }
        .sidebar-user { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
        .user-avatar { width: 34px; height: 34px; border-radius: 50%; background: rgba(255,255,255,0.2); display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 600; color: var(--white); flex-shrink: 0; }
        .user-name { font-size: 13px; font-weight: 500; color: var(--white); }
        .user-role { font-size: 11px; color: rgba(255,255,255,0.5); }
        .btn-logout { display: flex; align-items: center; justify-content: center; gap: 7px; width: 100%; padding: 8px; background: rgba(255,255,255,0.08); border: 1px solid rgba(255,255,255,0.15); border-radius: var(--radius-md); color: rgba(255,255,255,0.75); font-size: 13px; font-family: 'DM Sans', sans-serif; cursor: pointer; text-decoration: none; transition: background 0.15s; }
        .btn-logout:hover { background: rgba(255,255,255,0.16); color: var(--white); }

        /* ── MAIN ── */
        .main { margin-left: var(--sidebar-w); flex: 1; min-height: 100vh; display: flex; flex-direction: column; }

        /* ── TOPBAR ── */
        .topbar { height: 60px; background: var(--white); border-bottom: 1px solid var(--gray-200); display: flex; align-items: center; justify-content: space-between; padding: 0 28px; position: sticky; top: 0; z-index: 40; }
        .topbar h1 { font-size: 17px; font-weight: 600; color: var(--gray-800); }
        .topbar p  { font-size: 12px; color: var(--gray-400); margin-top: 1px; }
        .btn-outline { display: inline-flex; align-items: center; gap: 6px; font-size: 13px; font-weight: 500; color: var(--blue-800); text-decoration: none; padding: 6px 12px; border: 1px solid var(--blue-100); border-radius: var(--radius-md); background: var(--blue-50); transition: background 0.15s; }
        .btn-outline:hover { background: var(--blue-100); }
        .btn-outline svg { width: 15px; height: 15px; }

        /* ── CONTENT ── */
        .content { padding: 24px 28px; flex: 1; }

        /* ── ALERT ── */
        .alert { display: flex; align-items: center; gap: 10px; padding: 12px 16px; border-radius: var(--radius-md); font-size: 14px; margin-bottom: 20px; }
        .alert svg { width: 18px; height: 18px; flex-shrink: 0; }
        .alert-error { background: var(--red-50); color: var(--red-700); border: 1px solid var(--red-200); }

        /* ── TWO-COL LAYOUT ── */
        .request-grid { display: grid; grid-template-columns: 280px 1fr; gap: 20px; align-items: start; }

        /* ── CARD ── */
        .card { background: var(--white); border: 1px solid var(--gray-200); border-radius: var(--radius-lg); overflow: hidden; box-shadow: var(--shadow-sm); }
        .card-header { padding: 14px 20px; border-bottom: 1px solid var(--gray-200); }
        .card-header h2 { font-size: 15px; font-weight: 600; color: var(--gray-800); }
        .card-header p  { font-size: 12px; color: var(--gray-400); margin-top: 1px; }
        .card-body { padding: 20px; }

        /* ── MENTOR IDENTITY ── */
        .mentor-avatar-lg { width: 64px; height: 64px; border-radius: 50%; background: var(--blue-800); display: flex; align-items: center; justify-content: center; font-size: 22px; font-weight: 600; color: var(--white); margin-bottom: 14px; }
        .mentor-name-lg { font-size: 17px; font-weight: 600; color: var(--gray-800); margin-bottom: 3px; }
        .mentor-spec-lg { font-size: 13px; color: var(--gray-400); margin-bottom: 16px; }

        .detail-row { display: flex; align-items: center; gap: 10px; padding: 9px 0; border-bottom: 1px solid var(--gray-100); font-size: 13px; color: var(--gray-600); }
        .detail-row:last-child { border-bottom: none; }
        .detail-row svg { width: 15px; height: 15px; color: var(--gray-400); flex-shrink: 0; }
        .detail-label { font-size: 10px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.06em; color: var(--gray-400); margin-bottom: 2px; }
        .detail-val   { font-size: 14px; font-weight: 600; color: var(--gray-800); }

        /* pill */
        .pill { display: inline-flex; align-items: center; gap: 4px; padding: 3px 9px; border-radius: 20px; font-size: 11px; font-weight: 500; }
        .pill::before { content: ''; width: 5px; height: 5px; border-radius: 50%; }
        .pill-active   { background: var(--green-50); color: var(--green-700); }
        .pill-active::before   { background: var(--green-700); }
        .pill-inactive { background: var(--amber-50); color: var(--amber-700); }
        .pill-inactive::before { background: var(--amber-700); }

        /* expertise tags */
        .tag-row { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 8px; }
        .tag { background: var(--blue-50); color: var(--blue-800); font-size: 12px; font-weight: 500; padding: 4px 12px; border-radius: 20px; border: 1px solid var(--blue-100); }

        /* ── CONFIRM CARD ── */
        .confirm-info {
            background: var(--blue-25);
            border: 1px solid var(--blue-100);
            border-radius: var(--radius-md);
            padding: 16px;
            margin-bottom: 20px;
        }
        .confirm-info-label { font-size: 11px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.06em; color: var(--blue-800); margin-bottom: 10px; }
        .confirm-row { display: flex; justify-content: space-between; align-items: center; padding: 6px 0; border-bottom: 1px solid var(--blue-100); font-size: 13px; }
        .confirm-row:last-child { border-bottom: none; }
        .confirm-row .ck { color: var(--gray-400); font-weight: 500; }
        .confirm-row .cv { color: var(--gray-800); font-weight: 600; text-align: right; }

        .confirm-note {
            background: var(--amber-50); border: 1px solid var(--amber-200);
            border-radius: var(--radius-md); padding: 12px 14px;
            font-size: 13px; color: var(--amber-700); line-height: 1.6;
            display: flex; gap: 10px; margin-bottom: 20px;
        }
        .confirm-note svg { width: 16px; height: 16px; flex-shrink: 0; margin-top: 2px; }

        /* ── ACTION BUTTONS ── */
        .action-row { display: flex; gap: 10px; flex-wrap: wrap; }
        .btn-primary { display: inline-flex; align-items: center; justify-content: center; gap: 7px; padding: 10px 22px; background: var(--blue-800); color: var(--white); border: none; border-radius: var(--radius-md); font-size: 14px; font-weight: 500; font-family: 'DM Sans', sans-serif; cursor: pointer; text-decoration: none; transition: background 0.15s; }
        .btn-primary:hover { background: var(--blue-700); }
        .btn-secondary { display: inline-flex; align-items: center; justify-content: center; gap: 7px; padding: 10px 22px; background: var(--white); color: var(--gray-800); border: 1px solid var(--gray-200); border-radius: var(--radius-md); font-size: 14px; font-weight: 500; font-family: 'DM Sans', sans-serif; cursor: pointer; text-decoration: none; transition: background 0.15s; }
        .btn-secondary:hover { background: var(--gray-50); }

        /* ── NOT FOUND ── */
        .not-found { text-align: center; padding: 48px 20px; color: var(--gray-400); }
        .not-found svg { width: 40px; height: 40px; margin: 0 auto 12px; display: block; opacity: 0.3; }
        .not-found h3 { font-size: 16px; font-weight: 600; color: var(--gray-600); margin-bottom: 6px; }
        .not-found p  { font-size: 14px; }
    </style>
</head>
<body>

<!-- ════════════ SIDEBAR ════════════ -->
<aside class="sidebar">
    <div class="sidebar-brand">
        <div class="logo">
            <div class="logo-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
            </div>
            <div>
                <div class="logo-text">MentorKE</div>
                <div class="logo-sub">Mentee Portal</div>
            </div>
        </div>
    </div>
    <nav class="sidebar-nav">
        <div class="nav-section-label">Menu</div>
        <a href="<%= ctx %>/app/mentee-dashboard/" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>
            Dashboard
        </a>
        <a href="<%= ctx %>/app/mentee-sessions/browse" class="nav-link active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>
            Browse Mentors
        </a>
        <a href="<%= ctx %>/app/mentee-sessions/my-requests" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            My Requests
        </a>
        <a href="<%= ctx %>/app/sessions/upcoming" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            Upcoming Sessions
        </a>
        <a href="<%= ctx %>/app/sessions/completed" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
            Completed Sessions
        </a>
        <a href="<%= ctx %>/app/messaging/list-conversations" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            Messages
        </a>
        <a href="<%= ctx %>/app/home/" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
            Home
        </a>
    </nav>
    <div class="sidebar-footer">
        <div class="sidebar-user">
            <div class="user-avatar"><%= username.substring(0,1).toUpperCase() %></div>
            <div>
                <div class="user-name"><%= username %></div>
                <div class="user-role">Mentee</div>
            </div>
        </div>
        <a href="<%= ctx %>/app/login/?action=logout" class="btn-logout">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
            Sign out
        </a>
    </div>
</aside>

<!-- ════════════ MAIN ════════════ -->
<div class="main">

    <div class="topbar">
        <div>
            <h1>Request Mentorship</h1>
            <p><%= mentor != null ? "Confirm your request to " + mentorName : "No mentor selected" %></p>
        </div>
        <a href="<%= ctx %>/app/mentee-sessions/browse" class="btn-outline">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg>
            Back to mentors
        </a>
    </div>

    <div class="content">

        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="alert alert-error">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            <%= errorMessage %>
        </div>
        <% } %>

        <% if (mentor != null) { %>

        <div class="request-grid">

            <!-- LEFT: mentor identity -->
            <div class="card">
                <div class="card-body">
                    <div class="mentor-avatar-lg"><%= initials %></div>
                    <div class="mentor-name-lg"><%= mentorName %></div>
                    <div class="mentor-spec-lg"><%= mentorSpec %></div>

                    <div>
                        <div class="detail-row">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                            <div>
                                <div class="detail-label">Experience</div>
                                <div class="detail-val"><%= mentorYears %> yr<%= mentorYears != 1 ? "s" : "" %></div>
                            </div>
                        </div>
                        <div class="detail-row">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>
                            <div>
                                <div class="detail-label">Specialization</div>
                                <div class="detail-val"><%= mentorSpec %></div>
                            </div>
                        </div>
                        <div class="detail-row">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                            <div>
                                <div class="detail-label">Status</div>
                                <div style="margin-top:4px;"><span class="pill <%= isActive ? "pill-active" : "pill-inactive" %>"><%= mentorStatus %></span></div>
                            </div>
                        </div>
                        <% if (mentorExp2 != null) { %>
                        <div class="detail-row" style="align-items:flex-start; flex-direction:column; gap:6px;">
                            <div class="detail-label">Expertise</div>
                            <div class="tag-row">
                                <%
                                    String[] areas = mentorExp2.split("[,;\\n]+");
                                    for (String area : areas) {
                                        area = area.trim();
                                        if (!area.isEmpty()) {
                                %>
                                <span class="tag"><%= area %></span>
                                <%  } } %>
                            </div>
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>

            <!-- RIGHT: confirm request -->
            <div class="card">
                <div class="card-header">
                    <h2>Confirm your request</h2>
                    <p>Review the details before sending</p>
                </div>
                <div class="card-body">

                    <!-- Summary -->
                    <div class="confirm-info">
                        <div class="confirm-info-label">Request summary</div>
                        <div class="confirm-row">
                            <span class="ck">Mentor</span>
                            <span class="cv"><%= mentorName %></span>
                        </div>
                        <div class="confirm-row">
                            <span class="ck">Specialization</span>
                            <span class="cv"><%= mentorSpec %></span>
                        </div>
                        <div class="confirm-row">
                            <span class="ck">Experience</span>
                            <span class="cv"><%= mentorYears %> yr<%= mentorYears != 1 ? "s" : "" %></span>
                        </div>
                        <div class="confirm-row">
                            <span class="ck">Availability</span>
                            <span class="cv"><span class="pill <%= isActive ? "pill-active" : "pill-inactive" %>"><%= mentorStatus %></span></span>
                        </div>
                    </div>

                    <!-- Warning note -->
                    <div class="confirm-note">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                        <span>Your request will be <strong>pending</strong> until the mentor approves or rejects it. You can cancel it any time from My Requests.</span>
                    </div>

                    <!-- Form -->
                    <form method="post" action="<%= ctx %>/app/mentee-sessions/request-mentor">
                        <input type="hidden" name="mentorId"       value="<%= mentor.getId() %>">
                        <input type="hidden" name="specialization" value="<%= specialization != null ? specialization : "" %>">
                        <div class="action-row">
                            <button class="btn-primary" type="submit">
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
                                Send request
                            </button>
                            <a class="btn-secondary" href="<%= ctx %>/app/mentee-sessions/browse">
                                Cancel
                            </a>
                        </div>
                    </form>

                </div>
            </div>

        </div>

        <% } else { %>
        <div class="card">
            <div class="card-body">
                <div class="not-found">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/><line x1="18" y1="8" x2="23" y2="13"/><line x1="23" y1="8" x2="18" y2="13"/></svg>
                    <h3>No mentor selected</h3>
                    <p>Please browse mentors and select one to request mentorship.</p>
                    <a href="<%= ctx %>/app/mentee-sessions/browse" class="btn-outline" style="display:inline-flex; margin-top:16px;">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg>
                        Browse mentors
                    </a>
                </div>
            </div>
        </div>
        <% } %>

    </div><!-- /content -->
</div><!-- /main -->

</body>
</html>
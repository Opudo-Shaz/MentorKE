<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="app.model.Mentor" %>
<%@ page import="app.model.Mentee" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mentor Dashboard — MentorKE</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --blue-900: #0a2e6e;
            --blue-800: #0d47a1;
            --blue-700: #1565c0;
            --blue-600: #1976d2;
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
            --radius-sm: 6px;
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
        .sidebar {
            width: var(--sidebar-w);
            background: var(--blue-800);
            height: 100vh;
            position: fixed;
            left: 0; top: 0;
            display: flex;
            flex-direction: column;
            z-index: 50;
        }
        .sidebar-brand {
            padding: 22px 18px 18px;
            border-bottom: 1px solid rgba(255,255,255,0.12);
        }
        .sidebar-brand .logo { display: flex; align-items: center; gap: 10px; }
        .logo-icon {
            width: 34px; height: 34px;
            background: rgba(255,255,255,0.18);
            border-radius: var(--radius-md);
            display: flex; align-items: center; justify-content: center;
        }
        .logo-text { font-size: 17px; font-weight: 600; color: var(--white); }
        .logo-sub  { font-size: 11px; color: rgba(255,255,255,0.5); margin-top: 1px; }

        .sidebar-nav { flex: 1; padding: 14px 10px; overflow-y: auto; }
        .nav-section-label {
            font-size: 10px; font-weight: 600;
            letter-spacing: 0.08em; color: rgba(255,255,255,0.4);
            text-transform: uppercase; padding: 12px 8px 6px;
        }
        .nav-link {
            display: flex; align-items: center; gap: 10px;
            padding: 9px 12px; border-radius: var(--radius-md);
            color: rgba(255,255,255,0.72);
            font-size: 14px; font-weight: 400;
            text-decoration: none; margin-bottom: 2px;
            transition: background 0.15s, color 0.15s;
        }
        .nav-link svg { flex-shrink: 0; width: 18px; height: 18px; }
        .nav-link:hover { background: rgba(255,255,255,0.1); color: var(--white); }
        .nav-link.active { background: rgba(255,255,255,0.18); color: var(--white); font-weight: 500; }

        .sidebar-footer {
            padding: 14px 16px;
            border-top: 1px solid rgba(255,255,255,0.12);
        }
        .sidebar-user { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
        .user-avatar {
            width: 34px; height: 34px; border-radius: 50%;
            background: rgba(255,255,255,0.2);
            display: flex; align-items: center; justify-content: center;
            font-size: 13px; font-weight: 600; color: var(--white); flex-shrink: 0;
        }
        .user-name { font-size: 13px; font-weight: 500; color: var(--white); }
        .user-role { font-size: 11px; color: rgba(255,255,255,0.5); }
        .btn-logout {
            display: flex; align-items: center; justify-content: center; gap: 7px;
            width: 100%; padding: 8px;
            background: rgba(255,255,255,0.08);
            border: 1px solid rgba(255,255,255,0.15);
            border-radius: var(--radius-md);
            color: rgba(255,255,255,0.75);
            font-size: 13px; font-family: 'DM Sans', sans-serif;
            cursor: pointer; text-decoration: none;
            transition: background 0.15s;
        }
        .btn-logout:hover { background: rgba(255,255,255,0.16); color: var(--white); }

        /* ── MAIN ── */
        .main { margin-left: var(--sidebar-w); flex: 1; min-height: 100vh; display: flex; flex-direction: column; }

        /* ── TOPBAR ── */
        .topbar {
            height: 60px; background: var(--white);
            border-bottom: 1px solid var(--gray-200);
            display: flex; align-items: center; justify-content: space-between;
            padding: 0 28px;
            position: sticky; top: 0; z-index: 40;
        }
        .topbar h1 { font-size: 17px; font-weight: 600; color: var(--gray-800); }
        .topbar p  { font-size: 12px; color: var(--gray-400); margin-top: 1px; }
        .status-badge {
            display: inline-flex; align-items: center; gap: 5px;
            padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 500;
        }
        .status-badge::before { content: ''; width: 6px; height: 6px; border-radius: 50%; }
        .badge-active   { background: var(--green-50); color: var(--green-700); }
        .badge-active::before { background: var(--green-700); }
        .badge-inactive { background: var(--amber-50); color: var(--amber-700); }
        .badge-inactive::before { background: var(--amber-700); }

        /* ── CONTENT ── */
        .content { padding: 24px 28px; flex: 1; }

        /* ── ALERT ── */
        .alert {
            display: flex; align-items: center; gap: 10px;
            padding: 12px 16px; border-radius: var(--radius-md);
            font-size: 14px; margin-bottom: 20px;
        }
        .alert-error { background: var(--red-50); color: var(--red-700); border: 1px solid var(--red-200); }

        /* ── STATS ── */
        .stats-grid {
            display: grid; grid-template-columns: repeat(4, 1fr);
            gap: 16px; margin-bottom: 24px;
        }
        .stat-card {
            background: var(--white); border: 1px solid var(--gray-200);
            border-radius: var(--radius-lg); padding: 18px 20px;
            display: flex; align-items: center; gap: 14px;
            box-shadow: var(--shadow-sm);
        }
        .stat-icon {
            width: 42px; height: 42px; border-radius: var(--radius-md);
            display: flex; align-items: center; justify-content: center; flex-shrink: 0;
        }
        .stat-icon svg { width: 20px; height: 20px; }
        .stat-icon.blue  { background: var(--blue-50); }
        .stat-icon.teal  { background: #e0f2f1; }
        .stat-icon.green { background: var(--green-50); }
        .stat-icon.amber { background: var(--amber-50); }
        .stat-label { font-size: 12px; color: var(--gray-400); margin-bottom: 3px; }
        .stat-value { font-size: 22px; font-weight: 600; color: var(--gray-800); line-height: 1; }

        /* ── TWO-COL ── */
        .two-col { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px; }
        .full-col { margin-bottom: 20px; }

        /* ── CARD ── */
        .card {
            background: var(--white); border: 1px solid var(--gray-200);
            border-radius: var(--radius-lg); overflow: hidden; box-shadow: var(--shadow-sm);
        }
        .card-header {
            padding: 14px 20px; border-bottom: 1px solid var(--gray-200);
            display: flex; align-items: center; justify-content: space-between;
        }
        .card-header h2 { font-size: 15px; font-weight: 600; color: var(--gray-800); }
        .card-header p  { font-size: 12px; color: var(--gray-400); margin-top: 1px; }
        .card-body { padding: 20px; }

        /* ── PROFILE ── */
        .profile-avatar {
            width: 56px; height: 56px; border-radius: 50%;
            background: var(--blue-800);
            display: flex; align-items: center; justify-content: center;
            font-size: 20px; font-weight: 600; color: var(--white);
            margin-bottom: 12px;
        }
        .profile-name { font-size: 16px; font-weight: 600; color: var(--gray-800); }
        .profile-sub  { font-size: 13px; color: var(--gray-400); margin-bottom: 16px; }
        .profile-row {
            display: flex; justify-content: space-between; align-items: flex-start;
            padding: 8px 0; border-bottom: 1px solid var(--gray-100); font-size: 13px;
        }
        .profile-row:last-child { border-bottom: none; }
        .profile-row .key   { color: var(--gray-400); font-weight: 500; flex-shrink: 0; }
        .profile-row .value { color: var(--gray-800); text-align: right; max-width: 60%; }

        /* ── TABLE ── */
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; }
        thead th {
            background: var(--blue-25); color: var(--blue-800);
            font-size: 12px; font-weight: 600;
            padding: 10px 16px; text-align: left;
            border-bottom: 1px solid var(--blue-100); white-space: nowrap;
        }
        tbody td {
            padding: 12px 16px; font-size: 13px; color: var(--gray-800);
            border-bottom: 1px solid var(--gray-100); vertical-align: middle;
        }
        tbody tr:last-child td { border-bottom: none; }
        tbody tr:hover td { background: var(--gray-50); }

        /* ── PILLS ── */
        .pill {
            display: inline-flex; align-items: center; gap: 4px;
            padding: 3px 9px; border-radius: 20px; font-size: 11px; font-weight: 500;
        }
        .pill::before { content: ''; width: 5px; height: 5px; border-radius: 50%; }
        .pill-active   { background: var(--green-50);  color: var(--green-700); }
        .pill-active::before   { background: var(--green-700); }
        .pill-inactive { background: var(--amber-50);  color: var(--amber-700); }
        .pill-inactive::before { background: var(--amber-700); }

         /* ── EXPERTISE TAGS ── */
         .tag {
             display: inline-block;
             background: var(--blue-50); color: var(--blue-800);
             font-size: 12px; font-weight: 500;
             padding: 4px 12px; border-radius: 20px;
             border: 1px solid var(--blue-100);
             margin: 3px;
         }

         /* ── BIO / GOALS BOX ── */
         .info-box {
             font-size: 13px; color: var(--gray-600); line-height: 1.7;
             background: var(--blue-25);
             border-left: 3px solid var(--blue-200);
             border-radius: 0 var(--radius-md) var(--radius-md) 0;
             padding: 12px 14px;
         }

         /* ── OVERVIEW TILES ── */
         .overview-tile {
             border-radius: var(--radius-md); padding: 14px 16px; margin-bottom: 12px;
         }
         .overview-tile:last-child { margin-bottom: 0; }
         .overview-tile-label {
             font-size: 11px; font-weight: 600; text-transform: uppercase;
             letter-spacing: 0.06em; margin-bottom: 4px;
         }
         .overview-tile-value { font-size: 15px; font-weight: 600; color: var(--gray-800); }

         /* ── EMPTY STATE ── */
         .empty-state { text-align: center; padding: 32px 20px; color: var(--gray-400); }
         .empty-state svg { width: 36px; height: 36px; margin: 0 auto 10px; display: block; opacity: 0.35; }
         .empty-state p { font-size: 14px; }

         /* ── ACTION GRID ── */
         .action-grid {
             display: grid; grid-template-columns: repeat(5, 1fr); gap: 14px; margin-bottom: 20px;
         }
         .action-card {
             background: var(--white); border: 1px solid var(--gray-200);
             border-radius: var(--radius-lg); padding: 18px; text-align: center;
             box-shadow: var(--shadow-sm); text-decoration: none;
             color: var(--gray-800); transition: all 0.15s;
             display: flex; flex-direction: column; align-items: center; gap: 10px;
         }
         .action-card:hover {
             border-color: var(--blue-200); background: var(--blue-25);
             transform: translateY(-2px);
         }
         .action-icon {
             width: 40px; height: 40px; border-radius: var(--radius-md);
             background: var(--blue-50); display: flex; align-items: center; justify-content: center;
         }
         .action-icon svg { width: 20px; height: 20px; color: var(--blue-800); }
         .action-label { font-size: 13px; font-weight: 500; line-height: 1.3; }

         /* ── UNREAD BADGE ── */
         .unread-badge {
             position: absolute; top: -4px; right: -4px;
             background: var(--red-700); color: var(--white);
             font-size: 10px; font-weight: 600;
             width: 18px; height: 18px;
             border-radius: 50%;
             display: flex; align-items: center; justify-content: center;
             border: 2px solid var(--white);
         }
         .unread-badge.hidden { display: none; }
    </style>
</head>
<body>

<%
    String username = (String) session.getAttribute("username");
    if (username == null) username = "Mentor";

    Mentor mentor = (Mentor) request.getAttribute("mentor");
    List<Mentee> mentees = (List<Mentee>) request.getAttribute("mentees");
    String errorMsg = (String) request.getAttribute("error");

    // safe mentor fields
    String specialization  = (mentor != null && mentor.getSpecialization()    != null) ? mentor.getSpecialization()    : "—";
    String expertise       = (mentor != null && mentor.getExpertise()         != null) ? mentor.getExpertise()         : "";
    String bio             = (mentor != null && mentor.getBio()               != null) ? mentor.getBio()               : "No bio added yet.";
    String qualifications  = (mentor != null && mentor.getQualifications()    != null) ? mentor.getQualifications()    : "No qualifications listed.";
    String phoneNumber     = (mentor != null && mentor.getPhoneNumber()       != null) ? mentor.getPhoneNumber()       : "—";
    String mentorStatus    = (mentor != null && mentor.getStatus()            != null) ? mentor.getStatus()            : "Active";
    String mentorId        = (mentor != null && mentor.getId()                != null) ? String.valueOf(mentor.getId()) : "—";
    int    yearsExp        = (mentor != null) ? mentor.getYearsOfExperience() : 0;

    int menteeCount = (mentees != null) ? mentees.size() : 0;

    String initials = username.substring(0, 1).toUpperCase();
    if (specialization.length() > 1) initials = specialization.substring(0, 1).toUpperCase();
%>

<!-- ══════════════════ SIDEBAR ══════════════════ -->
<aside class="sidebar">
    <div class="sidebar-brand">
        <div class="logo">
            <div class="logo-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
            </div>
            <div>
                <div class="logo-text">MentorKE</div>
                <div class="logo-sub">Mentor Portal</div>
            </div>
        </div>
    </div>

    <nav class="sidebar-nav">
        <div class="nav-section-label">Menu</div>

        <a href="mentor-dashboard" class="nav-link active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>
            Dashboard
        </a>

        <a href="index" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
            Home
        </a>

        <a href="about" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            About
        </a>
    </nav>

    <div class="sidebar-footer">
        <div class="sidebar-user">
            <div class="user-avatar"><%= username.substring(0,1).toUpperCase() %></div>
            <div>
                <div class="user-name"><%= username %></div>
                <div class="user-role">Mentor</div>
            </div>
        </div>
        <a href="login?action=logout" class="btn-logout">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
            Sign out
        </a>
    </div>
</aside>

<!-- ══════════════════ MAIN ══════════════════ -->
<div class="main">

    <div class="topbar">
        <div>
            <h1>My Dashboard</h1>
            <p>Welcome back, <%= username %></p>
        </div>
        <span class="status-badge <%= "Active".equals(mentorStatus) ? "badge-active" : "badge-inactive" %>">
            <%= mentorStatus %>
        </span>
    </div>

    <div class="content">

        <% if (errorMsg != null && !errorMsg.isEmpty()) { %>
        <div class="alert alert-error">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            <%= errorMsg %>
        </div>
        <% } %>

        <!-- QUICK ACTIONS -->
        <div class="action-grid">
            <a href="/MentorKE/app/mentor-requests/pending" class="action-card">
                <div class="action-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                </div>
                <span class="action-label">Pending<br>Requests</span>
            </a>

            <a href="sessions?action=upcoming" class="action-card">
                <div class="action-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <span class="action-label">Upcoming<br>Sessions</span>
            </a>
            <a href="sessions?action=completed" class="action-card">
                <div class="action-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                </div>
                <span class="action-label">Completed<br>Sessions</span>
            </a>
            <a href="messaging?action=list-conversations" class="action-card" id="messagesActionCard">
                <div class="action-icon" style="position:relative;">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                    <span id="unreadBadge" class="unread-badge hidden">0</span>
                </div>
                <span class="action-label">Messages</span>
            </a>
        </div>

        <!-- STATS -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon blue">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#1565c0" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>
                </div>
                <div>
                    <div class="stat-label">Mentor ID</div>
                    <div class="stat-value" style="font-size:18px">#<%= mentorId %></div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon teal">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#0f6e56" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                </div>
                <div>
                    <div class="stat-label">Active mentees</div>
                    <div class="stat-value"><%= menteeCount %></div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon green">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#15803d" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <div>
                    <div class="stat-label">Years experience</div>
                    <div class="stat-value"><%= yearsExp %></div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon amber">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#b45309" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
                </div>
                <div>
                    <div class="stat-label">Specialization</div>
                    <div class="stat-value" style="font-size:13px; padding-top:5px"><%= specialization %></div>
                </div>
            </div>
        </div>

        <!-- ROW 1: Profile + Overview -->
        <div class="two-col">

            <!-- My Profile -->
            <div class="card">
                <div class="card-header">
                    <div>
                        <h2>My profile</h2>
                        <p>Your registered details</p>
                    </div>
                </div>
                <div class="card-body">
                    <div class="profile-avatar"><%= initials %></div>
                    <div class="profile-name"><%= username %></div>
                    <div class="profile-sub">Mentor · MentorKE</div>
                    <div class="profile-fields" style="display:flex; flex-direction:column; gap:0;">
                        <div class="profile-row">
                            <span class="key">Specialization</span>
                            <span class="value"><%= specialization %></span>
                        </div>
                        <div class="profile-row">
                            <span class="key">Experience</span>
                            <span class="value"><%= yearsExp %> years</span>
                        </div>
                        <div class="profile-row">
                            <span class="key">Phone number</span>
                            <span class="value"><%= phoneNumber %></span>
                        </div>
                        <div class="profile-row">
                            <span class="key">Mentees assigned</span>
                            <span class="value"><%= menteeCount %></span>
                        </div>
                        <div class="profile-row">
                            <span class="key">Status</span>
                            <span class="value">
                                <span class="pill <%= "Active".equals(mentorStatus) ? "pill-active" : "pill-inactive" %>">
                                    <%= mentorStatus %>
                                </span>
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Overview panel -->
            <div class="card">
                <div class="card-header">
                    <div>
                        <h2>Programme overview</h2>
                        <p>Your mentorship at a glance</p>
                    </div>
                </div>
                <div class="card-body">
                    <div class="overview-tile" style="background:var(--blue-25);">
                        <div class="overview-tile-label" style="color:var(--blue-800);">Specialization</div>
                        <div class="overview-tile-value"><%= specialization %></div>
                    </div>
                    <div class="overview-tile" style="background:var(--green-50);">
                        <div class="overview-tile-label" style="color:var(--green-700);">Years of experience</div>
                        <div class="overview-tile-value"><%= yearsExp %> years</div>
                    </div>
                    <div class="overview-tile" style="background:var(--amber-50);">
                        <div class="overview-tile-label" style="color:var(--amber-700);">Mentees assigned</div>
                        <div class="overview-tile-value"><%= menteeCount %> mentee<%= menteeCount != 1 ? "s" : "" %></div>
                    </div>
                    <div class="overview-tile" style="background:var(--gray-50); border:1px solid var(--gray-200);">
                        <div class="overview-tile-label" style="color:var(--gray-600);">Account status</div>
                        <div class="overview-tile-value"><%= mentorStatus %></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- ROW 2: Bio + Expertise -->
        <div class="two-col">

            <!-- Bio -->
            <div class="card">
                <div class="card-header">
                    <div>
                        <h2>About me</h2>
                        <p>Your bio and background</p>
                    </div>
                </div>
                <div class="card-body">
                    <div class="info-box"><%= bio %></div>

                    <% if (!qualifications.equals("No qualifications listed.")) { %>
                    <div style="margin-top:18px;">
                        <div style="font-size:12px; font-weight:600; color:var(--gray-400); text-transform:uppercase; letter-spacing:0.06em; margin-bottom:8px;">Qualifications</div>
                        <div class="info-box"><%= qualifications %></div>
                    </div>
                    <% } %>
                </div>
            </div>

            <!-- Expertise -->
            <div class="card">
                <div class="card-header">
                    <div>
                        <h2>Expertise areas</h2>
                        <p>Topics you mentor on</p>
                    </div>
                </div>
                <div class="card-body">
                    <% if (!expertise.isEmpty()) {
                           String[] areas = expertise.split("[,;\\n]+");
                    %>
                    <div style="display:flex; flex-wrap:wrap; gap:6px; margin-bottom:20px;">
                        <% for (String area : areas) {
                               area = area.trim();
                               if (!area.isEmpty()) { %>
                        <span class="tag"><%= area %></span>
                        <%     }
                           } %>
                    </div>
                    <% } else { %>
                    <p style="font-size:13px; color:var(--gray-400); margin-bottom:20px;">No expertise areas listed yet.</p>
                    <% } %>

                    <!-- Quick stats inside expertise card -->
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; margin-top:8px;">
                        <div style="background:var(--blue-25); border-radius:var(--radius-md); padding:12px; text-align:center;">
                            <div style="font-size:22px; font-weight:700; color:var(--blue-800);"><%= menteeCount %></div>
                            <div style="font-size:12px; color:var(--gray-400); margin-top:2px;">Mentees</div>
                        </div>
                        <div style="background:var(--green-50); border-radius:var(--radius-md); padding:12px; text-align:center;">
                            <div style="font-size:22px; font-weight:700; color:var(--green-700);"><%= yearsExp %></div>
                            <div style="font-size:12px; color:var(--gray-400); margin-top:2px;">Yrs exp.</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- ROW 3: Mentees Table -->
        <div class="full-col">
            <div class="card">
                <div class="card-header">
                    <div>
                        <h2>My mentees</h2>
                        <p>All mentees currently assigned to you</p>
                    </div>
                    <span style="background:var(--blue-50); color:var(--blue-800); font-size:12px; font-weight:600; padding:4px 12px; border-radius:20px; border:1px solid var(--blue-100);">
                        <%= menteeCount %> assigned
                    </span>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>User ID</th>
                                <th>Education level</th>
                                <th>Field of study</th>
                                <th>Phone</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (mentees != null && !mentees.isEmpty()) {
                                   for (Mentee m : mentees) { %>
                            <tr>
                                <td>#<%= m.getId() %></td>
                                <td><%= m.getUserId() %></td>
                                <td><%= m.getEducationLevel() != null ? m.getEducationLevel() : "—" %></td>
                                <td><%= m.getFieldOfStudy()   != null ? m.getFieldOfStudy()   : "—" %></td>
                                <td><%= m.getPhoneNumber()    != null ? m.getPhoneNumber()    : "—" %></td>
                                <td>
                                    <span class="pill <%= "Active".equals(m.getStatus()) ? "pill-active" : "pill-inactive" %>">
                                        <%= m.getStatus() != null ? m.getStatus() : "—" %>
                                    </span>
                                </td>
                            </tr>
                            <%     }
                               } else { %>
                            <tr>
                                <td colspan="6">
                                    <div class="empty-state">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
                                        <p>No mentees have been assigned to you yet.</p>
                                    </div>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </div><!-- /content -->
</div><!-- /main -->

<script>
    /* ── Unread message count polling ── */
    function updateUnreadCount() {
        fetch('messaging?action=unread-count', { credentials: 'same-origin' })
            .then(response => response.json())
            .then(data => {
                var badge = document.getElementById('unreadBadge');
                if (data.unreadCount && data.unreadCount > 0) {
                    badge.textContent = data.unreadCount;
                    badge.classList.remove('hidden');
                } else {
                    badge.classList.add('hidden');
                }
            })
            .catch(err => console.log('Unread count fetch failed:', err));
    }

    // Initial fetch
    updateUnreadCount();

    // Poll every 30 seconds
    setInterval(updateUnreadCount, 30000);
</script>

</body>
</html>
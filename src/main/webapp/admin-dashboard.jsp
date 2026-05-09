<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.util.List" %>
<%@ page import="app.model.User" %>
<%@ page import="app.model.Mentor" %>
<%@ page import="app.model.Mentee" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard — MentorKE</title>
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
            --red-50:   #fef2f2;
            --red-200:  #fecaca;
            --red-600:  #dc2626;
            --red-700:  #b91c1c;
            --green-50: #f0fdf4;
            --green-200:#bbf7d0;
            --green-700:#15803d;
            --amber-50: #fffbeb;
            --amber-200:#fde68a;
            --amber-700:#b45309;
            --sidebar-w: 230px;
            --topbar-h:  60px;
            --radius-sm: 6px;
            --radius-md: 8px;
            --radius-lg: 12px;
            --shadow-sm: 0 1px 3px rgba(0,0,0,0.07), 0 1px 2px rgba(0,0,0,0.05);
            --shadow-md: 0 4px 12px rgba(0,0,0,0.08);
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
        .sidebar-brand .logo {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .sidebar-brand .logo-icon {
            width: 34px; height: 34px;
            background: rgba(255,255,255,0.18);
            border-radius: var(--radius-md);
            display: flex; align-items: center; justify-content: center;
            font-size: 16px;
        }
        .sidebar-brand .logo-text {
            font-size: 17px;
            font-weight: 600;
            color: var(--white);
            letter-spacing: -0.2px;
        }
        .sidebar-brand .logo-sub {
            font-size: 11px;
            color: rgba(255,255,255,0.5);
            margin-top: 1px;
        }

        .sidebar-nav {
            flex: 1;
            padding: 14px 10px;
            overflow-y: auto;
        }

        .nav-section-label {
            font-size: 10px;
            font-weight: 600;
            letter-spacing: 0.08em;
            color: rgba(255,255,255,0.4);
            text-transform: uppercase;
            padding: 12px 8px 6px;
        }

        .nav-link {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 9px 12px;
            border-radius: var(--radius-md);
            color: rgba(255,255,255,0.72);
            font-size: 14px;
            font-weight: 400;
            cursor: pointer;
            text-decoration: none;
            border: none;
            background: none;
            width: 100%;
            transition: background 0.15s, color 0.15s;
            margin-bottom: 2px;
        }
        .nav-link svg { flex-shrink: 0; width: 18px; height: 18px; }
        .nav-link:hover { background: rgba(255,255,255,0.1); color: var(--white); }
        .nav-link.active {
            background: rgba(255,255,255,0.18);
            color: var(--white);
            font-weight: 500;
        }
        .nav-link .nav-badge {
            margin-left: auto;
            background: rgba(255,255,255,0.2);
            color: rgba(255,255,255,0.9);
            font-size: 11px;
            font-weight: 600;
            padding: 1px 7px;
            border-radius: 10px;
        }

        .sidebar-footer {
            padding: 14px 16px;
            border-top: 1px solid rgba(255,255,255,0.12);
        }
        .sidebar-user {
            display: flex; align-items: center; gap: 10px; margin-bottom: 10px;
        }
        .user-avatar {
            width: 34px; height: 34px;
            border-radius: 50%;
            background: rgba(255,255,255,0.2);
            display: flex; align-items: center; justify-content: center;
            font-size: 13px; font-weight: 600; color: var(--white);
            flex-shrink: 0;
        }
        .user-name { font-size: 13px; font-weight: 500; color: var(--white); }
        .user-role { font-size: 11px; color: rgba(255,255,255,0.5); }
        .btn-logout {
            display: flex; align-items: center; justify-content: center; gap: 7px;
            width: 100%;
            padding: 8px;
            background: rgba(255,255,255,0.08);
            border: 1px solid rgba(255,255,255,0.15);
            border-radius: var(--radius-md);
            color: rgba(255,255,255,0.75);
            font-size: 13px;
            font-family: 'DM Sans', sans-serif;
            cursor: pointer;
            text-decoration: none;
            transition: background 0.15s;
        }
        .btn-logout:hover { background: rgba(255,255,255,0.16); color: var(--white); }

        /* ── MAIN ── */
        .main {
            margin-left: var(--sidebar-w);
            flex: 1;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        /* ── TOPBAR ── */
        .topbar {
            height: var(--topbar-h);
            background: var(--white);
            border-bottom: 1px solid var(--gray-200);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 28px;
            position: sticky;
            top: 0;
            z-index: 40;
        }
        .topbar-left h1 {
            font-size: 17px;
            font-weight: 600;
            color: var(--gray-800);
            letter-spacing: -0.2px;
        }
        .topbar-left p { font-size: 12px; color: var(--gray-400); margin-top: 1px; }
        .topbar-right { display: flex; align-items: center; gap: 10px; }
        .count-badge {
            background: var(--blue-50);
            color: var(--blue-800);
            font-size: 12px; font-weight: 600;
            padding: 4px 12px;
            border-radius: 20px;
            border: 1px solid var(--blue-100);
        }

        /* ── CONTENT ── */
        .content { padding: 24px 28px; flex: 1; }

        /* ── STATS ── */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 16px;
            margin-bottom: 24px;
        }
        .stat-card {
            background: var(--white);
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-lg);
            padding: 18px 20px;
            display: flex;
            align-items: center;
            gap: 16px;
            box-shadow: var(--shadow-sm);
        }
        .stat-icon {
            width: 44px; height: 44px;
            border-radius: var(--radius-md);
            display: flex; align-items: center; justify-content: center;
            flex-shrink: 0;
        }
        .stat-icon.blue { background: var(--blue-50); }
        .stat-icon.teal { background: #e0f2f1; }
        .stat-icon.amber { background: var(--amber-50); }
        .stat-icon svg { width: 22px; height: 22px; }
        .stat-label { font-size: 12px; color: var(--gray-400); margin-bottom: 3px; }
        .stat-value { font-size: 24px; font-weight: 600; color: var(--gray-800); line-height: 1; }

        /* ── SECTION CARD ── */
        .section-card {
            background: var(--white);
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-lg);
            overflow: hidden;
            box-shadow: var(--shadow-sm);
            margin-bottom: 20px;
        }
        .section-header {
            padding: 16px 20px;
            border-bottom: 1px solid var(--gray-200);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .section-header h2 { font-size: 15px; font-weight: 600; color: var(--gray-800); }
        .section-header p  { font-size: 12px; color: var(--gray-400); margin-top: 1px; }

        /* ── BUTTONS ── */
        .btn {
            display: inline-flex; align-items: center; gap: 6px;
            padding: 8px 16px;
            border-radius: var(--radius-md);
            font-size: 13px; font-weight: 500;
            font-family: 'DM Sans', sans-serif;
            cursor: pointer; border: none; transition: background 0.15s, opacity 0.15s;
        }
        .btn svg { width: 16px; height: 16px; }
        .btn-primary { background: var(--blue-800); color: var(--white); }
        .btn-primary:hover { background: var(--blue-700); }
        .btn-outline {
            background: var(--white); color: var(--gray-600);
            border: 1px solid var(--gray-200);
        }
        .btn-outline:hover { background: var(--gray-100); }
        .btn-edit {
            background: var(--blue-25); color: var(--blue-800);
            border: 1px solid var(--blue-100);
            padding: 5px 10px; font-size: 12px;
        }
        .btn-edit:hover { background: var(--blue-50); }
        .btn-delete {
            background: var(--red-50); color: var(--red-700);
            border: 1px solid var(--red-200);
            padding: 5px 10px; font-size: 12px;
        }
        .btn-delete:hover { background: #fee2e2; }

        /* ── COLLAPSIBLE FORM ── */
        .add-form-wrap {
            overflow: hidden;
            max-height: 0;
            transition: max-height 0.3s ease;
            border-bottom: 1px solid transparent;
        }
        .add-form-wrap.open {
            max-height: 600px;
            border-bottom-color: var(--gray-200);
        }
        .add-form-inner {
            padding: 20px;
            background: var(--blue-25);
        }
        .add-form-inner h3 {
            font-size: 14px; font-weight: 600; color: var(--blue-800);
            margin-bottom: 16px;
        }
        .form-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 12px;
            margin-bottom: 12px;
        }
        .form-grid-2 {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 12px;
            margin-bottom: 12px;
        }
        .form-group { display: flex; flex-direction: column; gap: 5px; }
        .form-group.full { grid-column: 1 / -1; }
        .form-group label {
            font-size: 12px; font-weight: 500; color: var(--blue-800);
        }
        .form-group input,
        .form-group select,
        .form-group textarea {
            padding: 8px 11px;
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-md);
            font-size: 13px;
            font-family: 'DM Sans', sans-serif;
            color: var(--gray-800);
            background: var(--white);
            transition: border-color 0.15s, box-shadow 0.15s;
            width: 100%;
        }
        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: var(--blue-600);
            box-shadow: 0 0 0 3px rgba(25,118,210,0.12);
        }
        .form-group textarea { min-height: 70px; resize: vertical; }
        .form-actions { display: flex; gap: 8px; padding-top: 4px; }

        /* ── TABLE ── */
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; }
        thead th {
            background: var(--blue-25);
            color: var(--blue-800);
            font-size: 12px; font-weight: 600;
            padding: 11px 16px;
            text-align: left;
            border-bottom: 1px solid var(--blue-100);
            white-space: nowrap;
        }
        tbody td {
            padding: 12px 16px;
            font-size: 13px;
            color: var(--gray-800);
            border-bottom: 1px solid var(--gray-100);
            vertical-align: middle;
        }
        tbody tr:last-child td { border-bottom: none; }
        tbody tr:hover td { background: var(--gray-50); }

        .td-actions { display: flex; gap: 6px; align-items: center; }

        /* ── PILLS ── */
        .pill {
            display: inline-flex; align-items: center; gap: 5px;
            padding: 3px 10px;
            border-radius: 20px;
            font-size: 12px; font-weight: 500;
        }
        .pill::before {
            content: '';
            width: 6px; height: 6px;
            border-radius: 50%;
        }
        .pill-active  { background: var(--green-50); color: var(--green-700); }
        .pill-active::before  { background: var(--green-700); }
        .pill-inactive{ background: var(--amber-50); color: var(--amber-700); }
        .pill-inactive::before{ background: var(--amber-700); }

        /* ── ALERTS ── */
        .alert {
            display: flex; align-items: center; gap: 10px;
            padding: 12px 16px;
            border-radius: var(--radius-md);
            font-size: 14px;
            margin-bottom: 20px;
        }
        .alert svg { flex-shrink: 0; width: 18px; height: 18px; }
        .alert-success { background: var(--green-50); color: var(--green-700); border: 1px solid var(--green-200); }
        .alert-error   { background: var(--red-50);   color: var(--red-700);   border: 1px solid var(--red-200); }

        /* ── EMPTY STATE ── */
        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: var(--gray-400);
        }
        .empty-state svg { width: 40px; height: 40px; margin: 0 auto 12px; display: block; opacity: 0.4; }
        .empty-state p { font-size: 14px; }

        /* ── MODAL ── */
        .modal-overlay {
            display: none;
            position: fixed; inset: 0;
            background: rgba(15,23,42,0.45);
            z-index: 200;
            align-items: flex-start;
            justify-content: center;
            padding-top: 60px;
            overflow-y: auto;
        }
        .modal-overlay.open { display: flex; }
        .modal {
            background: var(--white);
            border-radius: var(--radius-lg);
            width: 520px;
            max-width: calc(100vw - 40px);
            box-shadow: 0 20px 60px rgba(0,0,0,0.15);
            overflow: hidden;
            margin-bottom: 40px;
        }
        .modal-header {
            padding: 18px 22px;
            border-bottom: 1px solid var(--gray-200);
            display: flex; align-items: center; justify-content: space-between;
        }
        .modal-header h2 { font-size: 16px; font-weight: 600; color: var(--gray-800); }
        .modal-close {
            background: none; border: none; cursor: pointer;
            color: var(--gray-400); font-size: 22px; line-height: 1;
            padding: 2px 6px; border-radius: var(--radius-sm);
            transition: color 0.15s, background 0.15s;
        }
        .modal-close:hover { background: var(--gray-100); color: var(--gray-800); }
        .modal-body { padding: 22px; }
        .modal-footer {
            padding: 16px 22px;
            border-top: 1px solid var(--gray-200);
            display: flex; gap: 8px; justify-content: flex-end;
        }

        /* hidden util */
        .hidden { display: none !important; }
    </style>
</head>
<body>

<%
    String currentView = (String) request.getAttribute("view");
    if (currentView == null) currentView = "users";
    String username = (String) session.getAttribute("username");
    if (username == null) username = "Admin";

    List<User>   users   = (List<User>)   request.getAttribute("users");
    List<Mentor> mentors = (List<Mentor>) request.getAttribute("mentors");
    List<Mentee> mentees = (List<Mentee>) request.getAttribute("mentees");

    int userCount   = (users   != null) ? users.size()   : 0;
    int mentorCount = (mentors != null) ? mentors.size() : 0;
    int menteeCount = (mentees != null) ? mentees.size() : 0;

    String successMsg = request.getParameter("success");
    String errorMsg   = request.getParameter("error");
%>

<!-- ══════════════════ SIDEBAR ══════════════════ -->
<aside class="sidebar">
    <div class="sidebar-brand">
        <div class="logo">
            <div class="logo-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
            </div>
            <div>
                <div class="logo-text">MentorKE</div>
                <div class="logo-sub">Admin Portal</div>
            </div>
        </div>
    </div>

    <nav class="sidebar-nav">
        <div class="nav-section-label">Main</div>

        <a href="admin" class="nav-link <%= "users".equals(currentView) ? "active" : "" %>">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            Users
            <span class="nav-badge"><%= userCount %></span>
        </a>

        <a href="admin?view=mentors" class="nav-link <%= "mentors".equals(currentView) ? "active" : "" %>">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="16"/><line x1="10" y1="14" x2="14" y2="14"/></svg>
            Mentors
            <span class="nav-badge"><%= mentorCount %></span>
        </a>

        <a href="admin?view=mentees" class="nav-link <%= "mentees".equals(currentView) ? "active" : "" %>">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/><polyline points="16 11 18 13 22 9"/></svg>
            Mentees
            <span class="nav-badge"><%= menteeCount %></span>
        </a>
    </nav>

    <div class="sidebar-footer">
        <div class="sidebar-user">
            <div class="user-avatar"><%= username.substring(0,1).toUpperCase() %></div>
            <div>
                <div class="user-name"><%= username %></div>
                <div class="user-role">Administrator</div>
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

    <!-- TOPBAR -->
    <div class="topbar">
        <div class="topbar-left">
            <h1>
                <% if ("users".equals(currentView))   { %>Users
                <% } else if ("mentors".equals(currentView)) { %>Mentors
                <% } else if ("mentees".equals(currentView)) { %>Mentees
                <% } %>
            </h1>
            <p>
                <% if ("users".equals(currentView))   { %>Manage all registered users
                <% } else if ("mentors".equals(currentView)) { %>Manage mentor profiles
                <% } else if ("mentees".equals(currentView)) { %>Manage mentee profiles
                <% } %>
            </p>
        </div>
        <div class="topbar-right">
            <span class="count-badge">
                <% if ("users".equals(currentView))   { %><%= userCount %> users
                <% } else if ("mentors".equals(currentView)) { %><%= mentorCount %> mentors
                <% } else if ("mentees".equals(currentView)) { %><%= menteeCount %> mentees
                <% } %>
            </span>
        </div>
    </div>

    <div class="content">

        <!-- ALERTS -->
        <% if ("user_added".equals(successMsg) || "user_updated".equals(successMsg) || "user_deleted".equals(successMsg)
              || "mentor_added".equals(successMsg) || "mentor_updated".equals(successMsg) || "mentor_deleted".equals(successMsg)
              || "mentee_added".equals(successMsg) || "mentee_updated".equals(successMsg) || "mentee_deleted".equals(successMsg)) { %>
            <div class="alert alert-success">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                <% if ("user_added".equals(successMsg))       { %>User added successfully.
                <% } else if ("user_updated".equals(successMsg))   { %>User updated successfully.
                <% } else if ("user_deleted".equals(successMsg))   { %>User deleted successfully.
                <% } else if ("mentor_added".equals(successMsg))   { %>Mentor added successfully.
                <% } else if ("mentor_updated".equals(successMsg)) { %>Mentor updated successfully.
                <% } else if ("mentor_deleted".equals(successMsg)) { %>Mentor deleted successfully.
                <% } else if ("mentee_added".equals(successMsg))   { %>Mentee added successfully.
                <% } else if ("mentee_updated".equals(successMsg)) { %>Mentee updated successfully.
                <% } else if ("mentee_deleted".equals(successMsg)) { %>Mentee deleted successfully.
                <% } %>
            </div>
        <% } %>
        <% if (errorMsg != null && !errorMsg.isEmpty()) { %>
            <div class="alert alert-error">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                Error: <%= errorMsg %>
            </div>
        <% } %>

        <!-- STATS -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon blue">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#1565c0" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                </div>
                <div>
                    <div class="stat-label">Total users</div>
                    <div class="stat-value"><%= userCount %></div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon teal">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#0f6e56" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="16"/><line x1="10" y1="14" x2="14" y2="14"/></svg>
                </div>
                <div>
                    <div class="stat-label">Total mentors</div>
                    <div class="stat-value"><%= mentorCount %></div>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon amber">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#b45309" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/><polyline points="16 11 18 13 22 9"/></svg>
                </div>
                <div>
                    <div class="stat-label">Total mentees</div>
                    <div class="stat-value"><%= menteeCount %></div>
                </div>
            </div>
        </div>


        <!-- ══════ USERS VIEW ══════ -->
        <% if ("users".equals(currentView)) { %>
        <div class="section-card">
            <div class="section-header">
                <div>
                    <h2>Users list</h2>
                    <p>All registered platform users</p>
                </div>
                <button class="btn btn-primary" onclick="toggleForm('addUserForm')">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                    Add user
                </button>
            </div>

            <!-- Add user form -->
            <div class="add-form-wrap" id="addUserForm">
                <div class="add-form-inner">
                    <h3>New user</h3>
                    <form action="user-management" method="post">
                        <input type="hidden" name="action" value="add">
                        <div class="form-grid">
                            <div class="form-group">
                                <label for="addUsername">Username</label>
                                <input type="text" id="addUsername" name="username" placeholder="Enter username" required>
                            </div>
                            <div class="form-group">
                                <label for="addEmail">Email</label>
                                <input type="email" id="addEmail" name="email" placeholder="Enter email" required>
                            </div>
                            <div class="form-group">
                                <label for="addPassword">Password</label>
                                <input type="password" id="addPassword" name="password" placeholder="Enter password" required>
                            </div>
                            <div class="form-group">
                                <label for="addRole">Role</label>
                                <select id="addRole" name="role" required>
                                    <option value="">Select role</option>
                                    <option value="mentee">Mentee</option>
                                    <option value="mentor">Mentor</option>
                                    <option value="admin">Admin</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="addStatus">Status</label>
                                <select name="status" id="addStatus">
                                    <option value="Active">Active</option>
                                    <option value="Inactive">Inactive</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">Add user</button>
                            <button type="button" class="btn btn-outline" onclick="toggleForm('addUserForm')">Cancel</button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Users table -->
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (users != null && !users.isEmpty()) {
                               for (User user : users) { %>
                        <tr>
                            <td><%= user.getId() %></td>
                            <td><%= user.getUsername() %></td>
                            <td><%= user.getEmail() %></td>
                            <td><%= user.getRole() %></td>
                            <td>
                                <span class="pill <%= "Active".equals(user.getStatus()) ? "pill-active" : "pill-inactive" %>">
                                    <%= user.getStatus() %>
                                </span>
                            </td>
                            <td>
                                <div class="td-actions">
                                    <button class="btn btn-edit" onclick="openEditModal('User','<%= user.getId() %>',{username:'<%= user.getUsername() %>',email:'<%= user.getEmail() %>',role:'<%= user.getRole() %>',status:'<%= user.getStatus() %>'})">
                                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                                        Edit
                                    </button>
                                    <form action="user-management" method="post" style="display:inline">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="userId" value="<%= user.getId() %>">
                                        <button type="submit" class="btn btn-delete" onclick="return confirm('Delete user <%= user.getUsername() %>?')">
                                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6M14 11v6"/><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>
                                            Delete
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                        <% } } else { %>
                        <tr>
                            <td colspan="6">
                                <div class="empty-state">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
                                    <p>No users found</p>
                                </div>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
        <% } %>


        <!-- ══════ MENTORS VIEW ══════ -->
        <% if ("mentors".equals(currentView)) { %>
        <div class="section-card">
            <div class="section-header">
                <div>
                    <h2>Mentors list</h2>
                    <p>All registered mentor profiles</p>
                </div>
                <button class="btn btn-primary" onclick="toggleForm('addMentorForm')">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                    Add mentor
                </button>
            </div>

            <div class="add-form-wrap" id="addMentorForm">
                <div class="add-form-inner">
                    <h3>New mentor</h3>
                    <form action="mentor-management" method="post">
                        <input type="hidden" name="action" value="add">
                        <div class="form-grid">
                            <div class="form-group">
                                <label for="addMentorUserId">User ID</label>
                                <input type="text" id="addMentorUserId" name="userId" placeholder="Enter user ID" required>
                            </div>
                            <div class="form-group">
                                <label for="addSpecialization">Specialization</label>
                                <input type="text" id="addSpecialization" name="specialization" placeholder="e.g. Software Engineering" required>
                            </div>
                            <div class="form-group">
                                <label for="addYearsOfExperience">Years of experience</label>
                                <input type="number" id="addYearsOfExperience" name="yearsOfExperience" placeholder="0" required>
                            </div>
                            <div class="form-group">
                                <label for="addPhoneNumberMentor">Phone number</label>
                                <input type="tel" id="addPhoneNumberMentor" name="phoneNumber" placeholder="+254..." required>
                            </div>
                            <div class="form-group">
                                <label for="addStatusMentor">Status</label>
                                <select id="addStatusMentor" name="status">
                                    <option value="Active">Active</option>
                                    <option value="Inactive">Inactive</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-grid-2">
                            <div class="form-group">
                                <label for="addExpertise">Expertise</label>
                                <textarea id="addExpertise" name="expertise" placeholder="Expertise areas" required></textarea>
                            </div>
                            <div class="form-group">
                                <label for="addBio">Bio</label>
                                <textarea id="addBio" name="bio" placeholder="Short bio" required></textarea>
                            </div>
                            <div class="form-group full">
                                <label for="addQualifications">Qualifications</label>
                                <textarea id="addQualifications" name="qualifications" placeholder="Qualifications" required></textarea>
                            </div>
                        </div>
                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">Add mentor</button>
                            <button type="button" class="btn btn-outline" onclick="toggleForm('addMentorForm')">Cancel</button>
                        </div>
                    </form>
                </div>
            </div>

            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>User ID</th>
                            <th>Specialization</th>
                            <th>Experience</th>
                            <th>Phone</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (mentors != null && !mentors.isEmpty()) {
                               for (Mentor mentor : mentors) { %>
                        <tr>
                            <td><%= mentor.getId() %></td>
                            <td><%= mentor.getUserId() %></td>
                            <td><%= mentor.getSpecialization() %></td>
                            <td><%= mentor.getYearsOfExperience() %> yrs</td>
                            <td><%= mentor.getPhoneNumber() %></td>
                            <td>
                                <span class="pill <%= "Active".equals(mentor.getStatus()) ? "pill-active" : "pill-inactive" %>">
                                    <%= mentor.getStatus() %>
                                </span>
                            </td>
                            <td>
                                <div class="td-actions">
                                    <button class="btn btn-edit" onclick="openEditModal('Mentor','<%= mentor.getId() %>',{specialization:'<%= mentor.getSpecialization() %>',expertise:'<%= mentor.getExpertise() %>',yearsOfExperience:'<%= mentor.getYearsOfExperience() %>',bio:'<%= mentor.getBio() %>',qualifications:'<%= mentor.getQualifications() %>',phoneNumber:'<%= mentor.getPhoneNumber() %>',status:'<%= mentor.getStatus() %>'})">
                                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                                        Edit
                                    </button>
                                    <form action="mentor-management" method="post" style="display:inline">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="mentorId" value="<%= mentor.getId() %>">
                                        <button type="submit" class="btn btn-delete" onclick="return confirm('Delete this mentor?')">
                                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6M14 11v6"/><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>
                                            Delete
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                        <% } } else { %>
                        <tr>
                            <td colspan="7">
                                <div class="empty-state">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>
                                    <p>No mentors found</p>
                                </div>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
        <% } %>


        <!-- ══════ MENTEES VIEW ══════ -->
        <% if ("mentees".equals(currentView)) { %>
        <div class="section-card">
            <div class="section-header">
                <div>
                    <h2>Mentees list</h2>
                    <p>All registered mentee profiles</p>
                </div>
                <button class="btn btn-primary" onclick="toggleForm('addMenteeForm')">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                    Add mentee
                </button>
            </div>

            <div class="add-form-wrap" id="addMenteeForm">
                <div class="add-form-inner">
                    <h3>New mentee</h3>
                    <form action="mentee-management" method="post">
                        <input type="hidden" name="action" value="add">
                        <div class="form-grid">
                            <div class="form-group">
                                <label for="addMenteeUserId">User ID</label>
                                <input type="text" id="addMenteeUserId" name="userId" placeholder="Enter user ID" required>
                            </div>
                            <div class="form-group">
                                <label for="addEducationLevel">Education level</label>
                                <select id="addEducationLevel" name="educationLevel" required>
                                    <option value="">Select level</option>
                                    <option value="High School">High School</option>
                                    <option value="Undergraduate">Undergraduate</option>
                                    <option value="Master">Master</option>
                                    <option value="PhD">PhD</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="addFieldOfStudy">Field of study</label>
                                <input type="text" id="addFieldOfStudy" name="fieldOfStudy" placeholder="e.g. Computer Science" required>
                            </div>
                            <div class="form-group">
                                <label for="addPhoneNumberMentee">Phone number</label>
                                <input type="tel" id="addPhoneNumberMentee" name="phoneNumber" placeholder="+254..." required>
                            </div>
                            <div class="form-group">
                                <label for="addMentorId">Mentor ID (optional)</label>
                                <input type="text" id="addMentorId" name="mentorId" placeholder="Leave blank if none">
                            </div>
                            <div class="form-group">
                                <label for="addStatusMentee">Status</label>
                                <select id="addStatusMentee" name="status">
                                    <option value="Active">Active</option>
                                    <option value="Inactive">Inactive</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group" style="margin-bottom:12px">
                            <label for="addLearningGoals">Learning goals</label>
                            <textarea id="addLearningGoals" name="learningGoals" placeholder="Describe learning goals" required></textarea>
                        </div>
                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">Add mentee</button>
                            <button type="button" class="btn btn-outline" onclick="toggleForm('addMenteeForm')">Cancel</button>
                        </div>
                    </form>
                </div>
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
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (mentees != null && !mentees.isEmpty()) {
                               for (Mentee mentee : mentees) { %>
                        <tr>
                            <td><%= mentee.getId() %></td>
                            <td><%= mentee.getUserId() %></td>
                            <td><%= mentee.getEducationLevel() %></td>
                            <td><%= mentee.getFieldOfStudy() %></td>
                            <td><%= mentee.getPhoneNumber() %></td>
                            <td>
                                <span class="pill <%= "Active".equals(mentee.getStatus()) ? "pill-active" : "pill-inactive" %>">
                                    <%= mentee.getStatus() %>
                                </span>
                            </td>
                            <td>
                                <div class="td-actions">
                                    <button class="btn btn-edit" onclick="openEditModal('Mentee','<%= mentee.getId() %>',{educationLevel:'<%= mentee.getEducationLevel() %>',fieldOfStudy:'<%= mentee.getFieldOfStudy() %>',learningGoals:'<%= mentee.getLearningGoals() %>',phoneNumber:'<%= mentee.getPhoneNumber() %>',mentorId:'<%= mentee.getMentorId() != null ? mentee.getMentorId() : "" %>',status:'<%= mentee.getStatus() %>'})">
                                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                                        Edit
                                    </button>
                                    <form action="mentee-management" method="post" style="display:inline">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="menteeId" value="<%= mentee.getId() %>">
                                        <button type="submit" class="btn btn-delete" onclick="return confirm('Delete this mentee?')">
                                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6M14 11v6"/><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>
                                            Delete
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                        <% } } else { %>
                        <tr>
                            <td colspan="7">
                                <div class="empty-state">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                                    <p>No mentees found</p>
                                </div>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
        <% } %>

    </div><!-- /content -->
</div><!-- /main -->


<!-- ══════════════════ EDIT MODAL ══════════════════ -->
<div class="modal-overlay" id="editModal">
    <div class="modal">
        <div class="modal-header">
            <h2 id="modalTitle">Edit</h2>
            <button class="modal-close" onclick="closeEditModal()" aria-label="Close">&times;</button>
        </div>
        <form id="editForm" method="post">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="entityType" id="editEntityType">
            <input type="hidden" name="id" id="editId">

            <div class="modal-body">

                <!-- User fields -->
                <div id="userFields" class="hidden">
                    <div class="form-grid-2" style="margin-bottom:12px">
                        <div class="form-group">
                            <label for="editUsername">Username</label>
                            <input type="text" name="username" id="editUsername">
                        </div>
                        <div class="form-group">
                            <label for="editEmail">Email</label>
                            <input type="email" name="email" id="editEmail">
                        </div>
                        <div class="form-group">
                            <label for="editRole">Role</label>
                            <select name="role" id="editRole">
                                <option value="mentee">Mentee</option>
                                <option value="mentor">Mentor</option>
                                <option value="admin">Admin</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="editStatusUser">Status</label>
                            <select name="status" id="editStatusUser">
                                <option value="Active">Active</option>
                                <option value="Inactive">Inactive</option>
                            </select>
                        </div>
                    </div>
                </div>

                <!-- Mentor fields -->
                <div id="mentorFields" class="hidden">
                    <div class="form-grid" style="margin-bottom:12px">
                        <div class="form-group">
                            <label for="editSpecialization">Specialization</label>
                            <input type="text" name="specialization" id="editSpecialization">
                        </div>
                        <div class="form-group">
                            <label for="editYearsOfExperience">Years of experience</label>
                            <input type="number" name="yearsOfExperience" id="editYearsOfExperience">
                        </div>
                        <div class="form-group">
                            <label for="editPhoneNumberMentor">Phone number</label>
                            <input type="tel" name="phoneNumber" id="editPhoneNumberMentor">
                        </div>
                        <div class="form-group">
                            <label for="editStatusMentor">Status</label>
                            <select name="status" id="editStatusMentor">
                                <option value="Active">Active</option>
                                <option value="Inactive">Inactive</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-grid-2" style="margin-bottom:12px">
                        <div class="form-group">
                            <label for="editExpertise">Expertise</label>
                            <textarea name="expertise" id="editExpertise"></textarea>
                        </div>
                        <div class="form-group">
                            <label for="editBio">Bio</label>
                            <textarea name="bio" id="editBio"></textarea>
                        </div>
                        <div class="form-group full">
                            <label for="editQualifications">Qualifications</label>
                            <textarea name="qualifications" id="editQualifications"></textarea>
                        </div>
                    </div>
                </div>

                <!-- Mentee fields -->
                <div id="menteeFields" class="hidden">
                    <div class="form-grid" style="margin-bottom:12px">
                        <div class="form-group">
                            <label for="editEducationLevel">Education level</label>
                            <select name="educationLevel" id="editEducationLevel">
                                <option value="High School">High School</option>
                                <option value="Undergraduate">Undergraduate</option>
                                <option value="Master">Master</option>
                                <option value="PhD">PhD</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="editFieldOfStudy">Field of study</label>
                            <input type="text" name="fieldOfStudy" id="editFieldOfStudy">
                        </div>
                        <div class="form-group">
                            <label for="editPhoneNumberMentee">Phone number</label>
                            <input type="tel" name="phoneNumber" id="editPhoneNumberMentee">
                        </div>
                        <div class="form-group">
                            <label for="editMentorId">Mentor ID (optional)</label>
                            <input type="text" name="mentorId" id="editMentorId">
                        </div>
                        <div class="form-group">
                            <label for="editStatusMentee">Status</label>
                            <select name="status" id="editStatusMentee">
                                <option value="Active">Active</option>
                                <option value="Inactive">Inactive</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group" style="margin-bottom:4px">
                        <label for="editLearningGoals">Learning goals</label>
                        <textarea name="learningGoals" id="editLearningGoals"></textarea>
                    </div>
                </div>

            </div><!-- /modal-body -->

            <div class="modal-footer">
                <button type="button" class="btn btn-outline" onclick="closeEditModal()">Cancel</button>
                <button type="submit" class="btn btn-primary">Save changes</button>
            </div>
        </form>
    </div>
</div>


<script>
    /* ── form toggle ── */
    function toggleForm(id) {
        var el = document.getElementById(id);
        el.classList.toggle('open');
    }

    /* ── edit modal ── */
    function openEditModal(type, id, data) {
        var modal = document.getElementById('editModal');
        var form  = document.getElementById('editForm');

        document.getElementById('modalTitle').textContent = 'Edit ' + type;
        document.getElementById('editEntityType').value = type;
        document.getElementById('editId').value = id;

        // hide all field groups
        document.getElementById('userFields').classList.add('hidden');
        document.getElementById('mentorFields').classList.add('hidden');
        document.getElementById('menteeFields').classList.add('hidden');

        if (type === 'User') {
            document.getElementById('userFields').classList.remove('hidden');
            form.action = 'user-management';
            setVal('editUsername', data.username);
            setVal('editEmail', data.email);
            setVal('editRole', data.role);
            setVal('editStatusUser', data.status);
        } else if (type === 'Mentor') {
            document.getElementById('mentorFields').classList.remove('hidden');
            form.action = 'mentor-management';
            setVal('editSpecialization', data.specialization);
            setVal('editYearsOfExperience', data.yearsOfExperience);
            setVal('editPhoneNumberMentor', data.phoneNumber);
            setVal('editStatusMentor', data.status);
            setVal('editExpertise', data.expertise);
            setVal('editBio', data.bio);
            setVal('editQualifications', data.qualifications);
        } else if (type === 'Mentee') {
            document.getElementById('menteeFields').classList.remove('hidden');
            form.action = 'mentee-management';
            setVal('editEducationLevel', data.educationLevel);
            setVal('editFieldOfStudy', data.fieldOfStudy);
            setVal('editPhoneNumberMentee', data.phoneNumber);
            setVal('editMentorId', data.mentorId);
            setVal('editStatusMentee', data.status);
            setVal('editLearningGoals', data.learningGoals);
        }

        modal.classList.add('open');
    }

    function setVal(id, val) {
        var el = document.getElementById(id);
        if (!el || val === undefined || val === null) return;
        el.value = val;
    }

    function closeEditModal() {
        document.getElementById('editModal').classList.remove('open');
    }

    window.addEventListener('click', function(e) {
        var modal = document.getElementById('editModal');
        if (e.target === modal) closeEditModal();
    });
</script>

</body>
</html>
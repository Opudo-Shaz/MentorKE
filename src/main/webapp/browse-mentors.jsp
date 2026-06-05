<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="app.model.Mentor" %>
<%
    String ctx = request.getContextPath();
    String username = (String) session.getAttribute("username");
    if (username == null) username = "Mentee";

    String selectedSpecialization = (String) request.getAttribute("selectedSpecialization");
    Integer selectedMinYearsOfExperience = (Integer) request.getAttribute("selectedMinYearsOfExperience");
    String selectedAvailability = (String) request.getAttribute("selectedAvailability");
    String selectedLocation = (String) request.getAttribute("selectedLocation");
    Double selectedMinRating = (Double) request.getAttribute("selectedMinRating");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage   = (String) request.getAttribute("errorMessage");
    List<Mentor> mentors  = (List<Mentor>) request.getAttribute("mentors");
    int mentorCount = (mentors != null) ? mentors.size() : 0;

    String minYearsValue = selectedMinYearsOfExperience != null ? String.valueOf(selectedMinYearsOfExperience) : "";
    String minRatingValue = selectedMinRating != null ? String.valueOf(selectedMinRating) : "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Browse Mentors – MentorKE</title>
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
        .logo { display: flex; align-items: center; gap: 10px; }
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
            color: rgba(255,255,255,0.72); font-size: 14px; font-weight: 400;
            cursor: pointer; text-decoration: none; margin-bottom: 2px;
            transition: background 0.15s, color 0.15s;
        }
        .nav-link svg { flex-shrink: 0; width: 18px; height: 18px; }
        .nav-link:hover  { background: rgba(255,255,255,0.1);  color: var(--white); }
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
            color: rgba(255,255,255,0.75); font-size: 13px;
            font-family: 'DM Sans', sans-serif;
            cursor: pointer; text-decoration: none;
            transition: background 0.15s;
        }
        .btn-logout:hover { background: rgba(255,255,255,0.16); color: var(--white); }

        /* ── MAIN ── */
        .main { margin-left: var(--sidebar-w); flex: 1; min-height: 100vh; display: flex; flex-direction: column; }

        /* ── TOPBAR ── */
        .topbar {
            height: 60px;
            background: var(--white);
            border-bottom: 1px solid var(--gray-200);
            display: flex; align-items: center; justify-content: space-between;
            padding: 0 28px;
            position: sticky; top: 0; z-index: 40;
        }
        .topbar h1 { font-size: 17px; font-weight: 600; color: var(--gray-800); }
        .topbar p  { font-size: 12px; color: var(--gray-400); margin-top: 1px; }
        .back-link {
            display: inline-flex; align-items: center; gap: 6px;
            font-size: 13px; font-weight: 500; color: var(--blue-800);
            text-decoration: none;
            padding: 6px 12px; border: 1px solid var(--blue-100);
            border-radius: var(--radius-md); background: var(--blue-50);
            transition: background 0.15s;
        }
        .back-link:hover { background: var(--blue-100); }
        .back-link svg { width: 15px; height: 15px; }

        /* ── CONTENT ── */
        .content { padding: 24px 28px; flex: 1; }

        /* ── ALERTS ── */
        .alert {
            display: flex; align-items: center; gap: 10px;
            padding: 12px 16px; border-radius: var(--radius-md);
            font-size: 14px; margin-bottom: 20px;
        }
        .alert svg { width: 18px; height: 18px; flex-shrink: 0; }
        .alert-success { background: var(--green-50);  color: var(--green-700); border: 1px solid var(--green-200); }
        .alert-error   { background: var(--red-50);    color: var(--red-700);   border: 1px solid var(--red-200); }

        /* ── FILTER CARD ── */
        .card {
            background: var(--white);
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-lg);
            overflow: hidden;
            box-shadow: var(--shadow-sm);
        }
        .card-header {
            padding: 14px 20px;
            border-bottom: 1px solid var(--gray-200);
            display: flex; align-items: center; justify-content: space-between;
        }
        .card-header h2  { font-size: 15px; font-weight: 600; color: var(--gray-800); }
        .card-header p   { font-size: 12px; color: var(--gray-400); margin-top: 1px; }
        .card-body { padding: 20px; }

        /* count badge in header */
        .count-badge {
            display: inline-flex; align-items: center;
            padding: 3px 10px; border-radius: 20px;
            font-size: 12px; font-weight: 600;
            background: var(--blue-50); color: var(--blue-800);
        }

        /* ── FILTER BAR ── */
        .filter-bar { display: flex; gap: 10px; flex-wrap: wrap; }
        .filter-input {
            flex: 1; min-width: 180px;
            padding: 9px 14px;
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-md);
            font-size: 14px; font-family: 'DM Sans', sans-serif;
            color: var(--gray-800); background: var(--gray-50);
            transition: border-color 0.15s, box-shadow 0.15s;
            outline: none;
        }
        .filter-input:focus {
            border-color: var(--blue-200);
            box-shadow: 0 0 0 3px rgba(13,71,161,0.08);
            background: var(--white);
        }
        .filter-input::placeholder { color: var(--gray-400); }
        .btn-primary {
            display: inline-flex; align-items: center; gap: 7px;
            padding: 9px 16px; background: var(--blue-800); color: var(--white);
            border: none; border-radius: var(--radius-md);
            font-size: 14px; font-weight: 500; font-family: 'DM Sans', sans-serif;
            cursor: pointer; text-decoration: none;
            transition: background 0.15s;
        }
        .btn-primary:hover { background: var(--blue-700); }
        .btn-primary svg { width: 16px; height: 16px; }
        .btn-secondary {
            display: inline-flex; align-items: center; gap: 7px;
            padding: 9px 16px;
            background: var(--white); color: var(--gray-800);
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-md);
            font-size: 14px; font-weight: 500; font-family: 'DM Sans', sans-serif;
            cursor: pointer; text-decoration: none;
            transition: background 0.15s;
        }
        .btn-secondary:hover { background: var(--gray-50); }
        .btn-secondary svg { width: 16px; height: 16px; }

        .top-rated-badge {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 3px 8px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 600;
            background: var(--amber-50);
            color: var(--amber-700);
            border: 1px solid var(--amber-200);
        }

        /* ── MENTOR GRID ── */
        .mentor-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 16px;
            margin-top: 20px;
        }
        .mentor-card {
            background: var(--white);
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-lg);
            padding: 20px;
            box-shadow: var(--shadow-sm);
            display: flex; flex-direction: column; gap: 14px;
            transition: border-color 0.15s, box-shadow 0.15s;
        }
        .mentor-card:hover {
            border-color: var(--blue-200);
            box-shadow: 0 4px 16px rgba(13,71,161,0.08);
        }

        .mentor-card-top { display: flex; align-items: center; gap: 12px; }
        .mentor-avatar {
            width: 46px; height: 46px; border-radius: 50%;
            background: var(--blue-800);
            display: flex; align-items: center; justify-content: center;
            font-size: 16px; font-weight: 600; color: var(--white);
            flex-shrink: 0;
        }
        .mentor-name { font-size: 15px; font-weight: 600; color: var(--gray-800); }
        .mentor-spec { font-size: 13px; color: var(--gray-400); margin-top: 2px; }

        .mentor-meta { display: flex; flex-direction: column; gap: 6px; }
        .mentor-meta-row {
            display: flex; align-items: center; gap: 8px;
            font-size: 13px; color: var(--gray-600);
        }
        .mentor-meta-row svg { width: 15px; height: 15px; color: var(--gray-400); flex-shrink: 0; }

        .divider { height: 1px; background: var(--gray-100); }

        .mentor-card-actions { display: flex; gap: 8px; }
        .btn-card-primary {
            flex: 1; display: inline-flex; align-items: center; justify-content: center; gap: 6px;
            padding: 8px 12px; background: var(--blue-800); color: var(--white);
            border: none; border-radius: var(--radius-md);
            font-size: 13px; font-weight: 500; font-family: 'DM Sans', sans-serif;
            cursor: pointer; text-decoration: none;
            transition: background 0.15s;
        }
        .btn-card-primary:hover { background: var(--blue-700); }
        .btn-card-secondary {
            flex: 1; display: inline-flex; align-items: center; justify-content: center; gap: 6px;
            padding: 8px 12px;
            background: var(--white); color: var(--gray-800);
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-md);
            font-size: 13px; font-weight: 500; font-family: 'DM Sans', sans-serif;
            cursor: pointer; text-decoration: none;
            transition: background 0.15s;
        }
        .btn-card-secondary:hover { background: var(--gray-50); }

        /* status pill */
        .pill {
            display: inline-flex; align-items: center; gap: 4px;
            padding: 2px 8px; border-radius: 20px;
            font-size: 11px; font-weight: 500;
        }
        .pill::before { content: ''; width: 5px; height: 5px; border-radius: 50%; }
        .pill-active   { background: var(--green-50); color: var(--green-700); }
        .pill-active::before { background: var(--green-700); }
        .pill-inactive { background: var(--amber-50); color: var(--amber-700); }
        .pill-inactive::before { background: var(--amber-700); }

        /* ── EMPTY STATE ── */
        .empty-state {
            text-align: center; padding: 48px 20px; color: var(--gray-400);
        }
        .empty-state svg { width: 40px; height: 40px; margin: 0 auto 12px; display: block; opacity: 0.3; }
        .empty-state h3 { font-size: 16px; font-weight: 600; color: var(--gray-600); margin-bottom: 6px; }
        .empty-state p  { font-size: 14px; }
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
            <h1>Browse Mentors</h1>
            <p>Find and request mentors matched to your specialization</p>
        </div>
        <a href="<%= ctx %>/app/mentee-dashboard/" class="back-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg>
            Back to dashboard
        </a>
    </div>

    <div class="content">

        <!-- Alerts -->
        <% if (successMessage != null && !successMessage.isEmpty()) { %>
        <div class="alert alert-success">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
            <%= successMessage %>
        </div>
        <% } %>
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="alert alert-error">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            <%= errorMessage %>
        </div>
        <% } %>

        <!-- Filter + results card -->
        <div class="card">
            <div class="card-header">
                <div>
                    <h2>Available mentors</h2>
                    <p>Showing mentors matched to your specialization when available</p>
                </div>
                <span class="count-badge"><%= mentorCount %> mentor<%= mentorCount != 1 ? "s" : "" %></span>
            </div>
            <div class="card-body">

                <!-- Filter bar -->
                <form class="filter-bar" method="get" action="<%= ctx %>/app/mentee-sessions/browse">
                    <input
                        class="filter-input"
                        type="text"
                        name="specialization"
                        value="<%= selectedSpecialization != null ? selectedSpecialization : "" %>"
                        placeholder="Specialization"
                    >
                    <input
                        class="filter-input"
                        type="number"
                        min="0"
                        name="minYearsOfExperience"
                        value="<%= minYearsValue %>"
                        placeholder="Min years"
                    >
                    <input
                        class="filter-input"
                        type="text"
                        name="availability"
                        value="<%= selectedAvailability != null ? selectedAvailability : "" %>"
                        placeholder="Availability (e.g. Weekends)"
                    >
                    <input
                        class="filter-input"
                        type="text"
                        name="location"
                        value="<%= selectedLocation != null ? selectedLocation : "" %>"
                        placeholder="Location"
                    >
                    <input
                        class="filter-input"
                        type="number"
                        min="0"
                        max="5"
                        step="0.1"
                        name="minRating"
                        value="<%= minRatingValue %>"
                        placeholder="Min rating"
                    >
                    <button class="btn-primary" type="submit">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                        Filter
                    </button>
                    <a class="btn-secondary" href="<%= ctx %>/app/mentee-sessions/browse">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 .49-3.88"/></svg>
                        Show all
                    </a>
                </form>

                <!-- Mentor grid -->
                <% if (mentors != null && !mentors.isEmpty()) { %>
                <div class="mentor-grid">
                    <% for (int i = 0; i < mentors.size(); i++) {
                        Mentor mentor = mentors.get(i);
                        String initials = (mentor.getUsername() != null && !mentor.getUsername().isEmpty())
                            ? mentor.getUsername().substring(0,1).toUpperCase() : "M";
                        String spec = mentor.getSpecialization() != null ? mentor.getSpecialization() : "—";
                        int exp = mentor.getYearsOfExperience() != null ? mentor.getYearsOfExperience() : 0;
                        String status = mentor.getStatus() != null ? mentor.getStatus() : "Active";
                        String location = mentor.getLocation() != null && !mentor.getLocation().isBlank() ? mentor.getLocation() : "Not provided";
                        String availability = mentor.getAvailability() != null && !mentor.getAvailability().isBlank() ? mentor.getAvailability() : "Not provided";
                        double ratingValue = mentor.getAverageRating() != null ? mentor.getAverageRating() : 0.0;
                        int ratingCount = mentor.getRatingCount() != null ? mentor.getRatingCount() : 0;
                        String ratingDisplay = ratingCount > 0 ? String.format("%.1f/5 (%d)", ratingValue, ratingCount) : "Not rated yet";
                        boolean topRated = i < 3 && ratingCount > 0;
                        boolean isActive = "Active".equalsIgnoreCase(status);
                        String encodedSpec = java.net.URLEncoder.encode(spec.equals("—") ? "" : spec, java.nio.charset.StandardCharsets.UTF_8);
                    %>
                    <div class="mentor-card">
                        <div class="mentor-card-top">
                            <div class="mentor-avatar"><%= initials %></div>
                            <div>
                                <div class="mentor-name"><%= mentor.getUsername() %></div>
                                <div class="mentor-spec"><%= spec %></div>
                                <% if (topRated) { %>
                                <div style="margin-top:6px;"><span class="top-rated-badge">Top rated in results</span></div>
                                <% } %>
                            </div>
                        </div>

                        <div class="mentor-meta">
                            <div class="mentor-meta-row">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                                <%= exp %> yr<%= exp != 1 ? "s" : "" %> experience
                            </div>
                            <div class="mentor-meta-row">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>
                                <span class="pill <%= isActive ? "pill-active" : "pill-inactive" %>"><%= status %></span>
                            </div>
                            <div class="mentor-meta-row">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.24 10.24a6 6 0 0 0-8.48 0L12 10l.24.24a6 6 0 0 0 8.48 0L21 10l-.76.24z"/><path d="M4.93 19.07a10 10 0 0 1 14.14 0"/><path d="M8.46 15.54a5 5 0 0 1 7.07 0"/></svg>
                                <%= availability %>
                            </div>
                            <div class="mentor-meta-row">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13S3 17 3 10a9 9 0 1 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
                                <%= location %>
                            </div>
                            <div class="mentor-meta-row">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15 9 22 9 17 14 19 21 12 17 5 21 7 14 2 9 9 9 12 2"/></svg>
                                <%= ratingDisplay %>
                            </div>
                        </div>

                        <div class="divider"></div>

                        <div class="mentor-card-actions">
                            <a class="btn-card-primary"
                               href="<%= ctx %>/app/mentee-sessions/request?mentorId=<%= mentor.getId() %>&specialization=<%= encodedSpec %>">
                                Request mentorship
                            </a>
                            <a class="btn-card-secondary"
                               href="<%= ctx %>/app/mentee-sessions/view-mentor?mentorId=<%= mentor.getId() %>">
                                View profile
                            </a>
                        </div>
                    </div>
                    <% } %>
                </div>

                <% } else { %>
                <div class="empty-state">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/><line x1="18" y1="8" x2="23" y2="13"/><line x1="23" y1="8" x2="18" y2="13"/></svg>
                    <h3>No mentors found</h3>
                    <p>Try a different specialization or <a href="<%= ctx %>/app/mentee-sessions/browse" style="color:var(--blue-800); text-decoration:none; font-weight:500;">show all mentors</a>.</p>
                </div>
                <% } %>

            </div>
        </div>

    </div><!-- /content -->
</div><!-- /main -->

</body>
</html>
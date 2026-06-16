<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="app.model.MenteeGoal" %>
<%@ page import="app.model.GoalMilestone" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Goals — MentorKE</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        :root {
            --blue-800: #0d47a1; --blue-600: #1976d2; --blue-100: #bbdefb;
            --blue-50: #e3f2fd;  --blue-25: #f0f7ff;
            --white: #ffffff;    --gray-50: #f8fafc;  --gray-100: #f1f5f9;
            --gray-200: #e2e8f0; --gray-400: #94a3b8; --gray-600: #475569;
            --gray-800: #1e293b; --green-50: #f0fdf4; --green-200: #bbf7d0;
            --green-700: #15803d; --amber-50: #fffbeb; --amber-700: #b45309;
            --red-50: #fef2f2;   --red-200: #fecaca;  --red-700: #b91c1c;
            --sidebar-w: 230px;  --radius-md: 8px;    --radius-lg: 12px;
            --shadow-sm: 0 1px 3px rgba(0,0,0,0.07);
        }
        body { font-family: 'DM Sans', sans-serif; background: var(--gray-50); color: var(--gray-800); min-height: 100vh; display: flex; }

        /* SIDEBAR */
        .sidebar { width: var(--sidebar-w); background: var(--blue-800); height: 100vh; position: fixed; left: 0; top: 0; display: flex; flex-direction: column; z-index: 50; }
        .sidebar-brand { padding: 22px 18px 18px; border-bottom: 1px solid rgba(255,255,255,0.12); }
        .logo { display: flex; align-items: center; gap: 10px; }
        .logo-icon { width: 34px; height: 34px; background: rgba(255,255,255,0.18); border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; }
        .logo-text { font-size: 17px; font-weight: 600; color: #fff; }
        .logo-sub  { font-size: 11px; color: rgba(255,255,255,0.5); margin-top: 1px; }
        .sidebar-nav { flex: 1; padding: 14px 10px; overflow-y: auto; }
        .nav-label { font-size: 10px; font-weight: 600; letter-spacing: 0.08em; color: rgba(255,255,255,0.4); text-transform: uppercase; padding: 12px 8px 6px; }
        .nav-link { display: flex; align-items: center; gap: 10px; padding: 9px 12px; border-radius: var(--radius-md); color: rgba(255,255,255,0.72); font-size: 14px; text-decoration: none; margin-bottom: 2px; transition: background 0.15s, color 0.15s; }
        .nav-link svg { flex-shrink: 0; width: 18px; height: 18px; }
        .nav-link:hover { background: rgba(255,255,255,0.1); color: #fff; }
        .nav-link.active { background: rgba(255,255,255,0.18); color: #fff; font-weight: 500; }
        .sidebar-footer { padding: 14px 16px; border-top: 1px solid rgba(255,255,255,0.12); }
        .sidebar-user { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
        .user-avatar { width: 34px; height: 34px; border-radius: 50%; background: rgba(255,255,255,0.2); display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 600; color: #fff; }
        .user-name { font-size: 13px; font-weight: 500; color: #fff; }
        .user-role { font-size: 11px; color: rgba(255,255,255,0.5); }
        .btn-logout { display: flex; align-items: center; justify-content: center; gap: 7px; width: 100%; padding: 8px; background: rgba(255,255,255,0.08); border: 1px solid rgba(255,255,255,0.15); border-radius: var(--radius-md); color: rgba(255,255,255,0.75); font-size: 13px; font-family: 'DM Sans', sans-serif; cursor: pointer; text-decoration: none; }
        .btn-logout:hover { background: rgba(255,255,255,0.16); color: #fff; }

        /* MAIN */
        .main { margin-left: var(--sidebar-w); flex: 1; min-height: 100vh; display: flex; flex-direction: column; }
        .topbar { height: 60px; background: var(--white); border-bottom: 1px solid var(--gray-200); display: flex; align-items: center; justify-content: space-between; padding: 0 28px; position: sticky; top: 0; z-index: 40; }
        .topbar h1 { font-size: 17px; font-weight: 600; }
        .topbar p  { font-size: 12px; color: var(--gray-400); margin-top: 1px; }
        .content { padding: 24px 28px; flex: 1; }

        /* STATS */
        .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
        .stat-card { background: var(--white); border: 1px solid var(--gray-200); border-radius: var(--radius-lg); padding: 18px 20px; display: flex; align-items: center; gap: 14px; box-shadow: var(--shadow-sm); }
        .stat-icon { width: 42px; height: 42px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
        .stat-icon svg { width: 20px; height: 20px; }
        .stat-icon.blue  { background: var(--blue-50); }
        .stat-icon.green { background: var(--green-50); }
        .stat-icon.amber { background: var(--amber-50); }
        .stat-icon.gray  { background: var(--gray-100); }
        .stat-label { font-size: 12px; color: var(--gray-400); margin-bottom: 3px; }
        .stat-value { font-size: 22px; font-weight: 600; color: var(--gray-800); line-height: 1; }

        /* CARD */
        .card { background: var(--white); border: 1px solid var(--gray-200); border-radius: var(--radius-lg); overflow: hidden; box-shadow: var(--shadow-sm); margin-bottom: 20px; }
        .card-header { padding: 14px 20px; border-bottom: 1px solid var(--gray-200); display: flex; align-items: center; justify-content: space-between; }
        .card-header h2 { font-size: 15px; font-weight: 600; }
        .card-header p  { font-size: 12px; color: var(--gray-400); margin-top: 1px; }
        .card-body { padding: 20px; }

        /* FORM */
        .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
        .form-group { display: flex; flex-direction: column; gap: 6px; }
        .form-group.full { grid-column: 1 / -1; }
        label { font-size: 12px; font-weight: 600; color: var(--gray-600); text-transform: uppercase; letter-spacing: 0.04em; }
        input[type="text"], input[type="date"], textarea {
            padding: 9px 12px; border: 1px solid var(--gray-200); border-radius: var(--radius-md);
            font-size: 14px; font-family: 'DM Sans', sans-serif; color: var(--gray-800);
            background: var(--white); transition: border-color 0.15s;
            width: 100%;
        }
        input:focus, textarea:focus { outline: none; border-color: var(--blue-600); }
        textarea { resize: vertical; min-height: 80px; }

        /* MILESTONE INPUT */
        .milestone-list { display: flex; flex-direction: column; gap: 8px; margin-top: 8px; }
        .milestone-input-row { display: flex; align-items: center; gap: 8px; }
        .milestone-input-row input { flex: 1; }
        .btn-remove-milestone { background: none; border: none; cursor: pointer; color: var(--gray-400); padding: 4px; border-radius: 4px; display: flex; align-items: center; }
        .btn-remove-milestone:hover { color: var(--red-700); background: var(--red-50); }
        .btn-add-milestone { display: inline-flex; align-items: center; gap: 6px; font-size: 13px; color: var(--blue-800); background: none; border: none; cursor: pointer; font-family: 'DM Sans', sans-serif; font-weight: 500; padding: 4px 0; margin-top: 8px; }
        .btn-add-milestone:hover { color: var(--blue-600); }

        /* BUTTONS */
        .btn-primary { display: inline-flex; align-items: center; gap: 7px; padding: 10px 22px; background: var(--blue-800); color: #fff; border: none; border-radius: var(--radius-md); font-size: 14px; font-weight: 500; font-family: 'DM Sans', sans-serif; cursor: pointer; transition: background 0.15s; }
        .btn-primary:hover { background: var(--blue-600); }
        .btn-secondary { display: inline-flex; align-items: center; gap: 7px; padding: 9px 18px; background: var(--white); color: var(--gray-800); border: 1px solid var(--gray-200); border-radius: var(--radius-md); font-size: 13px; font-weight: 500; font-family: 'DM Sans', sans-serif; cursor: pointer; transition: background 0.15s; text-decoration: none; }
        .btn-secondary:hover { background: var(--gray-50); }
        .btn-danger { display: inline-flex; align-items: center; gap: 6px; padding: 7px 14px; background: var(--red-50); color: var(--red-700); border: 1px solid var(--red-200); border-radius: var(--radius-md); font-size: 12px; font-weight: 500; font-family: 'DM Sans', sans-serif; cursor: pointer; }
        .btn-danger:hover { background: var(--red-200); }

        /* GOAL CARDS */
        .goals-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
        .goal-card { background: var(--white); border: 1px solid var(--gray-200); border-radius: var(--radius-lg); padding: 20px; box-shadow: var(--shadow-sm); transition: box-shadow 0.15s; }
        .goal-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
        .goal-card-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
        .goal-title { font-size: 15px; font-weight: 600; color: var(--gray-800); }
        .goal-desc  { font-size: 13px; color: var(--gray-400); margin-top: 3px; line-height: 1.5; }
        .goal-meta  { display: flex; align-items: center; gap: 12px; margin-top: 10px; font-size: 12px; color: var(--gray-400); }
        .goal-meta svg { width: 13px; height: 13px; }

        /* PROGRESS BAR */
        .progress-wrap { margin: 14px 0 10px; }
        .progress-label-row { display: flex; justify-content: space-between; font-size: 12px; margin-bottom: 6px; }
        .progress-pct { font-weight: 700; color: var(--blue-800); }
        .progress-track { height: 8px; background: var(--gray-100); border-radius: 10px; overflow: hidden; }
        .progress-fill { height: 100%; border-radius: 10px; background: linear-gradient(90deg, var(--blue-800), var(--blue-600)); transition: width 0.5s ease; }
        .progress-fill.done { background: linear-gradient(90deg, var(--green-700), #22c55e); }

        /* MILESTONES */
        .milestones-section { margin-top: 14px; border-top: 1px solid var(--gray-100); padding-top: 14px; }
        .milestones-title { font-size: 11px; font-weight: 600; color: var(--gray-400); text-transform: uppercase; letter-spacing: 0.06em; margin-bottom: 10px; }
        .milestone-item { display: flex; align-items: center; gap: 10px; padding: 7px 0; border-bottom: 1px solid var(--gray-100); }
        .milestone-item:last-child { border-bottom: none; }
        .milestone-checkbox { width: 18px; height: 18px; border-radius: 50%; border: 2px solid var(--gray-200); display: flex; align-items: center; justify-content: center; cursor: pointer; flex-shrink: 0; transition: all 0.15s; background: var(--white); }
        .milestone-checkbox.checked { background: var(--green-700); border-color: var(--green-700); }
        .milestone-checkbox svg { width: 10px; height: 10px; color: #fff; }
        .milestone-text { font-size: 13px; color: var(--gray-800); flex: 1; }
        .milestone-text.done { text-decoration: line-through; color: var(--gray-400); }

        /* MANUAL PROGRESS */
        .manual-progress { margin-top: 14px; border-top: 1px solid var(--gray-100); padding-top: 14px; display: flex; align-items: center; gap: 10px; }
        .manual-progress input[type="range"] { flex: 1; accent-color: var(--blue-800); }
        .manual-pct-display { font-size: 13px; font-weight: 600; color: var(--blue-800); min-width: 38px; }

        /* PILLS */
        .pill { display: inline-flex; align-items: center; gap: 4px; padding: 3px 10px; border-radius: 20px; font-size: 11px; font-weight: 500; }
        .pill-progress  { background: var(--blue-50);  color: var(--blue-800); }
        .pill-completed { background: var(--green-50); color: var(--green-700); }
        .pill-abandoned { background: var(--gray-100); color: var(--gray-600); }

        /* EMPTY */
        .empty-state { text-align: center; padding: 48px 20px; color: var(--gray-400); }
        .empty-state svg { width: 48px; height: 48px; margin: 0 auto 16px; display: block; opacity: 0.3; }
        .empty-state h3 { font-size: 16px; font-weight: 600; color: var(--gray-600); margin-bottom: 6px; }
        .empty-state p  { font-size: 14px; margin-bottom: 20px; }

        /* ALERT */
        .alert { display: flex; align-items: center; gap: 10px; padding: 12px 16px; border-radius: var(--radius-md); font-size: 14px; margin-bottom: 20px; }
        .alert-success { background: var(--green-50); color: var(--green-700); border: 1px solid var(--green-200); }
        .alert-error   { background: var(--red-50);   color: var(--red-700);   border: 1px solid var(--red-200); }
        .alert.hidden  { display: none; }

        /* MODAL OVERLAY */
        .modal-overlay { display: none; position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100; align-items: center; justify-content: center; }
        .modal-overlay.open { display: flex; }
        .modal { background: var(--white); border-radius: var(--radius-lg); padding: 28px; width: 100%; max-width: 560px; max-height: 90vh; overflow-y: auto; box-shadow: 0 20px 60px rgba(0,0,0,0.15); }
        .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .modal-header h3 { font-size: 17px; font-weight: 600; }
        .modal-close { background: none; border: none; cursor: pointer; color: var(--gray-400); padding: 4px; border-radius: 4px; }
        .modal-close:hover { color: var(--gray-800); background: var(--gray-100); }
        .modal-footer { display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px; }

        /* BACK LINK */
        .back-link { display: inline-flex; align-items: center; gap: 6px; font-size: 14px; color: var(--gray-600); text-decoration: none; margin-bottom: 20px; }
        .back-link:hover { color: var(--blue-800); }
    </style>
</head>
<body>

<%
    String username = (String) session.getAttribute("username");
    if (username == null) username = "Mentee";
    String menteeIdStr = (String) session.getAttribute("userId");

    @SuppressWarnings("unchecked")
    List<MenteeGoal> goals = (List<MenteeGoal>) request.getAttribute("goals");

    String successMsg = (String) request.getAttribute("success");
    String errorMsg   = (String) request.getAttribute("error");

    int totalGoals     = goals != null ? goals.size() : 0;
    long completedGoals = goals != null ? goals.stream().filter(g -> "COMPLETED".equals(g.getStatus())).count() : 0;
    long inProgress    = totalGoals - completedGoals;
    int avgProg = goals != null && !goals.isEmpty()
        ? goals.stream().mapToInt(MenteeGoal::getOverallProgress).sum() / goals.size()
        : 0;

    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
%>

<!-- SIDEBAR -->
<aside class="sidebar">
    <div class="sidebar-brand">
        <div class="logo">
            <div class="logo-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
            </div>
            <div><div class="logo-text">MentorKE</div><div class="logo-sub">Mentee Portal</div></div>
        </div>
    </div>
    <nav class="sidebar-nav">
        <div class="nav-label">Menu</div>
        <a href="<%= request.getContextPath() %>/app/mentee-dashboard/" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>
            Dashboard
        </a>
        <a href="<%= request.getContextPath() %>/app/mentee-goals/" class="nav-link active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            My Goals
        </a>
        <a href="<%= request.getContextPath() %>/app/mentee-analytics/" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><path d="M7 15l3-3 3 2 4-5"/></svg>
            My Progress
        </a>
        <a href="<%= request.getContextPath() %>/app/home/" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
            Home
        </a>
    </nav>
    <div class="sidebar-footer">
        <div class="sidebar-user">
            <div class="user-avatar"><%= username.substring(0,1).toUpperCase() %></div>
            <div><div class="user-name"><%= username %></div><div class="user-role">Mentee</div></div>
        </div>
        <a href="<%= request.getContextPath() %>/app/login/?action=logout" class="btn-logout">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
            Sign out
        </a>
    </div>
</aside>

<!-- MAIN -->
<div class="main">
    <div class="topbar">
        <div><h1>My Goals</h1><p>Set goals, track milestones, reach 100%</p></div>
        <button class="btn-primary" onclick="openModal()">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
            New Goal
        </button>
    </div>

    <div class="content">

        <a href="<%= request.getContextPath() %>/app/mentee-dashboard/" class="back-link">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg>
            Back to dashboard
        </a>

        <!-- ALERTS -->
        <div id="alertSuccess" class="alert alert-success <%= successMsg != null ? "" : "hidden" %>">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
            <%= successMsg != null ? successMsg : "" %>
        </div>
        <div id="alertError" class="alert alert-error <%= errorMsg != null ? "" : "hidden" %>">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            <%= errorMsg != null ? errorMsg : "" %>
        </div>

        <!-- STATS -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon blue">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#1565c0" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <div><div class="stat-label">Total goals</div><div class="stat-value"><%= totalGoals %></div></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon green">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#15803d" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                </div>
                <div><div class="stat-label">Completed</div><div class="stat-value"><%= completedGoals %></div></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon amber">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#b45309" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><path d="M7 15l3-3 3 2 4-5"/></svg>
                </div>
                <div><div class="stat-label">In progress</div><div class="stat-value"><%= inProgress %></div></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon gray">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#475569" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <div><div class="stat-label">Avg progress</div><div class="stat-value"><%= avgProg %>%</div></div>
            </div>
        </div>

        <!-- GOALS LIST -->
        <% if (goals == null || goals.isEmpty()) { %>
        <div class="card">
            <div class="card-body">
                <div class="empty-state">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                    <h3>No goals yet</h3>
                    <p>Set your first goal to start tracking your progress.</p>
                    <button class="btn-primary" onclick="openModal()">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                        Set my first goal
                    </button>
                </div>
            </div>
        </div>
        <% } else { %>

        <div class="goals-grid">
            <% for (MenteeGoal goal : goals) {
                   boolean done = "COMPLETED".equals(goal.getStatus());
                   List<GoalMilestone> ms = goal.getMilestones();
                   long doneMs = ms != null ? ms.stream().filter(GoalMilestone::isCompleted).count() : 0;
            %>
            <div class="goal-card" id="goal-<%= goal.getId() %>">

                <!-- Header -->
                <div class="goal-card-header">
                    <div>
                        <div class="goal-title"><%= goal.getTitle() %></div>
                        <% if (goal.getDescription() != null && !goal.getDescription().isEmpty()) { %>
                        <div class="goal-desc"><%= goal.getDescription() %></div>
                        <% } %>
                    </div>
                    <span class="pill <%= done ? "pill-completed" : "pill-progress" %>">
                        <%= done ? "Completed" : "In progress" %>
                    </span>
                </div>

                <!-- Meta -->
                <div class="goal-meta">
                    <% if (goal.getTargetDate() != null) { %>
                    <span style="display:flex; align-items:center; gap:4px;">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                        Target: <%= goal.getTargetDate().format(dateFmt) %>
                    </span>
                    <% } %>
                    <% if (ms != null && !ms.isEmpty()) { %>
                    <span><%= doneMs %>/<%= ms.size() %> milestones</span>
                    <% } %>
                </div>

                <!-- Progress bar -->
                <div class="progress-wrap">
                    <div class="progress-label-row">
                        <span style="font-size:12px; color:var(--gray-600);">Progress</span>
                        <span class="progress-pct" id="pct-<%= goal.getId() %>"><%= goal.getOverallProgress() %>%</span>
                    </div>
                    <div class="progress-track">
                        <div class="progress-fill <%= done ? "done" : "" %>"
                             id="bar-<%= goal.getId() %>"
                             style="width:<%= goal.getOverallProgress() %>%;"></div>
                    </div>
                </div>

                <!-- Milestones -->
                <% if (ms != null && !ms.isEmpty()) { %>
                <div class="milestones-section">
                    <div class="milestones-title">Milestones</div>
                    <% for (GoalMilestone m : ms) { %>
                    <div class="milestone-item">
                        <div class="milestone-checkbox <%= m.isCompleted() ? "checked" : "" %>"
                             onclick="toggleMilestone(<%= goal.getId() %>, <%= m.getId() %>, <%= m.isCompleted() %>)"
                             title="<%= m.isCompleted() ? "Mark incomplete" : "Mark complete" %>">
                            <% if (m.isCompleted()) { %>
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                            <% } %>
                        </div>
                        <span class="milestone-text <%= m.isCompleted() ? "done" : "" %>"><%= m.getTitle() %></span>
                    </div>
                    <% } %>
                </div>
                <% } %>

                <!-- Manual progress slider (only if no milestones or want to override) -->
                <% if (ms == null || ms.isEmpty()) { %>
                <div class="manual-progress">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="var(--gray-400)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><path d="M7 15l3-3 3 2 4-5"/></svg>
                    <input type="range" min="0" max="100" value="<%= goal.getOverallProgress() %>"
                           oninput="updateSlider(<%= goal.getId() %>, this.value)"
                           onchange="saveProgress(<%= goal.getId() %>, this.value)"
                           style="flex:1;">
                    <span class="manual-pct-display" id="slider-pct-<%= goal.getId() %>"><%= goal.getOverallProgress() %>%</span>
                </div>
                <% } %>

                <!-- Card actions -->
                <div style="display:flex; gap:8px; margin-top:14px; border-top:1px solid var(--gray-100); padding-top:14px;">
                    <button class="btn-secondary" style="font-size:12px; padding:6px 12px;"
                            onclick="openLogModal(<%= goal.getId() %>)">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                        Log note
                    </button>
                    <button class="btn-danger" style="font-size:12px; padding:6px 12px; margin-left:auto;"
                            onclick="deleteGoal(<%= goal.getId() %>)">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14H6L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4h6v2"/></svg>
                        Delete
                    </button>
                </div>

            </div>
            <% } %>
        </div>
        <% } %>

    </div>
</div>

<!-- ══ CREATE GOAL MODAL ══ -->
<div class="modal-overlay" id="createModal">
    <div class="modal">
        <div class="modal-header">
            <h3>Set a new goal</h3>
            <button class="modal-close" onclick="closeModal()">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
        </div>

        <form id="goalForm" onsubmit="submitGoal(event)">
            <div class="form-grid">
                <div class="form-group full">
                    <label>Goal title *</label>
                    <input type="text" id="goalTitle" placeholder="e.g. Learn Data Structures" required>
                </div>
                <div class="form-group full">
                    <label>Description</label>
                    <textarea id="goalDesc" placeholder="What do you want to achieve?"></textarea>
                </div>
                <div class="form-group">
                    <label>Target date</label>
                    <input type="date" id="goalDate">
                </div>
            </div>

            <!-- Milestones -->
            <div style="margin-top:20px;">
                <label style="display:block; margin-bottom:8px;">Milestones (optional)</label>
                <div class="milestone-list" id="milestoneList">
                    <div class="milestone-input-row">
                        <input type="text" placeholder="e.g. Complete chapter 1">
                        <button type="button" class="btn-remove-milestone" onclick="removeMilestone(this)">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                        </button>
                    </div>
                </div>
                <button type="button" class="btn-add-milestone" onclick="addMilestone()">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                    Add milestone
                </button>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn-secondary" onclick="closeModal()">Cancel</button>
                <button type="submit" class="btn-primary">Save goal</button>
            </div>
        </form>
    </div>
</div>

<!-- ══ LOG NOTE MODAL ══ -->
<div class="modal-overlay" id="logModal">
    <div class="modal" style="max-width:400px;">
        <div class="modal-header">
            <h3>Log a progress note</h3>
            <button class="modal-close" onclick="closeLogModal()">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
        </div>
        <input type="hidden" id="logGoalId">
        <div class="form-group" style="margin-bottom:14px;">
            <label>Note</label>
            <textarea id="logNote" placeholder="What did you work on today?"></textarea>
        </div>
        <div class="form-group">
            <label>Progress % at this point</label>
            <input type="number" id="logProgress" min="0" max="100" value="0">
        </div>
        <div class="modal-footer">
            <button type="button" class="btn-secondary" onclick="closeLogModal()">Cancel</button>
            <button type="button" class="btn-primary" onclick="submitLog()">Save note</button>
        </div>
    </div>
</div>

<script>
    const CTX = '<%= request.getContextPath() %>';
    const TOKEN = localStorage.getItem('token') || '';

    // ── Modal ──────────────────────────────────────────────
    function openModal()    { document.getElementById('createModal').classList.add('open'); }
    function closeModal()   { document.getElementById('createModal').classList.remove('open'); }
    function openLogModal(goalId) {
        document.getElementById('logGoalId').value = goalId;
        document.getElementById('logNote').value   = '';
        document.getElementById('logModal').classList.add('open');
    }
    function closeLogModal() { document.getElementById('logModal').classList.remove('open'); }

    // ── Milestone inputs ───────────────────────────────────
    function addMilestone() {
        const list = document.getElementById('milestoneList');
        const row  = document.createElement('div');
        row.className = 'milestone-input-row';
        row.innerHTML = `
            <input type="text" placeholder="Milestone title">
            <button type="button" class="btn-remove-milestone" onclick="removeMilestone(this)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>`;
        list.appendChild(row);
    }
    function removeMilestone(btn) {
        btn.closest('.milestone-input-row').remove();
    }

    // ── Create goal ────────────────────────────────────────
    function submitGoal(e) {
        e.preventDefault();
        const title = document.getElementById('goalTitle').value.trim();
        if (!title) return;

        const milestoneInputs = document.querySelectorAll('#milestoneList input[type="text"]');
        const milestones = Array.from(milestoneInputs)
            .map(i => i.value.trim())
            .filter(v => v.length > 0);

        const body = {
            title:       title,
            description: document.getElementById('goalDesc').value.trim(),
            targetDate:  document.getElementById('goalDate').value || null,
            milestones:  milestones
        };

        fetch(CTX + '/api/goals', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + TOKEN },
            body: JSON.stringify(body)
        })
        .then(r => {
            if (r.ok) { closeModal(); location.reload(); }
            else showAlert('error', 'Failed to save goal. Please try again.');
        })
        .catch(() => showAlert('error', 'Network error. Please try again.'));
    }

    // ── Toggle milestone ───────────────────────────────────
    function toggleMilestone(goalId, milestoneId, currentlyDone) {
        const action = currentlyDone ? 'uncomplete' : 'complete';
        fetch(`${CTX}/api/goals/${goalId}/milestones/${milestoneId}/${action}`, {
            method: 'PATCH',
            headers: { 'Authorization': 'Bearer ' + TOKEN }
        })
        .then(r => r.json())
        .then(goal => {
            updateProgressUI(goalId, goal.overallProgress, goal.status === 'COMPLETED');
            location.reload();
        })
        .catch(() => showAlert('error', 'Could not update milestone.'));
    }

    // ── Manual slider ──────────────────────────────────────
    function updateSlider(goalId, value) {
        document.getElementById('slider-pct-' + goalId).textContent = value + '%';
        document.getElementById('pct-' + goalId).textContent = value + '%';
        document.getElementById('bar-' + goalId).style.width = value + '%';
    }

    function saveProgress(goalId, value) {
        fetch(`${CTX}/api/goals/${goalId}/progress`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + TOKEN },
            body: JSON.stringify({ progress: parseInt(value), note: '' })
        })
        .then(r => r.json())
        .then(goal => {
            updateProgressUI(goalId, goal.overallProgress, goal.status === 'COMPLETED');
            if (goal.status === 'COMPLETED') showAlert('success', '🎉 Goal completed!');
        })
        .catch(() => showAlert('error', 'Could not save progress.'));
    }

    // ── Log note ───────────────────────────────────────────
    function submitLog() {
        const goalId   = document.getElementById('logGoalId').value;
        const note     = document.getElementById('logNote').value.trim();
        const progress = parseInt(document.getElementById('logProgress').value) || 0;

        fetch(`${CTX}/api/goals/${goalId}/progress`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + TOKEN },
            body: JSON.stringify({ progress, note })
        })
        .then(r => { if (r.ok) { closeLogModal(); showAlert('success', 'Note saved!'); } })
        .catch(() => showAlert('error', 'Could not save note.'));
    }

    // ── Delete goal ────────────────────────────────────────
    function deleteGoal(goalId) {
        if (!confirm('Delete this goal? This cannot be undone.')) return;
        fetch(`${CTX}/api/goals/${goalId}`, {
            method: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + TOKEN }
        })
        .then(r => { if (r.ok) { document.getElementById('goal-' + goalId).remove(); showAlert('success', 'Goal deleted.'); } })
        .catch(() => showAlert('error', 'Could not delete goal.'));
    }

    // ── Helpers ────────────────────────────────────────────
    function updateProgressUI(goalId, pct, done) {
        const bar  = document.getElementById('bar-' + goalId);
        const pctEl = document.getElementById('pct-' + goalId);
        if (bar)   { bar.style.width = pct + '%'; if (done) bar.classList.add('done'); }
        if (pctEl) pctEl.textContent = pct + '%';
    }

    function showAlert(type, msg) {
        const el = document.getElementById(type === 'success' ? 'alertSuccess' : 'alertError');
        el.querySelector('svg ~ *') || (el.innerHTML += msg);
        el.classList.remove('hidden');
        setTimeout(() => el.classList.add('hidden'), 4000);
    }

    // Close modals on overlay click
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', e => {
            if (e.target === overlay) overlay.classList.remove('open');
        });
    });
</script>
</body>
</html>
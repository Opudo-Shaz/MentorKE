<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="app.model.Mentee" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Progress — MentorKE</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&display=swap" rel="stylesheet">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        :root {
            --blue-800: #0d47a1; --blue-600: #1976d2; --blue-100: #bbdefb;
            --blue-50: #e3f2fd;  --blue-25: #f0f7ff;
            --white: #ffffff;    --gray-50: #f8fafc;  --gray-100: #f1f5f9;
            --gray-200: #e2e8f0; --gray-400: #94a3b8; --gray-600: #475569;
            --gray-800: #1e293b; --green-50: #f0fdf4; --green-700: #15803d;
            --amber-50: #fffbeb; --amber-700: #b45309; --red-50: #fef2f2;
            --red-700: #b91c1c;  --sidebar-w: 230px;
            --radius-md: 8px;    --radius-lg: 12px;   --shadow-sm: 0 1px 3px rgba(0,0,0,0.07);
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
        .stat-icon.teal  { background: #e0f2f1; }
        .stat-label { font-size: 12px; color: var(--gray-400); margin-bottom: 3px; }
        .stat-value { font-size: 22px; font-weight: 600; color: var(--gray-800); line-height: 1; }

        /* CARDS */
        .two-col { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px; }
        .full-col { margin-bottom: 20px; }
        .card { background: var(--white); border: 1px solid var(--gray-200); border-radius: var(--radius-lg); overflow: hidden; box-shadow: var(--shadow-sm); }
        .card-header { padding: 14px 20px; border-bottom: 1px solid var(--gray-200); display: flex; align-items: center; justify-content: space-between; }
        .card-header h2 { font-size: 15px; font-weight: 600; }
        .card-header p  { font-size: 12px; color: var(--gray-400); margin-top: 1px; }
        .card-body { padding: 20px; }

        /* PROGRESS */
        .progress-row { margin-bottom: 16px; }
        .progress-row:last-child { margin-bottom: 0; }
        .progress-label { display: flex; justify-content: space-between; font-size: 13px; font-weight: 500; margin-bottom: 7px; }
        .progress-label span:last-child { color: var(--blue-800); font-weight: 600; }
        .progress-track { height: 10px; background: var(--gray-100); border-radius: 10px; overflow: hidden; }
        .progress-fill { height: 100%; border-radius: 10px; background: linear-gradient(90deg, var(--blue-800), var(--blue-600)); transition: width 0.6s ease; }

        /* DONUT LEGEND */
        .legend { display: flex; flex-direction: column; gap: 10px; justify-content: center; }
        .legend-item { display: flex; align-items: center; gap: 10px; font-size: 13px; }
        .legend-dot { width: 12px; height: 12px; border-radius: 50%; flex-shrink: 0; }
        .legend-value { font-weight: 600; color: var(--gray-800); margin-left: auto; }

        /* BACK */
        .back-link { display: inline-flex; align-items: center; gap: 6px; font-size: 14px; color: var(--gray-600); text-decoration: none; margin-bottom: 20px; }
        .back-link:hover { color: var(--blue-800); }

        /* CHART */
        .chart-wrap { position: relative; height: 220px; }
        .chart-wrap-donut { position: relative; height: 200px; }
    </style>
</head>
<body>

<%
    String username = (String) session.getAttribute("username");
    if (username == null) username = "Mentee";

    Mentee mentee   = (Mentee) request.getAttribute("mentee");
    int completed   = request.getAttribute("completed")    != null ? (int) request.getAttribute("completed")    : 0;
    int totalHours  = request.getAttribute("totalHours")   != null ? (int) request.getAttribute("totalHours")   : 0;
    long goalsSet   = request.getAttribute("goalsSet")     != null ? (long) request.getAttribute("goalsSet")    : 0;
    long goalsDone  = request.getAttribute("goalsDone")    != null ? (long) request.getAttribute("goalsDone")   : 0;
    int avgProgress = request.getAttribute("avgProgress")  != null ? (int) request.getAttribute("avgProgress")  : 0;
    String monthlyLabels = request.getAttribute("monthlyLabels") != null ? (String) request.getAttribute("monthlyLabels") : "";
    String monthlyData   = request.getAttribute("monthlyData")   != null ? (String) request.getAttribute("monthlyData")   : "0,0,0,0,0,0";

    long goalsInProgress = goalsSet - goalsDone;
    int completionRate   = goalsSet > 0 ? (int)((goalsDone * 100) / goalsSet) : 0;
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
        <a href="<%= request.getContextPath() %>/app/mentee-analytics/" class="nav-link active">
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
        <div><h1>My Progress</h1><p>Track your mentorship journey</p></div>
    </div>

    <div class="content">

        <a href="<%= request.getContextPath() %>/app/mentee-dashboard/" class="back-link">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg>
            Back to dashboard
        </a>

        <!-- STATS -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon blue">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#1565c0" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                </div>
                <div><div class="stat-label">Sessions completed</div><div class="stat-value"><%= completed %></div></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon teal">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#0f6e56" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <div><div class="stat-label">Hours of mentorship</div><div class="stat-value"><%= totalHours %></div></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon green">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#15803d" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><path d="M7 15l3-3 3 2 4-5"/></svg>
                </div>
                <div><div class="stat-label">Goals completed</div><div class="stat-value"><%= goalsDone %>/<%= goalsSet %></div></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon amber">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#b45309" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                </div>
                <div><div class="stat-label">Overall progress</div><div class="stat-value"><%= avgProgress %>%</div></div>
            </div>
        </div>

        <!-- ROW 1: Session trend + Goal donut -->
        <div class="two-col">

            <div class="card">
                <div class="card-header">
                    <div><h2>Session activity</h2><p>Monthly sessions — last 6 months</p></div>
                </div>
                <div class="card-body">
                    <div class="chart-wrap">
                        <canvas id="lineChart"></canvas>
                    </div>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <div><h2>Goal breakdown</h2><p>Completed vs in progress</p></div>
                </div>
                <div class="card-body">
                    <div style="display:flex; align-items:center; gap:28px;">
                        <div class="chart-wrap-donut" style="flex:1;">
                            <canvas id="goalDonut"></canvas>
                        </div>
                        <div class="legend">
                            <div class="legend-item">
                                <div class="legend-dot" style="background:#15803d;"></div>
                                <span style="color:var(--gray-600);">Completed</span>
                                <span class="legend-value"><%= goalsDone %></span>
                            </div>
                            <div class="legend-item">
                                <div class="legend-dot" style="background:#1976d2;"></div>
                                <span style="color:var(--gray-600);">In progress</span>
                                <span class="legend-value"><%= goalsInProgress %></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- ROW 2: Progress bars + Summary -->
        <div class="two-col">

            <div class="card">
                <div class="card-header">
                    <div><h2>Progress overview</h2><p>Key metrics at a glance</p></div>
                </div>
                <div class="card-body">
                    <div class="progress-row">
                        <div class="progress-label"><span>Overall goal progress</span><span><%= avgProgress %>%</span></div>
                        <div class="progress-track"><div class="progress-fill" style="width:<%= avgProgress %>%;"></div></div>
                    </div>
                    <div class="progress-row">
                        <div class="progress-label"><span>Goal completion rate</span><span><%= completionRate %>%</span></div>
                        <div class="progress-track"><div class="progress-fill" style="width:<%= completionRate %>%; background:linear-gradient(90deg,#15803d,#22c55e);"></div></div>
                    </div>
                    <div class="progress-row">
                        <div class="progress-label">
                            <span>Sessions attended</span>
                            <span><%= completed %> session<%= completed != 1 ? "s" : "" %></span>
                        </div>
                        <div class="progress-track">
                            <div class="progress-fill" style="width:<%= Math.min(completed * 10, 100) %>%; background:linear-gradient(90deg,#b45309,#f59e0b);"></div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <div><h2>Journey summary</h2><p>Your mentorship in numbers</p></div>
                </div>
                <div class="card-body">
                    <% String[][] rows = {
                        {"Sessions completed", String.valueOf(completed)},
                        {"Hours mentored",     String.valueOf(totalHours)},
                        {"Goals set",          String.valueOf(goalsSet)},
                        {"Goals completed",    String.valueOf(goalsDone)},
                        {"In progress",        String.valueOf(goalsInProgress)},
                        {"Avg progress",       avgProgress + "%"},
                        {"Completion rate",    completionRate + "%"}
                    };
                    for (String[] row : rows) { %>
                    <div style="display:flex; justify-content:space-between; padding:9px 0; border-bottom:1px solid var(--gray-100); font-size:13px;">
                        <span style="color:var(--gray-400); font-weight:500;"><%= row[0] %></span>
                        <span style="font-weight:600; color:var(--gray-800);"><%= row[1] %></span>
                    </div>
                    <% } %>
                </div>
            </div>
        </div>

    </div>
</div>

<script>
    // Line chart — monthly sessions
    new Chart(document.getElementById('lineChart'), {
        type: 'line',
        data: {
            labels: '<%= monthlyLabels %>'.split(','),
            datasets: [{
                label: 'Sessions',
                data: '<%= monthlyData %>'.split(',').map(Number),
                borderColor: '#0d47a1',
                backgroundColor: 'rgba(13,71,161,0.08)',
                borderWidth: 2,
                pointBackgroundColor: '#0d47a1',
                pointRadius: 4,
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { beginAtZero: true, ticks: { stepSize: 1 }, grid: { color: '#f1f5f9' } },
                x: { grid: { display: false } }
            }
        }
    });

    // Goal donut
    new Chart(document.getElementById('goalDonut'), {
        type: 'doughnut',
        data: {
            labels: ['Completed', 'In progress'],
            datasets: [{
                data: [<%= goalsDone %>, <%= goalsInProgress %>],
                backgroundColor: ['#15803d', '#1976d2'],
                borderWidth: 0,
                hoverOffset: 6
            }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            cutout: '68%',
            plugins: { legend: { display: false } }
        }
    });
</script>
</body>
</html>
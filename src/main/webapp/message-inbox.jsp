<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.util.List" %>
<%@ page import="app.model.Message" %>
<%
    String ctx = request.getContextPath();
    HttpSession currentSession = request.getSession(false);
    String userId   = currentSession != null ? String.valueOf(currentSession.getAttribute("userId")) : null;
    String username = currentSession != null ? (String) currentSession.getAttribute("username") : "User";
    String role     = currentSession != null ? (String) currentSession.getAttribute("role") : null;
    if (username == null) username = "User";
    boolean isMentor = "mentor".equalsIgnoreCase(role);
    String dashboardUrl = isMentor ? ctx + "/app/mentor-dashboard/" : ctx + "/app/mentee-dashboard/";

    List<Message> conversations = (List<Message>) request.getAttribute("conversations");
    Integer unreadCount = (Integer) request.getAttribute("unreadCount");
    String errorMessage = (String) request.getAttribute("errorMessage");
    int unread = unreadCount != null ? unreadCount : 0;
    int convCount = conversations != null ? conversations.size() : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Messages – MentorKE</title>
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

        body { font-family: 'DM Sans', sans-serif; background: var(--gray-50); color: var(--gray-800); min-height: 100vh; display: flex; }

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
        .nav-unread { margin-left: auto; background: var(--red-700); color: var(--white); font-size: 10px; font-weight: 600; min-width: 18px; height: 18px; padding: 0 4px; border-radius: 9px; display: flex; align-items: center; justify-content: center; }
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
        .topbar-right { display: flex; align-items: center; gap: 10px; }
        .unread-badge-topbar { display: inline-flex; align-items: center; gap: 6px; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; background: var(--red-50); color: var(--red-700); border: 1px solid var(--red-200); }
        .unread-badge-topbar::before { content: ''; width: 6px; height: 6px; border-radius: 50%; background: var(--red-700); }
        .unread-zero { background: var(--green-50); color: var(--green-700); border-color: var(--green-200); }
        .unread-zero::before { background: var(--green-700); }
        .btn-outline { display: inline-flex; align-items: center; gap: 6px; font-size: 13px; font-weight: 500; color: var(--blue-800); text-decoration: none; padding: 6px 12px; border: 1px solid var(--blue-100); border-radius: var(--radius-md); background: var(--blue-50); transition: background 0.15s; }
        .btn-outline:hover { background: var(--blue-100); }
        .btn-outline svg { width: 15px; height: 15px; }

        /* ── CONTENT ── */
        .content { padding: 24px 28px; flex: 1; }

        /* ── ALERT ── */
        .alert { display: flex; align-items: center; gap: 10px; padding: 12px 16px; border-radius: var(--radius-md); font-size: 14px; margin-bottom: 20px; }
        .alert svg { width: 18px; height: 18px; flex-shrink: 0; }
        .alert-error { background: var(--red-50); color: var(--red-700); border: 1px solid var(--red-200); }

        /* ── CARD ── */
        .card { background: var(--white); border: 1px solid var(--gray-200); border-radius: var(--radius-lg); overflow: hidden; box-shadow: var(--shadow-sm); }
        .card-header { padding: 14px 20px; border-bottom: 1px solid var(--gray-200); display: flex; align-items: center; justify-content: space-between; }
        .card-header h2 { font-size: 15px; font-weight: 600; color: var(--gray-800); }
        .card-header p  { font-size: 12px; color: var(--gray-400); margin-top: 1px; }
        .count-badge { display: inline-flex; align-items: center; padding: 3px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; background: var(--blue-50); color: var(--blue-800); }

        /* ── TABLE ── */
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; }
        thead th { background: var(--blue-25); color: var(--blue-800); font-size: 12px; font-weight: 600; padding: 10px 16px; text-align: left; border-bottom: 1px solid var(--blue-100); white-space: nowrap; }
        tbody td { padding: 12px 16px; font-size: 13px; color: var(--gray-800); border-bottom: 1px solid var(--gray-100); vertical-align: middle; }
        tbody tr:last-child td { border-bottom: none; }
        tbody tr:hover td { background: var(--gray-50); cursor: pointer; }

        .partner-cell { display: flex; align-items: center; gap: 10px; }
        .partner-avatar { width: 34px; height: 34px; border-radius: 50%; background: var(--blue-800); display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 600; color: var(--white); flex-shrink: 0; }
        .partner-name { font-size: 13px; font-weight: 500; color: var(--gray-800); }
        .partner-id   { font-size: 11px; color: var(--gray-400); }
        .snippet-cell { color: var(--gray-600); max-width: 340px; line-height: 1.5; }
        .snippet-empty { color: var(--gray-400); font-style: italic; }
        .time-cell { color: var(--gray-400); font-size: 12px; white-space: nowrap; }

        .btn-open { display: inline-flex; align-items: center; gap: 5px; padding: 6px 12px; background: var(--blue-800); color: var(--white); border: none; border-radius: var(--radius-md); font-size: 12px; font-weight: 500; font-family: 'DM Sans', sans-serif; text-decoration: none; transition: background 0.15s; white-space: nowrap; }
        .btn-open:hover { background: var(--blue-700); }
        .btn-open svg { width: 13px; height: 13px; }

        /* ── EMPTY STATE ── */
        .empty-state { text-align: center; padding: 48px 20px; color: var(--gray-400); }
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
                <div class="logo-sub"><%= isMentor ? "Mentor Portal" : "Mentee Portal" %></div>
            </div>
        </div>
    </div>
    <nav class="sidebar-nav">
        <div class="nav-section-label">Menu</div>
        <a href="<%= dashboardUrl %>" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>
            Dashboard
        </a>
        <% if (!isMentor) { %>
        <a href="<%= ctx %>/app/mentee-sessions/browse" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>
            Browse Mentors
        </a>
        <a href="<%= ctx %>/app/mentee-sessions/my-requests" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            My Requests
        </a>
        <% } %>
        <a href="<%= ctx %>/app/sessions/upcoming" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            Upcoming Sessions
        </a>
        <a href="<%= ctx %>/app/sessions/completed" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
            Completed Sessions
        </a>
        <a href="<%= ctx %>/app/messaging/list-conversations" class="nav-link active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            Messages
            <% if (unread > 0) { %><span class="nav-unread"><%= unread %></span><% } %>
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
                <div class="user-role"><%= isMentor ? "Mentor" : "Mentee" %></div>
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
            <h1>Messages</h1>
            <p>Conversations for <strong style="color:var(--gray-800);"><%= username %></strong></p>
        </div>
        <div class="topbar-right">
            <span class="unread-badge-topbar <%= unread == 0 ? "unread-zero" : "" %>">
                <%= unread == 0 ? "All read" : unread + " unread" %>
            </span>
            <a href="<%= dashboardUrl %>" class="btn-outline">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg>
                Back to dashboard
            </a>
        </div>
    </div>

    <div class="content">

        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="alert alert-error">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            <%= errorMessage %>
        </div>
        <% } %>

        <div class="card">
            <div class="card-header">
                <div>
                    <h2>Recent conversations</h2>
                    <p>Click a row to open the thread</p>
                </div>
                <span class="count-badge"><%= convCount %> thread<%= convCount != 1 ? "s" : "" %></span>
            </div>

            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>Conversation partner</th>
                            <th>Last message</th>
                            <th>Time</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                    <% if (conversations != null && !conversations.isEmpty() && userId != null) {
                           for (Message message : conversations) {
                               Object partnerIdRaw = userId.equals(String.valueOf(message.getSenderId()))
                                   ? message.getRecipientId() : message.getSenderId();
                               String partnerId = String.valueOf(partnerIdRaw);
                               String partnerInitial = partnerId.length() > 0
                                   ? partnerId.substring(0, 1).toUpperCase() : "?";
                               String snippet = message.getMessage() != null
                                   ? (message.getMessage().length() > 100
                                       ? message.getMessage().substring(0, 100) + "…"
                                       : message.getMessage())
                                   : null;
                    %>
                        <tr onclick="window.location='<%= ctx %>/app/messaging/conversation?userId=<%= partnerId %>'"
                            style="cursor:pointer;">
                            <td>
                                <div class="partner-cell">
                                    <div class="partner-avatar"><%= partnerInitial %></div>
                                    <div>
                                        <div class="partner-name">User #<%= partnerId %></div>
                                        <div class="partner-id">ID: <%= partnerId %></div>
                                    </div>
                                </div>
                            </td>
                            <td class="snippet-cell">
                                <% if (snippet != null) { %>
                                    <%= snippet %>
                                <% } else { %>
                                    <span class="snippet-empty">No messages yet</span>
                                <% } %>
                            </td>
                            <td class="time-cell"><%= message.getCreatedAt() != null ? message.getCreatedAt() : "—" %></td>
                            <td>
                                <a class="btn-open"
                                   href="<%= ctx %>/app/messaging/conversation?userId=<%= partnerId %>"
                                   onclick="event.stopPropagation();">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                                    Open
                                </a>
                            </td>
                        </tr>
                    <%   }
                       } else { %>
                        <tr>
                            <td colspan="4">
                                <div class="empty-state">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                                    <h3>No conversations yet</h3>
                                    <p>When you start messaging, threads will appear here.</p>
                                </div>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>

    </div><!-- /content -->
</div><!-- /main -->

</body>
</html>
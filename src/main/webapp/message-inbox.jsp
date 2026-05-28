<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
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
    List<Map<String, Object>> conversationSummaries = (List<Map<String, Object>>) request.getAttribute("conversationSummaries");
        List<Message> selectedMessages = (List<Message>) request.getAttribute("messages");
    Integer unreadCount = (Integer) request.getAttribute("unreadCount");
    String errorMessage = (String) request.getAttribute("errorMessage");
        String selectedPartnerId = (String) request.getAttribute("selectedPartnerId");
        String selectedPartnerName = (String) request.getAttribute("selectedPartnerName");
        String selectedPartnerRole = (String) request.getAttribute("selectedPartnerRole");
        String roomId = (String) request.getAttribute("roomId");
    int unread = unreadCount != null ? unreadCount : 0;
    int convCount = conversations != null ? conversations.size() : 0;
        boolean hasChatPartner = selectedPartnerId != null && !selectedPartnerId.trim().isEmpty();
        String chatTitle = selectedPartnerName != null && !selectedPartnerName.isEmpty()
            ? selectedPartnerName
            : (hasChatPartner ? "User #" + selectedPartnerId : "Choose a conversation");
        String chatSubtitle = selectedPartnerRole != null && !selectedPartnerRole.isEmpty()
            ? selectedPartnerRole.substring(0, 1).toUpperCase() + selectedPartnerRole.substring(1)
            : (hasChatPartner ? "Conversation partner" : "Live chat will appear here");
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

        .chat-card { margin-top: 18px; }
        .chat-header { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
        .chat-meta { display: flex; flex-direction: column; gap: 3px; }
        .chat-meta h3 { font-size: 16px; font-weight: 600; color: var(--gray-800); }
        .chat-meta p { font-size: 12px; color: var(--gray-400); }
        .chat-room-tag { font-size: 12px; font-weight: 600; color: var(--blue-800); background: var(--blue-50); border: 1px solid var(--blue-100); padding: 4px 10px; border-radius: 999px; }
        .chat-body { display: grid; gap: 14px; padding: 18px 20px 20px; }
        .chat-stream {
            min-height: 320px;
            max-height: 55vh;
            overflow-y: auto;
            padding: 14px;
            background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%);
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-lg);
        }
        .chat-empty { display: grid; place-items: center; min-height: 240px; text-align: center; color: var(--gray-400); }
        .chat-empty h3 { font-size: 16px; color: var(--gray-600); margin-bottom: 6px; }
        .chat-empty p { font-size: 14px; max-width: 420px; line-height: 1.5; }
        .message-row { display: flex; width: 100%; margin-bottom: 10px; }
        .message-row.mine { justify-content: flex-end; }
        .message-row.theirs { justify-content: flex-start; }
        .bubble {
            max-width: min(78%, 560px);
            padding: 11px 14px;
            border-radius: 16px;
            border: 1px solid transparent;
            box-shadow: 0 1px 2px rgba(15, 23, 42, 0.05);
            line-height: 1.45;
            white-space: pre-wrap;
            word-break: break-word;
        }
        .bubble.mine {
            background: var(--white);
            color: var(--gray-800);
            border-color: var(--gray-200);
            border-top-right-radius: 6px;
        }
        .bubble.theirs {
            background: var(--blue-800);
            color: var(--white);
            border-top-left-radius: 6px;
        }
        .bubble-meta { margin-top: 6px; font-size: 11px; opacity: 0.75; display: flex; gap: 8px; flex-wrap: wrap; }
        .message-composer { display: flex; gap: 10px; align-items: flex-end; }
        .message-input {
            flex: 1;
            min-height: 48px;
            max-height: 120px;
            resize: vertical;
            padding: 12px 14px;
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-md);
            font: inherit;
            color: var(--gray-800);
            background: var(--white);
            outline: none;
        }
        .message-input:focus { border-color: var(--blue-200); box-shadow: 0 0 0 3px rgba(13, 71, 161, 0.08); }
        .send-btn {
            padding: 12px 18px;
            border: 0;
            border-radius: var(--radius-md);
            background: var(--blue-800);
            color: var(--white);
            font: inherit;
            font-weight: 600;
            cursor: pointer;
            white-space: nowrap;
            min-width: 110px;
        }
        .send-btn:disabled { opacity: 0.55; cursor: not-allowed; }
        .thread-active td { background: rgba(13, 71, 161, 0.05); }
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
                    <% if (conversationSummaries != null && !conversationSummaries.isEmpty() && userId != null) {
                           for (Map<String, Object> summary : conversationSummaries) {
                               String partnerId = String.valueOf(summary.get("partnerId"));
                               String partnerName = String.valueOf(summary.get("partnerName"));
                               String partnerRole = String.valueOf(summary.get("partnerRole"));
                               String snippet = summary.get("lastMessage") != null ? String.valueOf(summary.get("lastMessage")) : null;
                               Object createdAtValue = summary.get("createdAt");
                               String createdAt = createdAtValue != null ? String.valueOf(createdAtValue) : "—";
                               boolean selected = Boolean.TRUE.equals(summary.get("selected"));
                               String partnerInitial = partnerName != null && !partnerName.isEmpty()
                                   ? partnerName.substring(0, 1).toUpperCase() : "?";
                    %>
                        <tr class="<%= selected ? "thread-active" : "" %>"
                            onclick="window.location='<%= ctx %>/app/messaging/list-conversations?partnerId=<%= partnerId %>'"
                            style="cursor:pointer;">
                            <td>
                                <div class="partner-cell">
                                    <div class="partner-avatar"><%= partnerInitial %></div>
                                    <div>
                                        <div class="partner-name"><%= partnerName %></div>
                                        <div class="partner-id"><%= partnerRole != null ? partnerRole.substring(0, 1).toUpperCase() + partnerRole.substring(1) : "Partner" %></div>
                                    </div>
                                </div>
                            </td>
                            <td class="snippet-cell">
                                <% if (snippet != null && !snippet.isEmpty()) { %>
                                    <%= snippet.length() > 100 ? snippet.substring(0, 100) + "…" : snippet %>
                                <% } else { %>
                                    <span class="snippet-empty">No messages yet</span>
                                <% } %>
                            </td>
                            <td class="time-cell"><%= createdAt %></td>
                            <td>
                                <a class="btn-open"
                                              href="<%= ctx %>/app/messaging/list-conversations?partnerId=<%= partnerId %>"
                                   onclick="event.stopPropagation();">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                                    Open
                                </a>
                            </td>
                        </tr>
                    <%   }
                       } else if (conversations != null && !conversations.isEmpty() && userId != null) {
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
                        <tr class="<%= partnerId.equals(selectedPartnerId) ? "thread-active" : "" %>"
                            onclick="window.location='<%= ctx %>/app/messaging/list-conversations?partnerId=<%= partnerId %>'"
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
                                              href="<%= ctx %>/app/messaging/list-conversations?partnerId=<%= partnerId %>"
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

        <div class="card chat-card">
            <div class="card-header chat-header">
                <div class="chat-meta">
                    <h2>Live chat</h2>
                    <p><%= hasChatPartner ? chatSubtitle + " chat with " + chatTitle : "Select a mentor or mentee to start chatting" %></p>
                </div>
                <% if (hasChatPartner) { %>
                <span class="chat-room-tag">Room <%= roomId != null ? roomId : "pending" %></span>
                <% } %>
            </div>

            <div class="chat-body">
                <% if (hasChatPartner) { %>
                <div id="chatStream" class="chat-stream" data-room-id="<%= roomId != null ? roomId : "" %>" data-user-id="<%= userId %>" data-user-name="<%= username %>" data-partner-id="<%= selectedPartnerId %>">
                    <% if (selectedMessages != null && !selectedMessages.isEmpty()) {
                           for (Message message : selectedMessages) {
                               boolean mine = userId != null && userId.equals(String.valueOf(message.getSenderId()));
                    %>
                    <div class="message-row <%= mine ? "mine" : "theirs" %>" data-sender-id="<%= message.getSenderId() %>" data-recipient-id="<%= message.getRecipientId() %>">
                        <div class="bubble <%= mine ? "mine" : "theirs" %>">
                            <%= message.getMessage() %>
                            <div class="bubble-meta">
                                <span><%= mine ? "You" : (selectedPartnerName != null ? selectedPartnerName : "Partner") %></span>
                                <span><%= message.getCreatedAt() != null ? message.getCreatedAt() : "Just now" %></span>
                            </div>
                        </div>
                    </div>
                    <%   }
                       } else { %>
                    <div class="chat-empty">
                        <div>
                            <h3>No messages yet</h3>
                            <p>Start the conversation below. This live chat will save messages to the database and stream them instantly to both sides through websocket updates.</p>
                        </div>
                    </div>
                    <% } %>
                </div>

                <form id="chatForm" class="message-composer" data-send-url="<%= ctx %>/app/messaging/send-message">
                    <input type="hidden" name="recipientId" value="<%= selectedPartnerId %>">
                    <textarea id="chatMessage" name="message" class="message-input" rows="2" placeholder="Write a message to <%= chatTitle %>..." required></textarea>
                    <button id="sendButton" type="submit" class="send-btn">Send</button>
                </form>
                <% } else { %>
                <div class="chat-empty">
                    <div>
                        <h3>Pick a conversation</h3>
                        <p>Choose a thread from the left side to open a live websocket chat and continue the conversation here.</p>
                    </div>
                </div>
                <% } %>
            </div>
        </div>

    </div><!-- /content -->
</div><!-- /main -->

<script>
(() => {
    const chatStream = document.getElementById('chatStream');
    const chatForm = document.getElementById('chatForm');
    const chatMessage = document.getElementById('chatMessage');
    const sendButton = document.getElementById('sendButton');

    if (!chatStream || !chatForm || !chatMessage || !sendButton) {
        return;
    }

    const roomId = chatStream.dataset.roomId || '';
    const userId = chatStream.dataset.userId || '';
    const userName = chatStream.dataset.userName || 'You';
    const partnerId = chatStream.dataset.partnerId || '';
    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = wsProtocol + '//' + window.location.host + '<%= ctx %>/chat/' + roomId;

    let socket = null;

    function scrollToBottom() {
        chatStream.scrollTop = chatStream.scrollHeight;
    }

    function applyBubbleTheme(row, bubble, mine) {
        row.classList.toggle('mine', mine);
        row.classList.toggle('theirs', !mine);
        row.style.justifyContent = mine ? 'flex-end' : 'flex-start';

        bubble.classList.toggle('mine', mine);
        bubble.classList.toggle('theirs', !mine);
        bubble.style.background = mine ? 'var(--white)' : 'var(--blue-800)';
        bubble.style.color = mine ? 'var(--gray-800)' : 'var(--white)';
        bubble.style.borderColor = mine ? 'var(--gray-200)' : 'transparent';
        bubble.style.borderTopLeftRadius = mine ? '16px' : '6px';
        bubble.style.borderTopRightRadius = mine ? '6px' : '16px';
    }

    function normalizeExistingMessages() {
        chatStream.querySelectorAll('.message-row').forEach((row) => {
            const bubble = row.querySelector('.bubble');
            if (!bubble) {
                return;
            }

            const senderId = row.dataset.senderId || '';
            const mine = senderId && senderId === userId;
            applyBubbleTheme(row, bubble, mine);
        });
    }

    function createBubble(messageData, mine) {
        const row = document.createElement('div');
        row.className = `message-row ${mine ? 'mine' : 'theirs'}`;
        row.dataset.senderId = messageData.senderId || '';
        row.dataset.recipientId = messageData.recipientId || '';

        const bubble = document.createElement('div');
        bubble.className = `bubble ${mine ? 'mine' : 'theirs'}`;
        bubble.textContent = messageData.message || '';

        const meta = document.createElement('div');
        meta.className = 'bubble-meta';
        const author = document.createElement('span');
        author.textContent = messageData.senderName || (mine ? userName : '<%= selectedPartnerName != null ? selectedPartnerName.replace("'", "\\'") : "Partner" %>');
        const time = document.createElement('span');
        time.textContent = messageData.createdAt || 'Just now';

        meta.appendChild(author);
        meta.appendChild(time);
        bubble.appendChild(meta);
        row.appendChild(bubble);

        applyBubbleTheme(row, bubble, mine);
        return row;
    }

    function appendMessage(messageData, mine) {
        const emptyState = chatStream.querySelector('.chat-empty');
        if (emptyState) {
            emptyState.remove();
        }

        chatStream.appendChild(createBubble(messageData, mine));
        scrollToBottom();
    }

    if (roomId) {
        try {
            socket = new WebSocket(wsUrl);

            socket.onmessage = (event) => {
                try {
                    const payload = JSON.parse(event.data);
                    const messageData = payload.message || payload;
                    if (!messageData || messageData.senderId === userId) {
                        return;
                    }
                    if (payload.roomId && payload.roomId !== roomId) {
                        return;
                    }
                    appendMessage(messageData, false);
                } catch (error) {
                    appendMessage({
                        message: event.data,
                        createdAt: 'Just now',
                        senderName: '<%= selectedPartnerName != null ? selectedPartnerName.replace("'", "\\'") : "Partner" %>'
                    }, false);
                }
            };
        } catch (error) {
            console.warn('WebSocket unavailable', error);
        }
    }

    normalizeExistingMessages();

    chatForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const text = chatMessage.value.trim();
        if (!text || !partnerId) {
            return;
        }

        sendButton.disabled = true;

        try {
            const response = await fetch(chatForm.dataset.sendUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                body: new URLSearchParams({ recipientId: partnerId, message: text })
            });

            const payload = await response.json();
            if (!response.ok || !payload.success) {
                throw new Error(payload.error || 'Unable to send message');
            }

            const savedMessage = payload.message || {
                senderId: userId,
                recipientId: partnerId,
                senderName: userName,
                message: text,
                createdAt: new Date().toISOString()
            };

            appendMessage(savedMessage, true);

            if (socket && socket.readyState === WebSocket.OPEN) {
                socket.send(JSON.stringify({
                    roomId: payload.roomId || roomId,
                    message: {
                        senderId: userId,
                        senderName: userName,
                        recipientId: partnerId,
                        message: text,
                        createdAt: savedMessage.createdAt || new Date().toISOString()
                    }
                }));
            }

            chatMessage.value = '';
            chatMessage.focus();
        } catch (error) {
            console.error(error);
            alert(error.message || 'Failed to send message');
        } finally {
            sendButton.disabled = false;
        }
    });

    scrollToBottom();
})();
</script>

</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="app.model.Mentor" %>
<%@ page import="app.model.Mentee" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    HttpSession httpSession = request.getSession(false);
    String username  = (String) (httpSession != null ? httpSession.getAttribute("username")  : null);
    String userIdStr = (String) (httpSession != null ? httpSession.getAttribute("userId")    : null);
    String role      = (String) (httpSession != null ? httpSession.getAttribute("role")      : null);

    if (username == null || userIdStr == null || role == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    boolean isMentor   = "mentor".equalsIgnoreCase(role);
    String dashboardUrl = isMentor
        ? request.getContextPath() + "/app/mentor-dashboard/"
        : request.getContextPath() + "/app/mentee-dashboard/";
    String avatarLetter = username.substring(0, 1).toUpperCase();
    String ctx = request.getContextPath();
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
            height: 100vh; position: fixed; left: 0; top: 0;
            display: flex; flex-direction: column; z-index: 50;
        }
        .sidebar-brand { padding: 22px 18px 18px; border-bottom: 1px solid rgba(255,255,255,0.12); }
        .logo { display: flex; align-items: center; gap: 10px; }
        .logo-icon {
            width: 34px; height: 34px; background: rgba(255,255,255,0.18);
            border-radius: var(--radius-md);
            display: flex; align-items: center; justify-content: center;
        }
        .logo-text { font-size: 17px; font-weight: 600; color: var(--white); }
        .logo-sub  { font-size: 11px; color: rgba(255,255,255,0.5); margin-top: 1px; }

        .sidebar-nav { flex: 1; padding: 14px 10px; overflow-y: auto; }
        .nav-section-label {
            font-size: 10px; font-weight: 600; letter-spacing: 0.08em;
            color: rgba(255,255,255,0.4); text-transform: uppercase; padding: 12px 8px 6px;
        }
        .nav-link {
            display: flex; align-items: center; gap: 10px;
            padding: 9px 12px; border-radius: var(--radius-md);
            color: rgba(255,255,255,0.72); font-size: 14px; font-weight: 400;
            text-decoration: none; margin-bottom: 2px;
            transition: background 0.15s, color 0.15s;
        }
        .nav-link svg { flex-shrink: 0; width: 18px; height: 18px; }
        .nav-link:hover  { background: rgba(255,255,255,0.1);  color: var(--white); }
        .nav-link.active { background: rgba(255,255,255,0.18); color: var(--white); font-weight: 500; }

        .sidebar-footer { padding: 14px 16px; border-top: 1px solid rgba(255,255,255,0.12); }
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
            background: rgba(255,255,255,0.08); border: 1px solid rgba(255,255,255,0.15);
            border-radius: var(--radius-md); color: rgba(255,255,255,0.75);
            font-size: 13px; font-family: 'DM Sans', sans-serif;
            cursor: pointer; text-decoration: none; transition: background 0.15s;
        }
        .btn-logout:hover { background: rgba(255,255,255,0.16); color: var(--white); }

        /* ── MAIN ── */
        .main { margin-left: var(--sidebar-w); flex: 1; min-height: 100vh; display: flex; flex-direction: column; }

        /* ── TOPBAR ── */
        .topbar {
            height: 60px; background: var(--white);
            border-bottom: 1px solid var(--gray-200);
            display: flex; align-items: center; justify-content: space-between;
            padding: 0 28px; position: sticky; top: 0; z-index: 40;
        }
        .topbar h1 { font-size: 17px; font-weight: 600; color: var(--gray-800); }
        .topbar p  { font-size: 12px; color: var(--gray-400); margin-top: 1px; }

        /* ── CONTENT: full-height two-panel ── */
        .content {
            flex: 1; display: flex; gap: 0; overflow: hidden;
            height: calc(100vh - 60px);
        }

        /* ── LEFT: conversations panel ── */
        .conversations-panel {
            width: 280px; background: var(--white);
            border-right: 1px solid var(--gray-200);
            display: flex; flex-direction: column; overflow: hidden;
            flex-shrink: 0;
        }
        .conversations-header {
            padding: 14px 20px; border-bottom: 1px solid var(--gray-200);
            display: flex; align-items: center; justify-content: space-between;
        }
        .conversations-header h2 { font-size: 14px; font-weight: 600; color: var(--gray-800); }
        .conv-count {
            display: inline-flex; padding: 2px 8px; border-radius: 20px;
            font-size: 11px; font-weight: 600;
            background: var(--blue-50); color: var(--blue-800);
        }
        .conversations-list { flex: 1; overflow-y: auto; padding: 6px 0; }

        .conversation-item {
            padding: 12px 16px; cursor: pointer;
            border-left: 3px solid transparent;
            border-bottom: 1px solid var(--gray-100);
            transition: background 0.15s, border-color 0.15s;
        }
        .conversation-item:hover { background: var(--gray-50); }
        .conversation-item.active { background: var(--blue-25); border-left-color: var(--blue-800); }
        .conversation-item-top { display: flex; align-items: center; gap: 10px; margin-bottom: 4px; }
        .conv-avatar {
            width: 32px; height: 32px; border-radius: 50%;
            background: var(--blue-800);
            display: flex; align-items: center; justify-content: center;
            font-size: 12px; font-weight: 600; color: var(--white); flex-shrink: 0;
        }
        .conversation-item.active .conv-avatar { background: var(--blue-700); }
        .conversation-item-name { font-size: 13px; font-weight: 500; color: var(--gray-800); }
        .conversation-item-time { font-size: 11px; color: var(--gray-400); padding-left: 42px; }

        .no-conversations {
            padding: 32px 20px; text-align: center; color: var(--gray-400);
        }
        .no-conversations svg { width: 36px; height: 36px; margin: 0 auto 10px; display: block; opacity: 0.3; }
        .no-conversations p { font-size: 13px; }

        /* ── RIGHT: chat panel ── */
        .chat-panel { flex: 1; display: flex; flex-direction: column; background: var(--white); overflow: hidden; }

        .chat-header {
            padding: 14px 24px; border-bottom: 1px solid var(--gray-200);
            display: flex; align-items: center; gap: 12px; flex-shrink: 0;
        }
        .chat-header-avatar {
            width: 38px; height: 38px; border-radius: 50%;
            background: var(--blue-800);
            display: flex; align-items: center; justify-content: center;
            font-size: 14px; font-weight: 600; color: var(--white); flex-shrink: 0;
        }
        .chat-header-title  { font-size: 15px; font-weight: 600; color: var(--gray-800); }
        .chat-header-subtitle { font-size: 12px; color: var(--gray-400); margin-top: 2px; }

        /* status dot */
        .online-dot { display: inline-block; width: 8px; height: 8px; border-radius: 50%; background: var(--green-700); margin-right: 4px; }

        .chat-messages {
            flex: 1; overflow-y: auto;
            padding: 20px 24px;
            display: flex; flex-direction: column; gap: 4px;
            background: var(--gray-50);
        }

        .message-group { display: flex; gap: 8px; margin-bottom: 10px; }
        .message-group.own { justify-content: flex-end; }

        .message-avatar {
            width: 30px; height: 30px; border-radius: 50%;
            background: var(--blue-100);
            display: flex; align-items: center; justify-content: center;
            font-size: 11px; font-weight: 600; color: var(--blue-800); flex-shrink: 0;
            align-self: flex-end;
        }
        .message-col { display: flex; flex-direction: column; max-width: 62%; }
        .message-group.own .message-col { align-items: flex-end; }

        .message-bubble {
            padding: 10px 14px; border-radius: var(--radius-lg);
            font-size: 14px; line-height: 1.5; word-break: break-word;
        }
        .message-bubble.received {
            background: var(--white); color: var(--gray-800);
            border: 1px solid var(--gray-200);
            border-bottom-left-radius: var(--radius-sm);
        }
        .message-bubble.sent {
            background: var(--blue-800); color: var(--white);
            border-bottom-right-radius: var(--radius-sm);
        }
        .message-time { font-size: 11px; color: var(--gray-400); margin-top: 4px; padding: 0 2px; }

        .message-system {
            text-align: center; font-size: 12px; color: var(--gray-400);
            padding: 8px 0; margin: 8px 0;
        }
        .message-system span {
            background: var(--gray-100); padding: 3px 12px; border-radius: 20px;
        }

        /* ── INPUT AREA ── */
        .chat-input-area {
            padding: 14px 24px; border-top: 1px solid var(--gray-200);
            display: flex; gap: 10px; align-items: flex-end;
            background: var(--white); flex-shrink: 0;
        }
        .chat-input {
            flex: 1; padding: 10px 14px;
            border: 1px solid var(--gray-200); border-radius: var(--radius-md);
            font-family: 'DM Sans', sans-serif; font-size: 14px; color: var(--gray-800);
            resize: none; max-height: 120px; background: var(--gray-50);
            transition: border-color 0.15s, box-shadow 0.15s;
        }
        .chat-input:focus {
            outline: none; border-color: var(--blue-200);
            box-shadow: 0 0 0 3px rgba(13,71,161,0.08); background: var(--white);
        }
        .chat-input::placeholder { color: var(--gray-400); }
        .btn-send {
            padding: 10px 18px; background: var(--blue-800); color: var(--white);
            border: none; border-radius: var(--radius-md);
            font-size: 14px; font-weight: 500; font-family: 'DM Sans', sans-serif;
            cursor: pointer; flex-shrink: 0; transition: background 0.15s;
            display: flex; align-items: center; gap: 6px;
        }
        .btn-send:hover  { background: var(--blue-700); }
        .btn-send:active { background: var(--blue-900); }
        .btn-send svg { width: 16px; height: 16px; }

        /* ── WELCOME / EMPTY STATES ── */
        .welcome-placeholder, .chat-empty {
            display: flex; flex-direction: column;
            align-items: center; justify-content: center;
            flex: 1; color: var(--gray-400);
            padding: 40px; text-align: center;
        }
        .welcome-placeholder svg, .chat-empty svg {
            width: 56px; height: 56px; margin-bottom: 16px; opacity: 0.25;
        }
        .welcome-placeholder h3, .chat-empty h3 {
            font-size: 16px; font-weight: 600; color: var(--gray-600); margin-bottom: 8px;
        }
        .welcome-placeholder p, .chat-empty p {
            font-size: 13px; color: var(--gray-400); max-width: 220px;
        }
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
        </a>
        <a href="<%= ctx %>/app/home/" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
            Home
        </a>
    </nav>

    <div class="sidebar-footer">
        <div class="sidebar-user">
            <div class="user-avatar"><%= avatarLetter %></div>
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
            <p>Chat with your <%= isMentor ? "mentees" : "mentor" %></p>
        </div>
    </div>

    <!-- TWO-PANEL CHAT -->
    <div class="content">

        <!-- LEFT: Conversations list -->
        <div class="conversations-panel">
            <div class="conversations-header">
                <h2>Conversations</h2>
                <span class="conv-count" id="convCount">0</span>
            </div>
            <div class="conversations-list" id="conversationsList">
                <div class="no-conversations">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                    <p>No conversations yet</p>
                </div>
            </div>
        </div>

        <!-- RIGHT: Chat panel -->
        <div class="chat-panel" id="chatPanel">
            <div class="welcome-placeholder">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                <h3>Select a conversation</h3>
                <p>Choose someone from the list to start messaging</p>
            </div>
        </div>

    </div>
</div>

<script>
    const role        = '<%= role %>';
    const userId      = '<%= userIdStr %>';
    const username    = '<%= username %>';
    const contextPath = '<%= ctx %>';

    let currentConversation = null;
    let ws = null;
    let messageHistory = {};

    function initializeConversations() {
        fetch(contextPath + '/app/conversations/list', { credentials: 'same-origin' })
            .then(r => r.json())
            .then(data => {
                if (data && data.conversations && data.conversations.length > 0) {
                    document.getElementById('convCount').textContent = data.conversations.length;
                    renderConversations(data.conversations);
                    selectConversation(data.conversations[0]);
                } else {
                    document.getElementById('conversationsList').innerHTML =
                        '<div class="no-conversations"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg><p>No conversations yet</p></div>';
                }
            })
            .catch(e => {
                console.error('Failed to load conversations:', e);
                document.getElementById('conversationsList').innerHTML =
                    '<div class="no-conversations"><p>Error loading conversations</p></div>';
            });
    }

    function renderConversations(conversations) {
        const list = document.getElementById('conversationsList');
        list.innerHTML = conversations.map(conv => {
            const initial = conv.partnerName ? conv.partnerName.substring(0, 1).toUpperCase() : '?';
            return `
                <div class="conversation-item" data-room-id="${conv.roomId}"
                     onclick="selectConversation(${JSON.stringify(conv).replace(/"/g, '&quot;')})">
                    <div class="conversation-item-top">
                        <div class="conv-avatar">${initial}</div>
                        <div class="conversation-item-name">${escapeHtml(conv.partnerName)}</div>
                    </div>
                    <div class="conversation-item-time">${conv.lastActivity || 'No messages yet'}</div>
                </div>
            `;
        }).join('');
    }

    function selectConversation(conv) {
        document.querySelectorAll('.conversation-item').forEach(el => el.classList.remove('active'));
        const item = document.querySelector(`[data-room-id="${conv.roomId}"]`);
        if (item) item.classList.add('active');

        currentConversation = conv;
        renderChatWindow(conv);
        connectWebSocket(conv.roomId);
    }

    function renderChatWindow(conv) {
        const messages  = messageHistory[conv.roomId] || [];
        const initial   = conv.partnerName ? conv.partnerName.substring(0, 1).toUpperCase() : '?';
        const partnerRole = conv.partnerRole === 'mentor' ? 'Mentor' : 'Mentee';

        document.getElementById('chatPanel').innerHTML = `
            <div class="chat-header">
                <div class="chat-header-avatar">${initial}</div>
                <div>
                    <div class="chat-header-title">${escapeHtml(conv.partnerName)}</div>
                    <div class="chat-header-subtitle"><span class="online-dot"></span>${partnerRole}</div>
                </div>
            </div>
            <div class="chat-messages" id="messagesList">
                ${messages.length === 0
                    ? '<div class="message-system"><span>No messages yet — say hello!</span></div>'
                    : messages.map(msg => renderBubble(msg)).join('')}
            </div>
            <div class="chat-input-area">
                <textarea id="messageInput" class="chat-input" placeholder="Type a message…" rows="1"></textarea>
                <button class="btn-send" onclick="sendMessage()">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
                    Send
                </button>
            </div>
        `;

        document.getElementById('messageInput').addEventListener('keydown', e => {
            if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); sendMessage(); }
        });

        scrollToBottom();
    }

    function renderBubble(msg) {
        const isOwn    = msg.sender === username;
        const initial  = msg.sender ? msg.sender.substring(0, 1).toUpperCase() : '?';
        return `
            <div class="message-group ${isOwn ? 'own' : ''}">
                ${!isOwn ? `<div class="message-avatar">${initial}</div>` : ''}
                <div class="message-col">
                    <div class="message-bubble ${isOwn ? 'sent' : 'received'}">${escapeHtml(msg.text)}</div>
                    <div class="message-time">${msg.time || ''}</div>
                </div>
            </div>
        `;
    }

    function connectWebSocket(roomId) {
        if (ws && ws.url.includes(roomId)) return;
        if (ws) ws.close();

        const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
        ws = new WebSocket(`${protocol}://${window.location.host}${contextPath}/chat/${roomId}`);

        ws.onmessage = event => {
            try {
                const msg = JSON.parse(event.data);
                if (!messageHistory[roomId]) messageHistory[roomId] = [];
                messageHistory[roomId].push(msg);
                if (currentConversation && currentConversation.roomId === roomId) {
                    const list = document.getElementById('messagesList');
                    if (list) {
                        list.insertAdjacentHTML('beforeend', renderBubble(msg));
                        scrollToBottom();
                    }
                }
            } catch (e) { console.error('Failed to parse message:', e); }
        };

        ws.onerror = e => console.error('WebSocket error:', e);
        ws.onclose = ()  => console.log('WebSocket closed');
    }

    function sendMessage() {
        const input = document.getElementById('messageInput');
        const text  = input ? input.value.trim() : '';
        if (!text || !currentConversation || !ws || ws.readyState !== WebSocket.OPEN) return;

        const now  = new Date();
        const time = String(now.getHours()).padStart(2,'0') + ':' + String(now.getMinutes()).padStart(2,'0');
        const msg  = { sender: username, text, time };

        if (!messageHistory[currentConversation.roomId]) messageHistory[currentConversation.roomId] = [];
        messageHistory[currentConversation.roomId].push(msg);

        ws.send(JSON.stringify(msg));

        // Append bubble directly without full re-render
        const list = document.getElementById('messagesList');
        if (list) {
            list.insertAdjacentHTML('beforeend', renderBubble(msg));
            scrollToBottom();
        }

        input.value = '';
        input.style.height = 'auto';
    }

    function scrollToBottom() {
        const list = document.getElementById('messagesList');
        if (list) list.scrollTop = list.scrollHeight;
    }

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    window.addEventListener('DOMContentLoaded', initializeConversations);
</script>

</body>
</html>
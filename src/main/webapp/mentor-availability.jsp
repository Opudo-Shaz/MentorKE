<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="app.model.MentorAvailability" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Set Availability — MentorKE</title>
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

        .sidebar {
            width: var(--sidebar-w);
            background: var(--blue-800);
            height: 100vh;
            position: fixed;
            left: 0;
            top: 0;
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
            width: 34px;
            height: 34px;
            background: rgba(255,255,255,0.18);
            border-radius: var(--radius-md);
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .logo-text { font-size: 17px; font-weight: 600; color: var(--white); }
        .logo-sub  { font-size: 11px; color: rgba(255,255,255,0.5); margin-top: 1px; }

        .sidebar-nav { flex: 1; padding: 14px 10px; overflow-y: auto; }
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
            text-decoration: none;
            margin-bottom: 2px;
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
            width: 34px;
            height: 34px;
            border-radius: 50%;
            background: rgba(255,255,255,0.2);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 13px;
            font-weight: 600;
            color: var(--white);
        }
        .user-name { font-size: 13px; font-weight: 500; color: var(--white); }
        .user-role { font-size: 11px; color: rgba(255,255,255,0.5); }
        .btn-logout {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 7px;
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

        .main {
            margin-left: var(--sidebar-w);
            flex: 1;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        .topbar {
            height: 60px;
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
        .topbar h1 { font-size: 17px; font-weight: 600; color: var(--gray-800); }
        .topbar p  { font-size: 12px; color: var(--gray-400); margin-top: 1px; }

        .content { padding: 24px 28px; flex: 1; }

        .card {
            background: #fff;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            padding: 24px;
            max-width: 920px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.07);
        }
        .day-row { display: grid; grid-template-columns: 130px 1fr 1fr 90px; gap: 12px; align-items: center; padding: 14px 0; border-bottom: 1px solid #f1f5f9; }
        .day-row:last-child { border-bottom: none; }
        .day-name { font-size: 14px; font-weight: 500; }
        label { font-size: 12px; color: #94a3b8; display: block; margin-bottom: 4px; }
        input[type="time"] { width: 100%; padding: 8px 10px; border: 1px solid #e2e8f0; border-radius: 6px; font-size: 14px; font-family: 'DM Sans', sans-serif; color: #1e293b; }
        input[type="time"]:focus { outline: none; border-color: #1976d2; }
        .toggle { position: relative; display: inline-block; width: 44px; height: 24px; }
        .toggle input { display: none; }
        .slider { position: absolute; cursor: pointer; top: 0; left: 0; right: 0; bottom: 0; background: #e2e8f0; border-radius: 24px; transition: 0.2s; }
        .slider::before { position: absolute; content: ''; height: 18px; width: 18px; border-radius: 50%; left: 3px; bottom: 3px; background: white; transition: 0.2s; }
        input:checked + .slider { background: #1976d2; }
        input:checked + .slider::before { transform: translateX(20px); }
        .btn-save { margin-top: 24px; padding: 10px 28px; background: #0d47a1; color: #fff; border: none; border-radius: 8px; font-size: 14px; font-weight: 500; font-family: 'DM Sans', sans-serif; cursor: pointer; transition: background 0.15s; display: inline-flex; align-items: center; gap: 8px; }
        .btn-save:hover { background: #1565c0; }
        .btn-save:disabled { background: #94a3b8; cursor: not-allowed; }
        .alert { border-radius: 8px; padding: 12px 16px; font-size: 14px; margin-bottom: 20px; display: none; }
        .alert-success { background: #f0fdf4; border: 1px solid #bbf7d0; color: #15803d; }
        .alert-error   { background: #fef2f2; border: 1px solid #fecaca; color: #b91c1c; }
    </style>
</head>
<body>

<%
    String username = (String) session.getAttribute("username");
    if (username == null) {
        response.sendRedirect(request.getContextPath() + "/app/login/");
        return;
    }

    @SuppressWarnings("unchecked")
    List<MentorAvailability> existingSlots =
        (List<MentorAvailability>) request.getAttribute("availabilitySlots");
%>

<aside class="sidebar">
    <div class="sidebar-brand">
        <div class="logo">
            <div class="logo-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M22 10v6M2 10l10-5 10 5-10 5z"/>
                    <path d="M6 12v5c3 3 9 3 12 0v-5"/>
                </svg>
            </div>
            <div>
                <div class="logo-text">MentorKE</div>
                <div class="logo-sub">Mentor Portal</div>
            </div>
        </div>
    </div>

    <nav class="sidebar-nav">
        <div class="nav-section-label">Menu</div>

        <a href="<%= request.getContextPath() %>/app/mentor-dashboard/" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="3" width="7" height="7"/>
                <rect x="14" y="3" width="7" height="7"/>
                <rect x="3" y="14" width="7" height="7"/>
                <rect x="14" y="14" width="7" height="7"/>
            </svg>
            Dashboard
        </a>

        <a href="<%= request.getContextPath() %>/app/mentor-availability/" class="nav-link active">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                <line x1="16" y1="2" x2="16" y2="6"/>
                <line x1="8" y1="2" x2="8" y2="6"/>
                <line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
            Availability
        </a>

        <div class="nav-section-label">My Progress</div>

        <a href="<%= request.getContextPath() %>/app/sessions/completed" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
                <line x1="16" y1="13" x2="8" y2="13"/>
                <line x1="16" y1="17" x2="8" y2="17"/>
            </svg>
            Reports
        </a>

        <a href="<%= request.getContextPath() %>/app/mentor-analytics/" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M3 3v18h18"/>
                <path d="M7 15l3-3 3 2 4-5"/>
            </svg>
            Analytics
        </a>

        <a href="<%= request.getContextPath() %>/app/home/" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
            Home
        </a>

        <a href="/MentorKE/app/about/" class="nav-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
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
        <a href="<%= request.getContextPath() %>/app/login/?action=logout" class="btn-logout">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                <polyline points="16 17 21 12 16 7"/>
                <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
            Sign out
        </a>
    </div>
</aside>

<div class="main">
    <div class="topbar">
        <div>
            <h1>Set Availability</h1>
            <p>Choose when you are available for mentoring sessions</p>
        </div>
    </div>

    <div class="content">
        <div id="alertSuccess" class="alert alert-success">Availability saved successfully!</div>
        <div id="alertError"   class="alert alert-error">Failed to save availability. Please try again.</div>

        <div class="card">
            <form id="availabilityForm">
        <%
            String[] days = {"MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"};

            // Build a quick lookup from existing slots
            java.util.Map<String, MentorAvailability> slotMap = new java.util.HashMap<>();
            if (existingSlots != null) {
                for (MentorAvailability slot : existingSlots) {
                    slotMap.put(slot.getDayOfWeek(), slot);
                }
            }

            for (String day : days) {
                MentorAvailability existing = slotMap.get(day);
                String startVal = existing != null ? existing.getStartTime().toString().substring(0, 5) : "08:00";
                String endVal   = existing != null ? existing.getEndTime().toString().substring(0, 5)   : "17:00";
                boolean checked = existing == null || existing.isAvailable();
        %>
        <div class="day-row">
            <div class="day-name"><%= day.charAt(0) + day.substring(1).toLowerCase() %></div>
            <div>
                <label>From</label>
                <input type="time" name="<%= day %>_start" value="<%= startVal %>">
            </div>
            <div>
                <label>To</label>
                <input type="time" name="<%= day %>_end" value="<%= endVal %>">
            </div>
            <div style="text-align:center;">
                <label>Available</label>
                <label class="toggle">
                    <input type="checkbox" name="<%= day %>_available" <%= checked ? "checked" : "" %>>
                    <span class="slider"></span>
                </label>
            </div>
        </div>
        <% } %>

                <button type="submit" class="btn-save" id="saveBtn">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/><polyline points="17 21 17 13 7 13 7 21"/><polyline points="7 3 7 8 15 8"/></svg>
                    Save Availability
                </button>
            </form>
        </div>
    </div>
</div>

<script>
    const DAYS = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];
    const CTX  = '<%= request.getContextPath() %>';

    document.getElementById('availabilityForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const btn = document.getElementById('saveBtn');
        btn.disabled = true;
        btn.textContent = 'Saving...';

        const slots = DAYS.map(day => ({
            dayOfWeek:   day,
            startTime:   document.querySelector(`[name="${day}_start"]`).value,
            endTime:     document.querySelector(`[name="${day}_end"]`).value,
            isAvailable: document.querySelector(`[name="${day}_available"]`).checked ? 'true' : 'false'
        }));

        // Use session-based auth — no Authorization header needed
        // credentials: 'same-origin' sends the session cookie automatically
        fetch(CTX + '/api/availability', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin',
            body: JSON.stringify(slots)
        })
        .then(r => {
            if (r.ok) {
                showAlert('success');
            } else {
                return r.text().then(t => { throw new Error(t); });
            }
        })
        .catch(err => {
            console.error('Save failed:', err);
            showAlert('error');
        })
        .finally(() => {
            btn.disabled = false;
            btn.innerHTML = `
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/><polyline points="17 21 17 13 7 13 7 21"/><polyline points="7 3 7 8 15 8"/></svg>
                Save Availability`;
        });
    });

    function showAlert(type) {
        const success = document.getElementById('alertSuccess');
        const error   = document.getElementById('alertError');
        success.style.display = 'none';
        error.style.display   = 'none';

        if (type === 'success') {
            success.style.display = 'block';
            setTimeout(() => success.style.display = 'none', 3000);
        } else {
            error.style.display = 'block';
            setTimeout(() => error.style.display = 'none', 5000);
        }
    }
</script>
</body>
</html>
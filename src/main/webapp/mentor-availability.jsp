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
        body { font-family: 'DM Sans', sans-serif; background: #f8fafc; color: #1e293b; padding: 32px; }
        .page-header { margin-bottom: 28px; }
        .page-header h1 { font-size: 22px; font-weight: 600; }
        .page-header p  { font-size: 14px; color: #64748b; margin-top: 4px; }
        .card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px; max-width: 720px; box-shadow: 0 1px 3px rgba(0,0,0,0.07); }
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
        .back-link { display: inline-flex; align-items: center; gap: 6px; font-size: 14px; color: #475569; text-decoration: none; margin-bottom: 20px; }
        .back-link:hover { color: #0d47a1; }
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

<a href="<%= request.getContextPath() %>/app/mentor-dashboard/" class="back-link">
    ← Back to dashboard
</a>

<div class="page-header">
    <h1>Set Your Availability</h1>
    <p>Choose the days and times you are available for mentoring sessions.</p>
</div>

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
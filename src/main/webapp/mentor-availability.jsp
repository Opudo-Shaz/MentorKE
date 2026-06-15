<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Set Availability — MentorKE</title>
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'DM Sans', sans-serif; background: #f8fafc; color: #1e293b; padding: 32px; }
        .page-header { margin-bottom: 28px; }
        .page-header h1 { font-size: 22px; font-weight: 600; }
        .page-header p  { font-size: 14px; color: #64748b; margin-top: 4px; }
        .card {
            background: #fff; border: 1px solid #e2e8f0;
            border-radius: 12px; padding: 24px; max-width: 720px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.07);
        }
        .day-row {
            display: grid;
            grid-template-columns: 120px 1fr 1fr 80px;
            gap: 12px; align-items: center;
            padding: 14px 0;
            border-bottom: 1px solid #f1f5f9;
        }
        .day-row:last-child { border-bottom: none; }
        .day-name { font-size: 14px; font-weight: 500; }
        label { font-size: 12px; color: #94a3b8; display: block; margin-bottom: 4px; }
        input[type="time"] {
            width: 100%; padding: 8px 10px;
            border: 1px solid #e2e8f0; border-radius: 6px;
            font-size: 14px; font-family: 'DM Sans', sans-serif;
            color: #1e293b;
        }
        input[type="time"]:focus { outline: none; border-color: #1976d2; }
        .toggle {
            position: relative; display: inline-block;
            width: 44px; height: 24px;
        }
        .toggle input { display: none; }
        .slider {
            position: absolute; cursor: pointer;
            top: 0; left: 0; right: 0; bottom: 0;
            background: #e2e8f0; border-radius: 24px;
            transition: 0.2s;
        }
        .slider::before {
            position: absolute; content: '';
            height: 18px; width: 18px; border-radius: 50%;
            left: 3px; bottom: 3px;
            background: white; transition: 0.2s;
        }
        input:checked + .slider { background: #1976d2; }
        input:checked + .slider::before { transform: translateX(20px); }
        .btn-save {
            margin-top: 24px; padding: 10px 28px;
            background: #0d47a1; color: #fff;
            border: none; border-radius: 8px;
            font-size: 14px; font-weight: 500;
            font-family: 'DM Sans', sans-serif;
            cursor: pointer; transition: background 0.15s;
        }
        .btn-save:hover { background: #1565c0; }
        .back-link {
            display: inline-flex; align-items: center; gap: 6px;
            font-size: 14px; color: #475569; text-decoration: none;
            margin-bottom: 20px;
        }
        .back-link:hover { color: #0d47a1; }
        .alert-success {
            background: #f0fdf4; border: 1px solid #bbf7d0;
            color: #15803d; border-radius: 8px;
            padding: 12px 16px; font-size: 14px;
            margin-bottom: 20px; display: none;
        }
    </style>
</head>
<body>

<a href="<%= request.getContextPath() %>/app/mentor-dashboard/" class="back-link">
    ← Back to dashboard
</a>

<div class="page-header">
    <h1>Set Your Availability</h1>
    <p>Choose the days and times you are available for mentoring sessions.</p>
</div>

<div id="successAlert" class="alert-success">
    Availability saved successfully!
</div>

<div class="card">
    <form id="availabilityForm">
        <% String[] days = {"MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"}; %>
        <% for (String day : days) { %>
        <div class="day-row">
            <div class="day-name"><%= day.charAt(0) + day.substring(1).toLowerCase() %></div>
            <div>
                <label>From</label>
                <input type="time" name="<%= day %>_start" value="08:00">
            </div>
            <div>
                <label>To</label>
                <input type="time" name="<%= day %>_end" value="17:00">
            </div>
            <div style="text-align:center;">
                <label>Available</label>
                <label class="toggle">
                    <input type="checkbox" name="<%= day %>_available" checked>
                    <span class="slider"></span>
                </label>
            </div>
        </div>
        <% } %>
        <button type="submit" class="btn-save">Save Availability</button>
    </form>
</div>

<script>
    const DAYS = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];

    // Load existing availability on page load
    fetch('<%= request.getContextPath() %>/api/availability', {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
    })
    .then(r => r.json())
    .then(slots => {
        slots.forEach(slot => {
            const day = slot.dayOfWeek;
            const startEl = document.querySelector(`[name="${day}_start"]`);
            const endEl   = document.querySelector(`[name="${day}_end"]`);
            const availEl = document.querySelector(`[name="${day}_available"]`);
            if (startEl) startEl.value = slot.startTime.substring(0,5);
            if (endEl)   endEl.value   = slot.endTime.substring(0,5);
            if (availEl) availEl.checked = slot.available;
        });
    })
    .catch(() => {}); // no existing availability yet

    // Save on submit
    document.getElementById('availabilityForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const slots = DAYS.map(day => ({
            dayOfWeek:   day,
            startTime:   document.querySelector(`[name="${day}_start"]`).value,
            endTime:     document.querySelector(`[name="${day}_end"]`).value,
            isAvailable: document.querySelector(`[name="${day}_available"]`).checked ? 'true' : 'false'
        }));

        fetch('<%= request.getContextPath() %>/api/availability', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            body: JSON.stringify(slots)
        })
        .then(r => r.json())
        .then(() => {
            const alert = document.getElementById('successAlert');
            alert.style.display = 'block';
            setTimeout(() => alert.style.display = 'none', 3000);
        })
        .catch(err => console.error('Save failed:', err));
    });
</script>
</body>
</html>
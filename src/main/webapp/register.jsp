<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="java.lang.reflect.Modifier" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register — MentorKE</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --blue-900: #0a2e6e;
            --blue-800: #0d47a1;
            --blue-700: #1565c0;
            --blue-600: #1976d2;
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
            --red-700:  #b91c1c;
            --radius-md: 8px;
            --radius-lg: 12px;
            --shadow-md: 0 4px 24px rgba(0,0,0,0.08);
        }

        body {
            font-family: 'DM Sans', sans-serif;
            background: var(--gray-50);
            color: var(--gray-800);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        /* ── NAVBAR ── */
        .navbar {
            background: var(--white);
            border-bottom: 1px solid var(--gray-200);
            padding: 0 60px; height: 64px;
            display: flex; align-items: center; justify-content: space-between;
            position: sticky; top: 0; z-index: 100;
        }
        .nav-brand { display: flex; align-items: center; gap: 10px; text-decoration: none; }
        .nav-brand-icon {
            width: 34px; height: 34px; background: var(--blue-800);
            border-radius: var(--radius-md);
            display: flex; align-items: center; justify-content: center;
        }
        .nav-brand-icon svg { width: 18px; height: 18px; }
        .nav-brand-name { font-size: 18px; font-weight: 700; color: var(--blue-800); letter-spacing: -0.3px; }
        .nav-links { display: flex; align-items: center; gap: 6px; }
        .nav-links a {
            padding: 7px 14px; border-radius: 7px;
            font-size: 14px; font-weight: 500; color: var(--gray-600);
            text-decoration: none; transition: background 0.15s, color 0.15s;
        }
        .nav-links a:hover { background: var(--gray-100); color: var(--gray-800); }
        .nav-links .btn-outline { border: 1px solid var(--gray-200); color: var(--gray-800); }
        .nav-links .btn-primary { background: var(--blue-800); color: var(--white); padding: 7px 18px; }
        .nav-links .btn-primary:hover { background: var(--blue-700); color: var(--white); }

        /* ── PAGE LAYOUT ── */
        .page {
            flex: 1;
            display: grid;
            grid-template-columns: 1fr 1fr;
            min-height: calc(100vh - 64px);
        }

        /* ── LEFT PANEL ── */
        .left-panel {
            background: linear-gradient(160deg, var(--blue-900) 0%, var(--blue-800) 55%, var(--blue-700) 100%);
            padding: 60px 50px;
            display: flex; flex-direction: column; justify-content: center;
            position: relative; overflow: hidden;
        }
        .left-panel::before {
            content: '';
            position: absolute; inset: 0;
            background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.03'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
        }
        .left-content { position: relative; z-index: 1; }
        .left-tag {
            display: inline-flex; align-items: center; gap: 7px;
            background: rgba(255,255,255,0.12); border: 1px solid rgba(255,255,255,0.2);
            border-radius: 20px; padding: 5px 14px 5px 10px;
            font-size: 13px; font-weight: 500; color: rgba(255,255,255,0.9);
            margin-bottom: 28px;
        }
        .left-tag-dot { width: 7px; height: 7px; border-radius: 50%; background: #4ade80; }
        .left-panel h2 {
            font-size: 34px; font-weight: 700; color: var(--white);
            line-height: 1.2; letter-spacing: -0.5px; margin-bottom: 16px;
        }
        .left-panel h2 span { color: var(--blue-100); }
        .left-panel p {
            font-size: 15px; color: rgba(255,255,255,0.65);
            line-height: 1.8; margin-bottom: 40px; max-width: 380px;
        }
        .left-features { display: flex; flex-direction: column; gap: 16px; }
        .left-feature {
            display: flex; align-items: flex-start; gap: 12px;
        }
        .left-feature-icon {
            width: 32px; height: 32px; border-radius: 8px;
            background: rgba(255,255,255,0.12);
            display: flex; align-items: center; justify-content: center;
            flex-shrink: 0; margin-top: 1px;
        }
        .left-feature-icon svg { width: 16px; height: 16px; }
        .left-feature-title { font-size: 14px; font-weight: 600; color: var(--white); margin-bottom: 2px; }
        .left-feature-sub   { font-size: 13px; color: rgba(255,255,255,0.55); line-height: 1.5; }

        /* ── RIGHT PANEL (FORM) ── */
        .right-panel {
            display: flex; align-items: center; justify-content: center;
            padding: 40px 50px; background: var(--white);
            overflow-y: auto;
        }
        .form-wrap { width: 100%; max-width: 400px; }

        .form-header { margin-bottom: 28px; }
        .form-header h1 {
            font-size: 26px; font-weight: 700; color: var(--gray-800);
            letter-spacing: -0.4px; margin-bottom: 6px;
        }
        .form-header p { font-size: 14px; color: var(--gray-400); }

        /* ── STEP INDICATOR ── */
        .step-indicator {
            display: flex; align-items: center; gap: 0;
            margin-bottom: 28px;
        }
        .step {
            display: flex; align-items: center; gap: 8px;
            font-size: 12px; font-weight: 500; color: var(--gray-400);
        }
        .step.active { color: var(--blue-800); }
        .step.done   { color: var(--gray-400); }
        .step-num {
            width: 24px; height: 24px; border-radius: 50%;
            background: var(--gray-100); color: var(--gray-400);
            display: flex; align-items: center; justify-content: center;
            font-size: 11px; font-weight: 700; flex-shrink: 0;
        }
        .step.active .step-num { background: var(--blue-800); color: var(--white); }
        .step.done   .step-num { background: var(--gray-200); }
        .step-line {
            flex: 1; height: 1px; background: var(--gray-200); margin: 0 8px;
        }

        /* ── ROLE SELECTOR ── */
        .role-selector {
            display: grid; grid-template-columns: 1fr 1fr;
            gap: 10px; margin-bottom: 20px;
        }
        .role-option { display: none; }
        .role-label {
            display: flex; flex-direction: column; align-items: center; gap: 6px;
            padding: 14px 10px;
            border: 1.5px solid var(--gray-200);
            border-radius: var(--radius-md); cursor: pointer;
            transition: border-color 0.15s, background 0.15s;
            text-align: center;
        }
        .role-label:hover { border-color: var(--blue-200); background: var(--blue-25); }
        .role-option:checked + .role-label {
            border-color: var(--blue-800);
            background: var(--blue-25);
        }
        .role-icon {
            width: 36px; height: 36px; border-radius: 9px;
            display: flex; align-items: center; justify-content: center;
        }
        .role-icon svg { width: 18px; height: 18px; }
        .role-icon.mentee-icon  { background: var(--blue-50); }
        .role-icon.mentor-icon  { background: #e0f2f1; }
        .role-title { font-size: 13px; font-weight: 600; color: var(--gray-800); }
        .role-sub   { font-size: 11px; color: var(--gray-400); }

        /* ── FORM FIELDS ── */
        .fields-grid {
            display: grid; grid-template-columns: 1fr 1fr;
            gap: 12px; margin-bottom: 12px;
        }
        .form-group { display: flex; flex-direction: column; gap: 5px; }
        .form-group.full { grid-column: 1 / -1; }
        .form-group label {
            font-size: 12px; font-weight: 500; color: var(--gray-600);
        }
        .form-group input,
        .form-group select {
            padding: 9px 12px;
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-md);
            font-size: 14px; font-family: 'DM Sans', sans-serif;
            color: var(--gray-800); background: var(--white);
            transition: border-color 0.15s, box-shadow 0.15s;
            width: 100%;
        }
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: var(--blue-600);
            box-shadow: 0 0 0 3px rgba(25,118,210,0.12);
        }
        .form-group input::placeholder { color: var(--gray-400); }

        /* ── SUBMIT ── */
        .btn-submit {
            width: 100%; padding: 11px;
            background: var(--blue-800); color: var(--white);
            border: none; border-radius: var(--radius-md);
            font-size: 15px; font-weight: 600;
            font-family: 'DM Sans', sans-serif;
            cursor: pointer; margin-top: 6px;
            display: flex; align-items: center; justify-content: center; gap: 8px;
            transition: background 0.15s, transform 0.15s;
        }
        .btn-submit:hover { background: var(--blue-700); transform: translateY(-1px); }
        .btn-submit svg { width: 16px; height: 16px; }

        /* ── FOOTER LINKS ── */
        .form-footer {
            margin-top: 20px; text-align: center;
            font-size: 13px; color: var(--gray-400);
        }
        .form-footer a { color: var(--blue-800); font-weight: 500; text-decoration: none; }
        .form-footer a:hover { text-decoration: underline; }

        .divider {
            display: flex; align-items: center; gap: 10px;
            margin: 16px 0; color: var(--gray-400); font-size: 12px;
        }
        .divider::before, .divider::after {
            content: ''; flex: 1; height: 1px; background: var(--gray-200);
        }

        .reflection-note {
            font-size: 11px; color: var(--gray-400);
            text-align: center; margin-top: 14px;
            padding: 8px 12px; background: var(--gray-50);
            border-radius: var(--radius-md); border: 1px solid var(--gray-200);
        }

        /* ── FOOTER ── */
        footer {
            background: var(--blue-900); color: rgba(255,255,255,0.4);
            text-align: center; font-size: 13px; padding: 16px;
        }
    </style>
</head>
<body>

    <!-- ══════════ NAVBAR ══════════ -->
    <nav class="navbar">
        <a href="index" class="nav-brand">
            <div class="nav-brand-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
            </div>
            <span class="nav-brand-name">MentorKE</span>
        </a>
        <div class="nav-links">
            <a href="index">Home</a>
            <a href="about">About</a>
            <a href="login" class="btn-outline">Login</a>
        </div>
    </nav>

    <!-- ══════════ PAGE BODY ══════════ -->
    <div class="page">

        <!-- LEFT PANEL -->
        <div class="left-panel">
            <div class="left-content">
                <div class="left-tag">
                    <span class="left-tag-dot"></span>
                    Free to join
                </div>
                <h2>Start your<br><span>mentorship journey</span><br>today</h2>
                <p>Join thousands of Kenyans already growing their careers and skills through structured, personalised mentorship.</p>

                <div class="left-features">
                    <div class="left-feature">
                        <div class="left-feature-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                        </div>
                        <div>
                            <div class="left-feature-title">Get matched with an expert</div>
                            <div class="left-feature-sub">We pair you with a mentor whose expertise aligns with your goals and field of study.</div>
                        </div>
                    </div>
                    <div class="left-feature">
                        <div class="left-feature-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
                        </div>
                        <div>
                            <div class="left-feature-title">Track your progress</div>
                            <div class="left-feature-sub">Set goals and monitor your development through your personal dashboard.</div>
                        </div>
                    </div>
                    <div class="left-feature">
                        <div class="left-feature-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                        </div>
                        <div>
                            <div class="left-feature-title">Safe and verified</div>
                            <div class="left-feature-sub">Every mentor on MentorKE is reviewed and verified before being matched.</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- RIGHT PANEL (FORM) -->
        <div class="right-panel">
            <div class="form-wrap">

                <div class="form-header">
                    <h1>Create your account</h1>
                    <p>Fill in your details to get started — it takes less than 2 minutes.</p>
                </div>

                <!-- Role selection -->
                <div style="margin-bottom:20px;">
                    <div style="font-size:12px; font-weight:500; color:var(--gray-600); margin-bottom:10px;">I want to join as</div>
                    <div class="role-selector">
                        <div>
                            <input type="radio" name="role_display" id="role_mentee" value="mentee" class="role-option" checked>
                            <label for="role_mentee" class="role-label">
                                <div class="role-icon mentee-icon">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="#1565c0" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/><polyline points="16 11 18 13 22 9"/></svg>
                                </div>
                                <div class="role-title">Mentee</div>
                                <div class="role-sub">I want guidance</div>
                            </label>
                        </div>
                        <div>
                            <input type="radio" name="role_display" id="role_mentor" value="mentor" class="role-option">
                            <label for="role_mentor" class="role-label">
                                <div class="role-icon mentor-icon">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="#0f6e56" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="16"/><line x1="10" y1="14" x2="14" y2="14"/></svg>
                                </div>
                                <div class="role-title">Mentor</div>
                                <div class="role-sub">I want to give back</div>
                            </label>
                        </div>
                    </div>
                </div>

                <form action="register" method="post">

                    <!-- Hidden role field synced with visual selector -->
                    <input type="hidden" name="role" id="hiddenRole" value="mentee">

                    <%-- COMMON USER FIELDS VIA REFLECTION --%>
                    <%
                        List<String> generatedFields = new ArrayList<>();
                        try {
                            Field[] fields = app.model.User.class.getDeclaredFields();
                    %>
                    <div class="fields-grid">
                    <%
                            for (Field field : fields) {
                                String fieldName = field.getName();
                                Class<?> fieldType = field.getType();

                                if (Modifier.isStatic(field.getModifiers())) continue;
                                if (fieldName.equals("createdAt") || fieldName.equals("id") || fieldName.equals("role")) continue;

                                generatedFields.add(fieldName);

                                String inputType = "text";
                                if (fieldName.contains("email"))    inputType = "email";
                                else if (fieldName.contains("password")) inputType = "password";
                                else if (fieldType == long.class || fieldType == int.class) inputType = "number";

                                // make label human-readable: camelCase → "Camel Case"
                                String label = fieldName.replaceAll("([A-Z])", " $1");
                                label = label.substring(0, 1).toUpperCase() + label.substring(1);

                                boolean required = !fieldName.equals("status");

                                // full-width for certain fields
                                boolean fullWidth = fieldName.equals("email") || fieldName.equals("status");
                                String groupClass = fullWidth ? "form-group full" : "form-group";
                    %>
                        <div class="<%= groupClass %>">
                            <label for="<%= fieldName %>"><%= label %><%= required ? " *" : "" %></label>
                            <% if (fieldName.equals("status")) { %>
                            <select name="status" id="status">
                                <option value="Active" selected>Active</option>
                                <option value="Inactive">Inactive</option>
                            </select>
                            <% } else { %>
                            <input
                                type="<%= inputType %>"
                                name="<%= fieldName %>"
                                id="<%= fieldName %>"
                                placeholder="<%= label.toLowerCase() %>"
                                <%= required ? "required" : "" %>
                            >
                            <% } %>
                        </div>
                    <%
                            }
                    %>
                    </div>
                    <%
                        } catch (Exception e) {
                            System.err.println("[register.jsp] Reflection error: " + e.getMessage());
                        }
                    %>

                    <!-- ===== ROLE-SPECIFIC FIELDS ===== -->
                    
                    <!-- Mentee Fields -->
                    <div id="menteeFields" style="display: block;">
                        <div style="font-size:12px; font-weight:600; color:var(--gray-600); margin-top:20px; margin-bottom:12px;">Profile Details (Mentee)</div>
                        <div class="fields-grid">
                            <div class="form-group full">
                                <label for="educationLevel">Education Level *</label>
                                <select name="educationLevel" id="educationLevel" required>
                                    <option value="">Select education level</option>
                                    <option value="High School">High School</option>
                                    <option value="Bachelor">Bachelor's Degree</option>
                                    <option value="Master">Master's Degree</option>
                                    <option value="PhD">PhD</option>
                                    <option value="Certification">Professional Certification</option>
                                    <option value="Other">Other</option>
                                </select>
                            </div>
                            <div class="form-group full">
                                <label for="fieldOfStudy">Field of Study *</label>
                                <input type="text" name="fieldOfStudy" id="fieldOfStudy" placeholder="e.g., Software Engineering, Business" required>
                            </div>
                            <div class="form-group full">
                                <label for="learningGoals">Learning Goals *</label>
                                <input type="text" name="learningGoals" id="learningGoals" placeholder="What do you want to achieve?" required>
                            </div>
                            <div class="form-group full">
                                <label for="phoneNumberMentee">Phone Number</label>
                                <input type="tel" name="phoneNumber" id="phoneNumberMentee" placeholder="+254...">
                            </div>
                        </div>
                    </div>

                    <!-- Mentor Fields -->
                    <div id="mentorFields" style="display: none;">
                        <div style="font-size:12px; font-weight:600; color:var(--gray-600); margin-top:20px; margin-bottom:12px;">Profile Details (Mentor)</div>
                        <div class="fields-grid">
                            <div class="form-group full">
                                <label for="specialization">Specialization *</label>
                                <input type="text" name="specialization" id="specialization" placeholder="e.g., Full-Stack Web Development">
                            </div>
                            <div class="form-group full">
                                <label for="expertise">Expertise *</label>
                                <input type="text" name="expertise" id="expertise" placeholder="Your key skills" required>
                            </div>
                            <div class="form-group">
                                <label for="yearsOfExperience">Years of Experience *</label>
                                <input type="number" name="yearsOfExperience" id="yearsOfExperience" placeholder="e.g., 5" min="0" required>
                            </div>
                            <div class="form-group">
                                <label for="bio">Bio *</label>
                                <input type="text" name="bio" id="bio" placeholder="Brief bio" required>
                            </div>
                            <div class="form-group full">
                                <label for="qualifications">Qualifications *</label>
                                <input type="text" name="qualifications" id="qualifications" placeholder="Your certifications and credentials" required>
                            </div>
                            <div class="form-group full">
                                <label for="phoneNumberMentor">Phone Number</label>
                                <input type="tel" name="phoneNumber" id="phoneNumberMentor" placeholder="+254...">
                            </div>
                        </div>
                    </div>

                    <button type="submit" class="btn-submit">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                        Create account
                    </button>

                </form>

                <div class="reflection-note">
                    ⚡ Form fields auto-generated with role-specific sections. Base fields from User class, role-specific fields managed by service layer.
                </div>

                <div class="divider">or</div>

                <div class="form-footer">
                    Already have an account? <a href="login">Sign in</a>
                    &nbsp;·&nbsp;
                    <a href="index">Back to home</a>
                </div>

            </div>
        </div>
    </div>

    <footer>
        &copy; 2026 MentorKE. All rights reserved. Built in Kenya.
    </footer>

    <script>
        // Sync visual role selector with hidden form field and toggle role-specific fields
        document.querySelectorAll('input[name="role_display"]').forEach(function(radio) {
            radio.addEventListener('change', function() {
                var selectedRole = this.value;
                document.getElementById('hiddenRole').value = selectedRole;
                
                // Toggle role-specific form sections
                if (selectedRole === 'mentee') {
                    document.getElementById('menteeFields').style.display = 'block';
                    document.getElementById('mentorFields').style.display = 'none';
                    // Make mentee fields required
                    document.getElementById('educationLevel').required = true;
                    document.getElementById('fieldOfStudy').required = true;
                    document.getElementById('learningGoals').required = true;
                    // Make mentor fields not required
                    document.getElementById('specialization').required = false;
                    document.getElementById('expertise').required = false;
                    document.getElementById('yearsOfExperience').required = false;
                    document.getElementById('bio').required = false;
                    document.getElementById('qualifications').required = false;
                } else if (selectedRole === 'mentor') {
                    document.getElementById('menteeFields').style.display = 'none';
                    document.getElementById('mentorFields').style.display = 'block';
                    // Make mentee fields not required
                    document.getElementById('educationLevel').required = false;
                    document.getElementById('fieldOfStudy').required = false;
                    document.getElementById('learningGoals').required = false;
                    // Make mentor fields required
                    document.getElementById('specialization').required = true;
                    document.getElementById('expertise').required = true;
                    document.getElementById('yearsOfExperience').required = true;
                    document.getElementById('bio').required = true;
                    document.getElementById('qualifications').required = true;
                }
            });
        });
        
        // Initialize on page load
        document.addEventListener('DOMContentLoaded', function() {
            var selectedRole = document.querySelector('input[name="role_display"]:checked');
            if (selectedRole) {
                selectedRole.dispatchEvent(new Event('change'));
            }
        });
    </script>

</body>
</html>
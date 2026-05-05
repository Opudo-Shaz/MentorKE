<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String username = (String) request.getAttribute("username");
    String role = (String) request.getAttribute("role");
    String dashboardUrl = "";
    String dashboardName = "";

    if ("mentor".equalsIgnoreCase(role)) {
        dashboardUrl = "mentor-dashboard";
        dashboardName = "Mentor Dashboard";
    } else if ("mentee".equalsIgnoreCase(role)) {
        dashboardUrl = "mentee-dashboard";
        dashboardName = "Mentee Dashboard";
    } else if ("admin".equalsIgnoreCase(role)) {
        dashboardUrl = "admin";
        dashboardName = "Admin Dashboard";
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Registration Successful - MentorKE</title>
    <meta http-equiv='refresh' content='3;url=<%= dashboardUrl %>'>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f8fb; text-align: center; margin: 0; }
        .card { background: white; padding: 40px; margin: 100px auto; width: 400px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #0d47a1; margin-top: 0; }
        .success-icon { font-size: 48px; color: #4caf50; }
        p { color: #333; line-height: 1.6; }
        .info { background-color: #e3f2fd; padding: 15px; border-radius: 5px; margin: 15px 0; }
        .countdown { font-size: 14px; color: #666; margin-top: 20px; }
        a { display: inline-block; margin-top: 15px; padding: 10px 20px; background: #0d47a1; color: white; text-decoration: none; border-radius: 5px; }
        a:hover { background: #1565c0; }
    </style>
</head>
<body>
    <div class='card'>
        <div class='success-icon'>✓</div>
        <h1>Registration Successful!</h1>
        <p>Welcome to MentorKE, <strong><%= username %></strong>!</p>
        <div class='info'>
            <p><strong>Role:</strong> <%= role.substring(0, 1).toUpperCase() + role.substring(1) %></p>
            <p><strong>Redirecting to:</strong> <%= dashboardName %></p>
        </div>
        <p class='countdown'>Redirecting in 3 seconds...</p>
        <p><a href='<%= dashboardUrl %>'>Click here if not redirected automatically</a></p>
    </div>
</body>
</html>


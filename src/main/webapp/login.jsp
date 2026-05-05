<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>

<%
    // === GET ERROR MESSAGE FROM REQUEST ATTRIBUTE (set by Login servlet) ===
    String errorMessage = (String) request.getAttribute("errorMessage");

    // === GET PRE-FILLED USERNAME IF AVAILABLE ===
    String username = request.getParameter("username");
    if (username == null) {
        username = "";
    }

    System.out.println("[login.jsp] Rendering login form");
    System.out.println("[login.jsp] Request Method: " + request.getMethod());
    System.out.println("[login.jsp] Context Path: " + request.getContextPath());
%>

<!DOCTYPE html>
<html>
<head>
    <title>Login - MentorKE</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background-color: #f4f8fb; }
        .container { display: flex; justify-content: center; align-items: center; min-height: 100vh; }
        .card { background: white; padding: 30px; border-radius: 10px; width: 350px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #0d47a1; text-align: center; }
        input, select { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ccc; border-radius: 5px; box-sizing: border-box; }
        button { width: 100%; padding: 10px; background: #0d47a1; color: white; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; }
        button:hover { background: #1565c0; }
        .error { background-color: #ffebee; color: #d32f2f; padding: 10px; border-radius: 5px; margin-bottom: 10px; text-align: center; display: none; }
        .nav { text-align: center; margin-top: 20px; }
        .nav a { color: #0d47a1; text-decoration: none; margin: 0 10px; }
        .debug-info { font-size: 11px; color: #999; text-align: center; margin-top: 20px; border-top: 1px solid #ddd; padding-top: 10px; }
    </style>
</head>
<body>
    <div class='container'>
        <div class='card'>
            <h1>Login</h1>

            <%-- Display error message if present --%>
            <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                <div class='error' style="display: block;">
                    <strong>Error:</strong> <%= errorMessage %>
                </div>
                <% System.out.println("[login.jsp] Displaying error: " + errorMessage); %>
            <% } %>

            <%-- Login Form --%>
            <form action='<%= request.getContextPath() %>/login' method='post'>
                <div>
                    <label for='username'><strong>Username:</strong></label>
                    <input type='text' id='username' name='username'
                        placeholder='Enter username'
                        value='<%= username %>'
                        required>
                </div>

                <div>
                    <label for='password'><strong>Password:</strong></label>
                    <input type='password' id='password' name='password'
                        placeholder='Enter password'
                        required>
                </div>

                <div>
                    <label for='role'><strong>Select Role:</strong></label>
                    <select id='role' name='role' required>
                        <option value=''>-- Select a Role --</option>
                        <option value='mentee'>Mentee</option>
                        <option value='mentor'>Mentor</option>
                        <option value='admin'>Admin</option>
                    </select>
                </div>

                <button type='submit'>Login</button>
            </form>

            <%-- Navigation Links --%>
            <div class='nav'>
                <a href='<%= request.getContextPath() %>'>Home</a>
                <a href='<%= request.getContextPath() %>/register'>Register</a>
            </div>

            <%-- Debug Info (for development) --%>
            <div class='debug-info'>
                Session ID: <%= session.getId() %><br>
                Remote IP: <%= request.getRemoteAddr() %>
            </div>
        </div>
    </div>

</body>
</html>


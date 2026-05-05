<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Error - MentorKE</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f8fb; text-align: center; margin: 0; }
        .card { background: white; padding: 40px; margin: 100px auto; width: 400px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #d32f2f; margin-top: 0; }
        .error-icon { font-size: 48px; color: #d32f2f; }
        .error-msg { background-color: #ffebee; padding: 15px; border-radius: 5px; margin: 15px 0; color: #d32f2f; }
        a { display: inline-block; margin-top: 15px; padding: 10px 20px; background: #0d47a1; color: white; text-decoration: none; border-radius: 5px; }
        a:hover { background: #1565c0; }
    </style>
</head>
<body>
    <div class='card'>
        <div class='error-icon'>✗</div>
        <h1>Registration Error</h1>
        <div class='error-msg'><%= request.getAttribute("errorMessage") %></div>
        <p><a href='register'>Try Again</a></p>
    </div>
</body>
</html>


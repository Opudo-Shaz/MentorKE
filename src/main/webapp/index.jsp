<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>MentorKE - Home</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background-color: #f4f8fb; }
        .navbar { background-color: #0d47a1; padding: 15px; text-align: center; }
        .navbar a { color: white; margin: 0 15px; text-decoration: none; font-weight: bold; }
        .hero { text-align: center; padding: 60px 20px; background-color: #e3f2fd; }
        .hero h1 { color: #0d47a1; font-size: 36px; }
        .hero p { color: #333; font-size: 18px; }
        .btn { display: inline-block; padding: 12px 20px; margin: 10px; background-color: #0d47a1; color: white; text-decoration: none; border-radius: 5px; }
        .btn:hover { background-color: #1565c0; }
        .features { display: flex; justify-content: center; gap: 20px; padding: 40px; flex-wrap: wrap; }
        .card { background: white; padding: 20px; width: 250px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); text-align: center; }
        .card h3 { color: #0d47a1; }
        .footer { text-align: center; padding: 15px; background: #0d47a1; color: white; margin-top: 30px; }
    </style>
</head>
<body>
    <div class='navbar'>
        <a href='index'>Home</a>
        <a href='about'>About</a>
        <a href='register'>Register</a>
        <a href='login'>Login</a>
    </div>

    <div class='hero'>
        <h1>Welcome to MentorKE</h1>
        <p>Connecting mentors and mentees across Kenya for growth, guidance, and opportunity.</p>
        <a href='register' class='btn'>Get Started</a>
        <a href='login' class='btn'>Login</a>
        <a href='about' class='btn'>Learn More</a>
    </div>

    <div class='features'>
        <div class='card'>
            <h3>Find a Mentor</h3>
            <p>Connect with experienced professionals in your field.</p>
        </div>
        <div class='card'>
            <h3>Grow Your Career</h3>
            <p>Set goals, track progress, and build your future.</p>
        </div>
        <div class='card'>
            <h3>Easy Scheduling</h3>
            <p>Book mentorship sessions and manage your time easily.</p>
        </div>
    </div>

    <div class='footer'>
        <p>&copy; 2026 MentorKE | Built in Kenya</p>
    </div>
</body>
</html>


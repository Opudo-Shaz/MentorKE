<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html>
<head>
    <title>Mentee Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background-color: #f4f8fb; }
        .navbar { background-color: #0d47a1; padding: 15px; text-align: center; }
        .navbar a { color: white; margin: 0 15px; text-decoration: none; font-weight: bold; }
        .header { background-color: #e3f2fd; padding: 30px; text-align: center; }
        .header h1 { color: #0d47a1; margin: 0; }
        .container { padding: 40px; max-width: 1200px; margin: auto; }
        .dashboard-section { background: white; padding: 20px; margin: 20px 0; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .dashboard-section h2 { color: #0d47a1; margin-top: 0; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        table th, table td { padding: 12px; border: 1px solid #ddd; text-align: left; }
        table th { background-color: #0d47a1; color: white; }
        table tr:hover { background-color: #f5f5f5; }
        .btn { display: inline-block; padding: 10px 15px; background-color: #0d47a1; color: white; text-decoration: none; border-radius: 5px; margin-top: 10px; }
        .btn:hover { background-color: #1565c0; }
        .stats { display: flex; gap: 20px; margin: 20px 0; }
        .stat-card { flex: 1; background-color: #e3f2fd; padding: 20px; border-radius: 8px; text-align: center; }
        .stat-card h3 { color: #0d47a1; margin: 0 0 10px 0; }
        .stat-card .number { font-size: 28px; font-weight: bold; color: #0d47a1; }
        .progress-bar { width: 100%; background-color: #ddd; border-radius: 5px; overflow: hidden; margin: 10px 0; }
        .progress { background-color: #0d47a1; height: 20px; width: 65%; text-align: center; color: white; font-size: 12px; }
        .footer { text-align: center; padding: 15px; background: #0d47a1; color: white; margin-top: 30px; }
    </style>
</head>
<body>
    <div class='navbar'>
        <a href='index'>Home</a>
        <a href='about'>About</a>
        <a href='mentee-dashboard'>Dashboard</a>
        <a href='login?action=logout'>Logout</a>
    </div>

    <div class='header'>
        <h1>Mentee Dashboard</h1>
        <p>Welcome! Track your mentorship journey</p>
        <p>Logged in as: <strong><%= session.getAttribute("username") %></strong></p>
    </div>

    <div class='container'>
        <div class='stats'>
            <div class='stat-card'>
                <h3>Your Mentors</h3>
                <div class='number'>2</div>
            </div>
            <div class='stat-card'>
                <h3>Sessions Attended</h3>
                <div class='number'>12</div>
            </div>
            <div class='stat-card'>
                <h3>Next Session</h3>
                <div class='number'>2 days</div>
            </div>
        </div>

        <div class='dashboard-section'>
            <h2>Your Mentors</h2>
            <table>
                <tr>
                    <th>Mentor Name</th>
                    <th>Expertise</th>
                    <th>Experience</th>
                    <th>Action</th>
                </tr>
                <tr>
                    <td>Dr. James Mwangi</td>
                    <td>Software Engineering</td>
                    <td>10+ years</td>
                    <td><a href='#' class='btn'>Connect</a></td>
                </tr>
                <tr>
                    <td>Ms. Grace Kipchoge</td>
                    <td>Data Science</td>
                    <td>8+ years</td>
                    <td><a href='#' class='btn'>Connect</a></td>
                </tr>
            </table>
        </div>

        <div class='dashboard-section'>
            <h2>Learning Progress</h2>
            <h4>Web Development</h4>
            <div class='progress-bar'>
                <div class='progress' style='width: 65%;'>65%</div>
            </div>
            <h4>Cloud Computing</h4>
            <div class='progress-bar'>
                <div class='progress' style='width: 45%;'>45%</div>
            </div>
            <h4>DevOps Practices</h4>
            <div class='progress-bar'>
                <div class='progress' style='width: 30%;'>30%</div>
            </div>
        </div>

        <div class='dashboard-section'>
            <h2>Upcoming Sessions</h2>
            <table>
                <tr>
                    <th>Mentor</th>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Topic</th>
                </tr>
                <tr>
                    <td>Dr. James Mwangi</td>
                    <td>2026-03-27</td>
                    <td>4:00 PM</td>
                    <td>Advanced Java Concepts</td>
                </tr>
                <tr>
                    <td>Ms. Grace Kipchoge</td>
                    <td>2026-03-30</td>
                    <td>2:00 PM</td>
                    <td>Exploratory Data Analysis</td>
                </tr>
            </table>
        </div>
    </div>

    <div class='footer'>
           &copy; 2024 MentorKE. All rights reserved.
       </div>

</body>
</html>


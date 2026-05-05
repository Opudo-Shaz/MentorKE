<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html>
<head>
    <title>Mentor Dashboard</title>
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
        .footer { text-align: center; padding: 15px; background: #0d47a1; color: white; margin-top: 30px; }
    </style>
</head>
<body>
    <div class='navbar'>
        <a href='index'>Home</a>
        <a href='about'>About</a>
        <a href='mentor-dashboard'>Dashboard</a>
        <a href='login?action=logout'>Logout</a>
    </div>

    <div class='header'>
        <h1>Mentor Dashboard</h1>
        <p>Welcome back! Manage your mentees and sessions</p>
        <p>Logged in as: <strong><%= session.getAttribute("username") %></strong></p>
    </div>

    <div class='container'>
        <div class='stats'>
            <div class='stat-card'>
                <h3>Active Mentees</h3>
                <div class='number'>8</div>
            </div>
            <div class='stat-card'>
                <h3>Completed Sessions</h3>
                <div class='number'>24</div>
            </div>
            <div class='stat-card'>
                <h3>Upcoming Sessions</h3>
                <div class='number'>3</div>
            </div>
        </div>

        <div class='dashboard-section'>
            <h2>My Mentees</h2>
            <table>
                <tr>
                    <th>Mentee Name</th>
                    <th>Email</th>
                    <th>Focus Area</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                <tr>
                    <td>Jane Smith</td>
                    <td>jane@example.com</td>
                    <td>Web Development</td>
                    <td>Active</td>
                    <td><a href='#' class='btn'>View</a></td>
                </tr>
                <tr>
                    <td>John Kariuki</td>
                    <td>john@example.com</td>
                    <td>Data Science</td>
                    <td>Active</td>
                    <td><a href='#' class='btn'>View</a></td>
                </tr>
                <tr>
                    <td>Sarah Omondi</td>
                    <td>sarah@example.com</td>
                    <td>Mobile Development</td>
                    <td>Paused</td>
                    <td><a href='#' class='btn'>View</a></td>
                </tr>
            </table>
        </div>

        <div class='dashboard-section'>
            <h2>Upcoming Sessions</h2>
            <table>
                <tr>
                    <th>Mentee</th>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Topic</th>
                </tr>
                <tr>
                    <td>Jane Smith</td>
                    <td>2026-03-28</td>
                    <td>2:00 PM</td>
                    <td>React Framework Deep Dive</td>
                </tr>
                <tr>
                    <td>John Kariuki</td>
                    <td>2026-03-29</td>
                    <td>3:30 PM</td>
                    <td>Machine Learning Basics</td>
                </tr>
            </table>
        </div>
    </div>

    <div class='footer'>
        &copy; 2024 MentorKE. All rights reserved.
    </div>
</body>
</html>


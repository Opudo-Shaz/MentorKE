<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.util.List" %>
<%@ page import="app.model.User" %>
<%@ page import="app.model.Mentor" %>
<%@ page import="app.model.Mentee" %>
<%@ page import="app.dao.UserDAO" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background-color: #f4f8fb; }
        .navbar { background-color: #0d47a1; padding: 15px; text-align: center; }
        .navbar a { color: white; margin: 0 15px; text-decoration: none; font-weight: bold; }
        .navbar a:hover { background-color: #1565c0; padding: 10px; border-radius: 4px; }
        .header { background-color: #e3f2fd; padding: 30px; text-align: center; }
        .header h1 { color: #0d47a1; margin: 0; }
        .container { padding: 40px; max-width: 1200px; margin: auto; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        table th, table td { padding: 12px; border: 1px solid #ddd; text-align: left; }
        table th { background-color: #0d47a1; color: white; }
        table tr:hover { background-color: #f5f5f5; }
        .footer { text-align: center; padding: 15px; background: #0d47a1; color: white; margin-top: 30px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; color: #0d47a1; }
        input, select, textarea { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        textarea { min-height: 80px; font-family: Arial, sans-serif; }
        button { background-color: #0d47a1; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; margin-right: 10px; }
        button:hover { background-color: #1565c0; }
        .btn-danger { background-color: #d32f2f; }
        .btn-danger:hover { background-color: #c62828; }
        .btn-edit { background-color: #f57c00; }
        .btn-edit:hover { background-color: #e65100; }
        .form-section { background: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .success { background-color: #c8e6c9; color: #2e7d32; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        .error { background-color: #ffcdd2; color: #c62828; padding: 10px; border-radius: 4px; margin-bottom: 15px; }
        .modal { display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.5); overflow-y: auto; }
        .modal.show { display: block; }
        .modal-content { background-color: white; margin: 5% auto; padding: 20px; border-radius: 8px; width: 500px; box-shadow: 0 4px 12px rgba(0,0,0,0.2); }
        .close-btn { color: #999; float: right; font-size: 28px; font-weight: bold; cursor: pointer; }
        .close-btn:hover { color: #000; }
        .action-buttons { display: flex; gap: 5px; flex-wrap: wrap; }
        .action-buttons form { margin: 0; display: inline; }
        .hidden { display: none; }
    </style>
    <script>
        function openEditModal(entityType, id, data) {
            const modal = document.getElementById('editModal');
            const form = document.getElementById('editForm');

            document.getElementById('modalTitle').textContent = 'Edit ' + entityType;
            document.getElementById('editEntityType').value = entityType;
            document.getElementById('editId').value = id;

            // Hide all field groups
            document.getElementById('userFields').style.display = 'none';
            document.getElementById('userEmailField').style.display = 'none';
            document.getElementById('userRoleField').style.display = 'none';
            document.getElementById('mentorFields').style.display = 'none';
            document.getElementById('mentorExpertiseField').style.display = 'none';
            document.getElementById('mentorYearsField').style.display = 'none';
            document.getElementById('mentorBioField').style.display = 'none';
            document.getElementById('mentorQualificationsField').style.display = 'none';
            document.getElementById('menteeFields').style.display = 'none';
            document.getElementById('menteeFieldOfStudyField').style.display = 'none';
            document.getElementById('menteeLearningGoalsField').style.display = 'none';
            document.getElementById('menteeMentorIdField').style.display = 'none';
            document.getElementById('phoneNumberField').style.display = 'none';
            document.getElementById('statusField').style.display = 'none';

            // Show appropriate fields based on entity type
            if (entityType === 'User') {
                document.getElementById('userFields').style.display = 'block';
                document.getElementById('userEmailField').style.display = 'block';
                document.getElementById('userRoleField').style.display = 'block';
                document.getElementById('statusField').style.display = 'block';
                form.action = 'user-management';
            } else if (entityType === 'Mentor') {
                document.getElementById('mentorFields').style.display = 'block';
                document.getElementById('mentorExpertiseField').style.display = 'block';
                document.getElementById('mentorYearsField').style.display = 'block';
                document.getElementById('mentorBioField').style.display = 'block';
                document.getElementById('mentorQualificationsField').style.display = 'block';
                document.getElementById('phoneNumberField').style.display = 'block';
                document.getElementById('statusField').style.display = 'block';
                form.action = 'mentor-management';
            } else if (entityType === 'Mentee') {
                document.getElementById('menteeFields').style.display = 'block';
                document.getElementById('menteeFieldOfStudyField').style.display = 'block';
                document.getElementById('menteeLearningGoalsField').style.display = 'block';
                document.getElementById('menteeMentorIdField').style.display = 'block';
                document.getElementById('phoneNumberField').style.display = 'block';
                document.getElementById('statusField').style.display = 'block';
                form.action = 'mentee-management';
            }

            // Populate fields based on data
            for (let key in data) {
                const fieldId = 'edit' + key.charAt(0).toUpperCase() + key.slice(1).replace(/([A-Z])/g, match => match);
                const field = document.getElementById(fieldId);
                if (field) {
                    field.value = data[key];
                }
            }

            modal.classList.add('show');
        }

        function closeEditModal() {
            document.getElementById('editModal').classList.remove('show');
        }

        window.onclick = function(event) {
            var modal = document.getElementById('editModal');
            if (event.target == modal) {
                modal.classList.remove('show');
            }
        }
    </script>
</head>
<body>
    <div class='navbar'>
        <div class='navbar'>
            <a href='admin'>Dashboard</a>
            <a href='admin?view=users'>Users</a>
            <a href='admin?view=mentors'>Mentors</a>
            <a href='admin?view=mentees'>Mentees</a>
            <a href='login?action=logout'>Logout</a>
        </div>
    </div>


    <div class='header'>
        <h1>Admin Dashboard</h1>
        <p>Manage users, mentors, and mentees</p>
        <p>Logged in as: <strong><%= session.getAttribute("username") %></strong></p>
    </div>

    <div class='container'>
        <%-- Success/Error Messages --%>
        <% String successMsg = request.getParameter("success"); %>
        <% String errorMsg = request.getParameter("error"); %>
        <% if ("user_added".equals(successMsg)) { %>
            <div class='success'>✓ User added successfully!</div>
        <% } else if ("user_updated".equals(successMsg)) { %>
            <div class='success'>✓ User updated successfully!</div>
        <% } else if ("user_deleted".equals(successMsg)) { %>
            <div class='success'>✓ User deleted successfully!</div>
        <% } else if ("mentor_added".equals(successMsg)) { %>
            <div class='success'>✓ Mentor added successfully!</div>
        <% } else if ("mentor_updated".equals(successMsg)) { %>
            <div class='success'>✓ Mentor updated successfully!</div>
        <% } else if ("mentor_deleted".equals(successMsg)) { %>
            <div class='success'>✓ Mentor deleted successfully!</div>
        <% } else if ("mentee_added".equals(successMsg)) { %>
            <div class='success'>✓ Mentee added successfully!</div>
        <% } else if ("mentee_updated".equals(successMsg)) { %>
            <div class='success'>✓ Mentee updated successfully!</div>
        <% } else if ("mentee_deleted".equals(successMsg)) { %>
            <div class='success'>✓ Mentee deleted successfully!</div>
        <% } else if (errorMsg != null && !errorMsg.isEmpty()) { %>
            <div class='error'>✗ Error: <strong><%= errorMsg %></strong></div>
        <% } %>

        <%-- Get the current view --%>
        <% String view = (String) request.getAttribute("view");
           if (view == null) view = "users";
        %>

        <!-- ==================== USERS SECTION ==================== -->
        <% if ("users".equals(view)) { %>
            <!-- Add User Form -->
            <div class='form-section'>
                <h2>Add New User</h2>
                <form action='user-management' method='post'>
                    <input type='hidden' name='action' value='add'>

                    <div class='form-group'>
                        <label>Username:</label>
                        <input type='text' name='username' placeholder='Enter username' required>
                    </div>

                    <div class='form-group'>
                        <label>Password:</label>
                        <input type='password' name='password' placeholder='Enter password' required>
                    </div>

                    <div class='form-group'>
                        <label>Email:</label>
                        <input type='email' name='email' placeholder='Enter email' required>
                    </div>

                    <div class='form-group'>
                        <label>Role:</label>
                        <select name='role' required>
                            <option value=''>Select Role</option>
                            <option value='mentee'>Mentee</option>
                            <option value='mentor'>Mentor</option>
                            <option value='admin'>Admin</option>
                        </select>
                    </div>

                    <div class='form-group'>
                        <label>Status:</label>
                        <select name='status'>
                            <option value='Active'>Active</option>
                            <option value='Inactive'>Inactive</option>
                        </select>
                    </div>

                    <button type='submit'>Add User</button>
                </form>
            </div>

            <h2>Users List</h2>
            <table>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                <%
                    try {
                        List<User> users = (List<User>) request.getAttribute("users");
                        if (users != null && !users.isEmpty()) {
                            for (User user : users) {
                %>
                    <tr>
                        <td><%= user.getId() %></td>
                        <td><%= user.getUsername() %></td>
                        <td><%= user.getEmail() %></td>
                        <td><%= user.getRole() %></td>
                        <td><%= user.getStatus() %></td>
                        <td>
                            <div class='action-buttons'>
                                <button class='btn-edit' onclick="openEditModal('User', '<%= user.getId() %>', {id: '<%= user.getId() %>', username: '<%= user.getUsername() %>', email: '<%= user.getEmail() %>', role: '<%= user.getRole() %>', status: '<%= user.getStatus() %>'})">Edit</button>
                                <form action='user-management' method='post' style='display:inline;'>
                                    <input type='hidden' name='action' value='delete'>
                                    <input type='hidden' name='userId' value='<%= user.getId() %>'>
                                    <button type='submit' class='btn-danger' onclick='return confirm("Delete user <%= user.getUsername() %>?")'>Delete</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                <%
                            }
                        } else {
                %>
                    <tr>
                        <td colspan='6' style='text-align: center; color: #999;'>No users found</td>
                    </tr>
                <%
                        }
                    } catch (Exception e) {
                        System.err.println("[AdminDashboard] Error loading users: " + e.getMessage());
                %>
                    <tr>
                        <td colspan='6' style='text-align: center; color: red;'>Error loading users: <%= e.getMessage() %></td>
                    </tr>
                <%
                    }
                %>
            </table>
        <% } %>

        <!-- ==================== MENTORS SECTION ==================== -->
        <% if ("mentors".equals(view)) { %>
            <!-- Add Mentor Form -->
            <div class='form-section'>
                <h2>Add New Mentor</h2>
                <form action='mentor-management' method='post'>
                    <input type='hidden' name='action' value='add'>

                    <div class='form-group'>
                        <label>User ID:</label>
                        <input type='text' name='userId' placeholder='Enter user ID' required>
                    </div>

                    <div class='form-group'>
                        <label>Specialization:</label>
                        <input type='text' name='specialization' placeholder='Enter specialization' required>
                    </div>

                    <div class='form-group'>
                        <label>Expertise:</label>
                        <textarea name='expertise' placeholder='Enter expertise areas' required></textarea>
                    </div>

                    <div class='form-group'>
                        <label>Years of Experience:</label>
                        <input type='number' name='yearsOfExperience' placeholder='Enter years' required>
                    </div>

                    <div class='form-group'>
                        <label>Bio:</label>
                        <textarea name='bio' placeholder='Enter bio' required></textarea>
                    </div>

                    <div class='form-group'>
                        <label>Qualifications:</label>
                        <textarea name='qualifications' placeholder='Enter qualifications' required></textarea>
                    </div>

                    <div class='form-group'>
                        <label>Phone Number:</label>
                        <input type='tel' name='phoneNumber' placeholder='Enter phone number' required>
                    </div>

                    <div class='form-group'>
                        <label>Status:</label>
                        <select name='status' required>
                            <option value='Active'>Active</option>
                            <option value='Inactive'>Inactive</option>
                        </select>
                    </div>

                    <button type='submit'>Add Mentor</button>
                </form>
            </div>

            <h2>Mentors List</h2>
            <table>
                <tr>
                    <th>ID</th>
                    <th>User ID</th>
                    <th>Specialization</th>
                    <th>Years of Experience</th>
                    <th>Phone</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                <%
                    try {
                        List<Mentor> mentors = (List<Mentor>) request.getAttribute("mentors");
                        if (mentors != null && !mentors.isEmpty()) {
                            for (Mentor mentor : mentors) {
                %>
                    <tr>
                        <td><%= mentor.getId() %></td>
                        <td><%= mentor.getUserId() %></td>
                        <td><%= mentor.getSpecialization() %></td>
                        <td><%= mentor.getYearsOfExperience() %></td>
                        <td><%= mentor.getPhoneNumber() %></td>
                        <td><%= mentor.getStatus() %></td>
                        <td>
                            <div class='action-buttons'>
                                <button class='btn-edit' onclick="openEditModal('Mentor', '<%= mentor.getId() %>', {id: '<%= mentor.getId() %>', userId: '<%= mentor.getUserId() %>', specialization: '<%= mentor.getSpecialization() %>', expertise: '<%= mentor.getExpertise() %>', yearsOfExperience: '<%= mentor.getYearsOfExperience() %>', bio: '<%= mentor.getBio() %>', qualifications: '<%= mentor.getQualifications() %>', phoneNumber: '<%= mentor.getPhoneNumber() %>', status: '<%= mentor.getStatus() %>'})">Edit</button>
                                <form action='mentor-management' method='post' style='display:inline;'>
                                    <input type='hidden' name='action' value='delete'>
                                    <input type='hidden' name='mentorId' value='<%= mentor.getId() %>'>
                                    <button type='submit' class='btn-danger' onclick='return confirm("Delete mentor?")'>Delete</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                <%
                            }
                        } else {
                %>
                    <tr>
                        <td colspan='7' style='text-align: center; color: #999;'>No mentors found</td>
                    </tr>
                <%
                        }
                    } catch (Exception e) {
                        System.err.println("[AdminDashboard] Error loading mentors: " + e.getMessage());
                %>
                    <tr>
                        <td colspan='7' style='text-align: center; color: red;'>Error loading mentors: <%= e.getMessage() %></td>
                    </tr>
                <%
                    }
                %>
            </table>
        <% } %>

        <!-- ==================== MENTEES SECTION ==================== -->
        <% if ("mentees".equals(view)) { %>
            <!-- Add Mentee Form -->
            <div class='form-section'>
                <h2>Add New Mentee</h2>
                <form action='mentee-management' method='post'>
                    <input type='hidden' name='action' value='add'>

                    <div class='form-group'>
                        <label>User ID:</label>
                        <input type='text' name='userId' placeholder='Enter user ID' required>
                    </div>

                    <div class='form-group'>
                        <label>Education Level:</label>
                        <select name='educationLevel' required>
                            <option value=''>Select Education Level</option>
                            <option value='High School'>High School</option>
                            <option value='Bachelor'>Bachelor</option>
                            <option value='Master'>Master</option>
                            <option value='PhD'>PhD</option>
                        </select>
                    </div>

                    <div class='form-group'>
                        <label>Field of Study:</label>
                        <input type='text' name='fieldOfStudy' placeholder='Enter field of study' required>
                    </div>

                    <div class='form-group'>
                        <label>Learning Goals:</label>
                        <textarea name='learningGoals' placeholder='Enter learning goals' required></textarea>
                    </div>

                    <div class='form-group'>
                        <label>Phone Number:</label>
                        <input type='tel' name='phoneNumber' placeholder='Enter phone number' required>
                    </div>

                    <div class='form-group'>
                        <label>Mentor ID (Optional):</label>
                        <input type='text' name='mentorId' placeholder='Enter mentor ID (leave empty if none)'>
                    </div>

                    <div class='form-group'>
                        <label>Status:</label>
                        <select name='status' required>
                            <option value='Active'>Active</option>
                            <option value='Inactive'>Inactive</option>
                        </select>
                    </div>

                    <button type='submit'>Add Mentee</button>
                </form>
            </div>

            <h2>Mentees List</h2>
            <table>
                <tr>
                    <th>ID</th>
                    <th>User ID</th>
                    <th>Education Level</th>
                    <th>Field of Study</th>
                    <th>Phone</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                <%
                    try {
                        List<Mentee> mentees = (List<Mentee>) request.getAttribute("mentees");
                        if (mentees != null && !mentees.isEmpty()) {
                            for (Mentee mentee : mentees) {
                %>
                    <tr>
                        <td><%= mentee.getId() %></td>
                        <td><%= mentee.getUserId() %></td>
                        <td><%= mentee.getEducationLevel() %></td>
                        <td><%= mentee.getFieldOfStudy() %></td>
                        <td><%= mentee.getPhoneNumber() %></td>
                        <td><%= mentee.getStatus() %></td>
                        <td>
                            <div class='action-buttons'>
                                <button class='btn-edit' onclick="openEditModal('Mentee', '<%= mentee.getId() %>', {id: '<%= mentee.getId() %>', userId: '<%= mentee.getUserId() %>', educationLevel: '<%= mentee.getEducationLevel() %>', fieldOfStudy: '<%= mentee.getFieldOfStudy() %>', learningGoals: '<%= mentee.getLearningGoals() %>', phoneNumber: '<%= mentee.getPhoneNumber() %>', mentorId: '<%= mentee.getMentorId() %>', status: '<%= mentee.getStatus() %>'})">Edit</button>
                                <form action='mentee-management' method='post' style='display:inline;'>
                                    <input type='hidden' name='action' value='delete'>
                                    <input type='hidden' name='menteeId' value='<%= mentee.getId() %>'>
                                    <button type='submit' class='btn-danger' onclick='return confirm("Delete mentee?")'>Delete</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                <%
                            }
                        } else {
                %>
                    <tr>
                        <td colspan='7' style='text-align: center; color: #999;'>No mentees found</td>
                    </tr>
                <%
                        }
                    } catch (Exception e) {
                        System.err.println("[AdminDashboard] Error loading mentees: " + e.getMessage());
                %>
                    <tr>
                        <td colspan='7' style='text-align: center; color: red;'>Error loading mentees: <%= e.getMessage() %></td>
                    </tr>
                <%
                    }
                %>
            </table>
        <% } %>
    </div>

    <!-- Edit Modal (Dynamic for all entity types) -->
    <div id='editModal' class='modal'>
        <div class='modal-content'>
            <span class='close-btn' onclick='closeEditModal()'>&times;</span>
            <h2 id='modalTitle'>Edit</h2>
            <form action='user-management' method='post' id='editForm'>
                <input type='hidden' name='action' value='update'>
                <input type='hidden' name='entityType' id='editEntityType'>
                <input type='hidden' name='id' id='editId'>

                <!-- User Fields -->
                <div class='form-group' id='userFields' style='display:none;'>
                    <label>Username:</label>
                    <input type='text' name='username' id='editUsername'>
                </div>

                <div class='form-group' id='userEmailField' style='display:none;'>
                    <label>Email:</label>
                    <input type='email' name='email' id='editEmail'>
                </div>

                <div class='form-group' id='userRoleField' style='display:none;'>
                    <label>Role:</label>
                    <select name='role' id='editRole'>
                        <option value='mentee'>Mentee</option>
                        <option value='mentor'>Mentor</option>
                        <option value='admin'>Admin</option>
                    </select>
                </div>

                <!-- Mentor Fields -->
                <div class='form-group' id='mentorFields' style='display:none;'>
                    <label>Specialization:</label>
                    <input type='text' name='specialization' id='editSpecialization'>
                </div>

                <div class='form-group' id='mentorExpertiseField' style='display:none;'>
                    <label>Expertise:</label>
                    <textarea name='expertise' id='editExpertise'></textarea>
                </div>

                <div class='form-group' id='mentorYearsField' style='display:none;'>
                    <label>Years of Experience:</label>
                    <input type='number' name='yearsOfExperience' id='editYearsOfExperience'>
                </div>

                <div class='form-group' id='mentorBioField' style='display:none;'>
                    <label>Bio:</label>
                    <textarea name='bio' id='editBio'></textarea>
                </div>

                <div class='form-group' id='mentorQualificationsField' style='display:none;'>
                    <label>Qualifications:</label>
                    <textarea name='qualifications' id='editQualifications'></textarea>
                </div>

                <!-- Mentee Fields -->
                <div class='form-group' id='menteeFields' style='display:none;'>
                    <label>Education Level:</label>
                    <select name='educationLevel' id='editEducationLevel'>
                        <option value='High School'>High School</option>
                        <option value='Bachelor'>Bachelor</option>
                        <option value='Master'>Master</option>
                        <option value='PhD'>PhD</option>
                    </select>
                </div>

                <div class='form-group' id='menteeFieldOfStudyField' style='display:none;'>
                    <label>Field of Study:</label>
                    <input type='text' name='fieldOfStudy' id='editFieldOfStudy'>
                </div>

                <div class='form-group' id='menteeLearningGoalsField' style='display:none;'>
                    <label>Learning Goals:</label>
                    <textarea name='learningGoals' id='editLearningGoals'></textarea>
                </div>

                <div class='form-group' id='menteeMentorIdField' style='display:none;'>
                    <label>Mentor ID:</label>
                    <input type='text' name='mentorId' id='editMentorId'>
                </div>

                <!-- Common Fields -->
                <div class='form-group' id='phoneNumberField' style='display:none;'>
                    <label>Phone Number:</label>
                    <input type='tel' name='phoneNumber' id='editPhoneNumber'>
                </div>

                <div class='form-group' id='statusField' style='display:none;'>
                    <label>Status:</label>
                    <select name='status' id='editStatus'>
                        <option value='Active'>Active</option>
                        <option value='Inactive'>Inactive</option>
                    </select>
                </div>

                <button type='submit'>Update</button>
                <button type='button' onclick='closeEditModal()'>Cancel</button>
            </form>
        </div>
    </div>

    <div class='footer'>
        &copy; 2024 MentorKE. All rights reserved.
    </div>
</body>
</html>


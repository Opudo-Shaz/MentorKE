<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register - MentorKE</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background-color: #f4f8fb; }
        .header { background-color: #0d47a1; color: white; padding: 20px; text-align: center; }
        .container { display: flex; justify-content: center; align-items: center; min-height: 80vh; padding: 20px 0; }
        .card { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); width: 380px; max-height: 90vh; overflow-y: auto; }
        h1 { color: #0d47a1; text-align: center; }
        h3 { color: #0d47a1; font-size: 14px; margin: 15px 0 10px 0; }
        .form-group { margin: 10px 0; }
        .form-group label { display: block; font-weight: bold; margin-bottom: 5px; color: #333; font-size: 12px; }
        input, select, textarea { width: 100%; padding: 10px; margin: 5px 0; border: 1px solid #ccc; border-radius: 5px; box-sizing: border-box; font-family: Arial, sans-serif; }
        textarea { resize: vertical; min-height: 60px; }
        input[type=submit] { background-color: #0d47a1; color: white; border: none; cursor: pointer; font-weight: bold; margin-top: 10px; }
        input[type=submit]:hover { background-color: #1565c0; }
        .footer { text-align: center; padding: 15px; background: #0d47a1; color: white; margin-top: 20px; }
        .back-btn { display: block; text-align: center; margin-top: 15px; padding: 10px; background: #ccc; color: black; text-decoration: none; border-radius: 5px; font-size: 13px; }
        .back-btn:hover { background: #b0bec5; }
        .reflection-info { text-align: center; font-size: 11px; color: #999; margin-top: 10px; }
        .role-section { border-top: 1px solid #ddd; padding-top: 15px; margin-top: 15px; }
    </style>
</head>
<body>
    <div class='header'>
        <h1>MentorKE</h1>
        <p>Create your account</p>
    </div>

    <div class='container'>
        <div class='card'>
            <h1>Register</h1>
            <form action='register' method='post'>

                <%-- DYNAMIC FORM FIELDS USING JAVA REFLECTION --%>
                <%
                    List<String> generatedFields = new ArrayList<>();
                    System.out.println("[RegisterPage.jsp] Using Java Reflection to inspect User class fields");

                    try {
                        Field[] fields = app.model.User.class.getDeclaredFields();
                        System.out.println("[RegisterPage.jsp] Found " + fields.length + " fields in User class");

                        for (Field field : fields) {
                            String fieldName = field.getName();
                            Class<?> fieldType = field.getType();

                            // Skip static fields and internal fields
                            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                                System.out.println("[RegisterPage.jsp] Skipping static field: " + fieldName);
                                continue;
                            }
                            if (fieldName.equals("createdAt") || fieldName.equals("id")) {
                                System.out.println("[RegisterPage.jsp] Skipping auto-generated field: " + fieldName);
                                continue;
                            }

                            generatedFields.add(fieldName);
                            System.out.println("[RegisterPage.jsp] Generating form field for: " + fieldName + " (type: " + fieldType.getSimpleName() + ")");

                            // Determine input type and placeholder
                            String inputType = "text";
                            if (fieldName.contains("email")) {
                                inputType = "email";
                            } else if (fieldName.contains("password")) {
                                inputType = "password";
                            } else if (fieldType == long.class || fieldType == int.class) {
                                inputType = "number";
                            }

                            String placeholder = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                            boolean required = !fieldName.equals("status");
                %>
                    <div class='form-group'>
                        <label for='<%= fieldName %>'><%= placeholder %> <%= required ? "*" : "" %></label>
                        <input type='<%= inputType %>' name='<%= fieldName %>' id='<%= fieldName %>' placeholder='Enter <%= placeholder.toLowerCase() %>' <%= required ? "required" : "" %>>
                    </div>
                <%
                        }
                    } catch (Exception e) {
                        System.err.println("[RegisterPage.jsp] Error during reflection: " + e.getMessage());
                        e.printStackTrace();
                    }
                %>

                <!-- Role Selection (always included) -->
                <div class='form-group role-section'>
                    <label for='role'>Role *</label>
                    <select name='role' id='role' required onchange='toggleRoleFields(this.value)'>
                        <option value=''>Select Role</option>
                        <option value='mentee'>Mentee</option>
                        <option value='mentor'>Mentor</option>
                    </select>
                </div>

                <!-- Mentor-specific fields -->
                <div id='mentorFields' style='display: none;'>
                    <h3>Mentor Information</h3>
                    <div class='form-group'>
                        <label for='specialization'>Specialization *</label>
                        <input type='text' name='specialization' id='specialization' placeholder='e.g., Computer Science'>
                    </div>
                    <div class='form-group'>
                        <label for='expertise'>Expertise *</label>
                        <input type='text' name='expertise' id='expertise' placeholder='e.g., Web Development, Machine Learning'>
                    </div>
                    <div class='form-group'>
                        <label for='yearsOfExperience'>Years of Experience *</label>
                        <input type='number' name='yearsOfExperience' id='yearsOfExperience' placeholder='0' min='0'>
                    </div>
                    <div class='form-group'>
                        <label for='bio'>Bio *</label>
                        <textarea name='bio' id='bio' placeholder='Tell us about yourself'></textarea>
                    </div>
                    <div class='form-group'>
                        <label for='qualifications'>Qualifications *</label>
                        <textarea name='qualifications' id='qualifications' placeholder='List your qualifications and certifications'></textarea>
                    </div>
                    <div class='form-group'>
                        <label for='mentorPhoneNumber'>Phone Number</label>
                        <input type='tel' name='phoneNumber' id='mentorPhoneNumber' placeholder='(Optional)'>
                    </div>
                </div>

                <!-- Mentee-specific fields -->
                <div id='menteeFields' style='display: none;'>
                    <h3>Mentee Information</h3>
                    <div class='form-group'>
                        <label for='educationLevel'>Education Level *</label>
                        <select name='educationLevel' id='educationLevel'>
                            <option value=''>Select Education Level</option>
                            <option value='High School'>High School</option>
                            <option value='Bachelors'>Bachelors</option>
                            <option value='Masters'>Masters</option>
                            <option value='PhD'>PhD</option>
                            <option value='Other'>Other</option>
                        </select>
                    </div>
                    <div class='form-group'>
                        <label for='fieldOfStudy'>Field of Study *</label>
                        <input type='text' name='fieldOfStudy' id='fieldOfStudy' placeholder='e.g., Computer Science, Business'>
                    </div>
                    <div class='form-group'>
                        <label for='learningGoals'>Learning Goals *</label>
                        <textarea name='learningGoals' id='learningGoals' placeholder='What do you want to learn?'></textarea>
                    </div>
                    <div class='form-group'>
                        <label for='menteePhoneNumber'>Phone Number</label>
                        <input type='tel' name='phoneNumber' id='menteePhoneNumber' placeholder='(Optional)'>
                    </div>
                </div>

                <input type='submit' value='Register'>
            </form>

            <script>
            function toggleRoleFields(role) {
                document.getElementById('mentorFields').style.display = (role === 'mentor') ? 'block' : 'none';
                document.getElementById('menteeFields').style.display = (role === 'mentee') ? 'block' : 'none';
                updateRequiredFields(role);
            }
            
            function updateRequiredFields(role) {
                // Set all role-specific fields as not required first
                ['educationLevel', 'fieldOfStudy', 'learningGoals'].forEach(id => {
                    document.getElementById(id).required = false;
                });
                ['specialization', 'expertise', 'yearsOfExperience', 'bio', 'qualifications'].forEach(id => {
                    document.getElementById(id).required = false;
                });
                
                // Set required based on role
                if (role === 'mentee') {
                    ['educationLevel', 'fieldOfStudy', 'learningGoals'].forEach(id => {
                        document.getElementById(id).required = true;
                    });
                } else if (role === 'mentor') {
                    ['specialization', 'expertise', 'yearsOfExperience', 'bio', 'qualifications'].forEach(id => {
                        document.getElementById(id).required = true;
                    });
                }
            }
            
            document.querySelector('form').addEventListener('submit', function(e) {
                const role = document.getElementById('role').value;
                if (role === 'mentor') {
                    if (!document.getElementById('specialization').value.trim()) {
                        alert('Specialization is required');
                        e.preventDefault();
                        return;
                    }
                    if (!document.getElementById('expertise').value.trim()) {
                        alert('Expertise is required');
                        e.preventDefault();
                        return;
                    }
                } else if (role === 'mentee') {
                    if (!document.getElementById('fieldOfStudy').value.trim()) {
                        alert('Field of Study is required');
                        e.preventDefault();
                        return;
                    }
                    if (!document.getElementById('learningGoals').value.trim()) {
                        alert('Learning Goals are required');
                        e.preventDefault();
                        return;
                    }
                }
            });
            </script>

            <p class='reflection-info'>Form fields dynamically generated from User class using Java Reflection (<%= generatedFields.size() %> fields)</p>
            <a href='login' class='back-btn'>Already have an account? Login</a>
            <a href='index' class='back-btn'>Back to Home</a>
        </div>
    </div>

    <div class='footer'>
        &copy; 2024 MentorKE. All rights reserved.
    </div>
</body>
</html>

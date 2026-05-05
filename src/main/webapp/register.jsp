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
        .card { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); width: 320px; }
        h1 { color: #0d47a1; text-align: center; }
        .form-group { margin: 10px 0; }
        .form-group label { display: block; font-weight: bold; margin-bottom: 5px; color: #333; font-size: 12px; }
        input, select { width: 100%; padding: 10px; margin: 5px 0; border: 1px solid #ccc; border-radius: 5px; box-sizing: border-box; }
        input[type=submit] { background-color: #0d47a1; color: white; border: none; cursor: pointer; font-weight: bold; }
        input[type=submit]:hover { background-color: #1565c0; }
        .footer { text-align: center; padding: 15px; background: #0d47a1; color: white; margin-top: 80px; }
        .back-btn { display: block; text-align: center; margin-top: 15px; padding: 10px; background: #ccc; color: black; text-decoration: none; border-radius: 5px; }
        .back-btn:hover { background: #b0bec5; }
        .reflection-info { text-align: center; font-size: 11px; color: #999; margin-top: 10px; }
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
                <div class='form-group'>
                    <label for='role'>Role *</label>
                    <select name='role' id='role' required>
                        <option value=''>Select Role</option>
                        <option value='mentee'>Mentee</option>
                        <option value='mentor'>Mentor</option>
                        <option value='admin'>Admin</option>
                    </select>
                </div>

                <input type='submit' value='Register'>
            </form>
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


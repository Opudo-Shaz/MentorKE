package app.bean.event;

public class UserRegisteredEvent {

    private final String email;
    private final String name;
    private final String role;
    private final String specialization; // Optional field for mentors
    private final String tempPassword;   // Optional - only set for admin-created accounts

    public UserRegisteredEvent(String email, String name, String role) {
        this(email, name, role, null, null);
    }

    public UserRegisteredEvent(String email, String name, String role, String specialization) {
        this(email, name, role, specialization, null);
    }

    public UserRegisteredEvent(String email, String name, String role, String specialization, String tempPassword) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.specialization = specialization;
        this.tempPassword = tempPassword;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getSpecialization() { return specialization; }
    public String getTempPassword() { return tempPassword; }

    /**
     * Convenience check for listeners to decide which email template to use.
     */
    public boolean hasTempPassword() {
        return tempPassword != null && !tempPassword.isEmpty();
    }
}
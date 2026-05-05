package app.bean.event;

public class UserRegisteredEvent {

    private final String email;
    private final String name;
    private final String role;
    private final String specialization; // Optional field for mentors

    public UserRegisteredEvent(String email, String name, String role) {
        this(email, name, role, null);
    }

    public UserRegisteredEvent(String email, String name, String role, String specialization) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.specialization = specialization;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getSpecialization() { return specialization; }
}
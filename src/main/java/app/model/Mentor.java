package app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentors")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true, callSuper = false)
public class Mentor extends User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "mentor")
    @JsonIgnore
    private List<Mentee> mentees = new ArrayList<>();

    @NotBlank
    @Size(max = 100)
    @Column(name = "specialization", length = 100)
    @ToString.Include
    private String specialization;

    @Size(max = 5000)
    @Column(name = "expertise", columnDefinition = "TEXT")
    @ToString.Include
    private String expertise;

    @Min(0)
    @Max(80)
    @Column(name = "years_of_experience")
    @ToString.Include
    private Integer yearsOfExperience;

    @Size(max = 5000)
    @Column(name = "bio", columnDefinition = "TEXT")
    @ToString.Include
    private String bio;

    @Size(max = 5000)
    @Column(name = "qualifications", columnDefinition = "TEXT")
    @ToString.Include
    private String qualifications;

    @Pattern(regexp = "^$|^[0-9+()\\-\\s]{7,20}$")
    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    @ToString.Include
    private String phoneNumber;

    @Builder
    public Mentor(Long id, String userId, String specialization, String expertise,
                  Integer yearsOfExperience, String bio, String qualifications,
                  String phoneNumber, String status) {
        setId(id);
        setUserId(userId);
        this.specialization = specialization;
        this.expertise = expertise;
        this.yearsOfExperience = yearsOfExperience;
        this.bio = bio;
        this.qualifications = qualifications;
        this.phoneNumber = phoneNumber;
        setStatus(status);
    }

    // --- Identity bridge ---

    @Transient
    @ToString.Include(name = "userId", rank = 1)  
    public String getUserId() {
        return getId() != null ? String.valueOf(getId()) : null;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            setId(null);
            return;
        }
        setId(Long.parseLong(userId));
    }

    // --- User bridge ---

    public User getUser() {
        return this;
    }

    public void setUser(User user) {
        if (user == null) {
            setId(null);
            setUsername(null);
            setPassword(null);
            setRole(null);
            setEmail(null);
            setStatus(null);
            setCreatedAt(null);
            setUpdatedAt(null);
            return;
        }
        setId(user.getId());
        setUsername(user.getUsername());
        setPassword(user.getPassword());
        setRole(user.getRole());
        setEmail(user.getEmail());
        setStatus(user.getStatus());
        setCreatedAt(user.getCreatedAt());
        setUpdatedAt(user.getUpdatedAt());
    }
}
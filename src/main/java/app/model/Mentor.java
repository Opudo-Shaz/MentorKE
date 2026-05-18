package app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentors")
public class Mentor extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "mentor")
    private List<Mentee> mentees = new ArrayList<>();

    @NotBlank
    @Size(max = 100)
    @Column(name = "specialization", length = 100)
    private String specialization;

    @Size(max = 5000)
    @Column(name = "expertise", columnDefinition = "TEXT")
    private String expertise;

    @Min(0)
    @Max(80)
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Size(max = 5000)
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Size(max = 5000)
    @Column(name = "qualifications", columnDefinition = "TEXT")
    private String qualifications;

    @Pattern(regexp = "^$|^[0-9+()\\-\\s]{7,20}$")
    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

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

    public Mentor() {
    }

    @Transient
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

    public List<Mentee> getMentees() {
        return mentees;
    }

    public void setMentees(List<Mentee> mentees) {
        this.mentees = mentees;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Mentor{" +
                "id='" + getId() + '\'' +
                ", userId='" + getUserId() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", expertise='" + expertise + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                ", bio='" + bio + '\'' +
                ", qualifications='" + qualifications + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}

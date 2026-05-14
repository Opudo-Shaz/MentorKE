package app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_requests")
public class MatchRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "mentee_id", length = 50, nullable = false)
    private String menteeId;

    @Column(name = "mentor_id", length = 50)
    private String mentorId;

    @Column(name = "requested_specialization", length = 100)
    private String requestedSpecialization;

    @Column(name = "status", length = 50)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public MatchRequest(String menteeId, String mentorId, String requestedSpecialization) {
        this.menteeId = menteeId;
        this.mentorId = mentorId;
        this.requestedSpecialization = requestedSpecialization;
        this.status = "PENDING";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMenteeId() { return menteeId; }
    public void setMenteeId(String menteeId) { this.menteeId = menteeId; }
    public String getMentorId() { return mentorId; }
    public void setMentorId(String mentorId) { this.mentorId = mentorId; }
    public String getRequestedSpecialization() { return requestedSpecialization; }
    public void setRequestedSpecialization(String requestedSpecialization) { this.requestedSpecialization = requestedSpecialization; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "MatchRequest{" +
                "id='" + id + '\'' +
                ", menteeId='" + menteeId + '\'' +
                ", mentorId='" + mentorId + '\'' +
                ", requestedSpecialization='" + requestedSpecialization + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

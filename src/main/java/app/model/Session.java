package app.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Session implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ToString.Include
    private Long id;

    @Column(name = "mentor_id", length = 50, nullable = false)
    @ToString.Include
    private String mentorId;

    @Column(name = "mentee_id", length = 50, nullable = false)
    @ToString.Include
    private String menteeId;

    @Column(name = "session_link", length = 255)
    private String sessionLink;               

    @Column(name = "scheduled_date", nullable = false)
    @ToString.Include
    private LocalDateTime scheduledDate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;          

    @Column(name = "status", length = 50)
    @ToString.Include
    private String status;

    @Column(name = "topic", columnDefinition = "TEXT")
    @ToString.Include
    private String topic;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;                     // excluded — TEXT field, can be large

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Session(String mentorId, String menteeId, LocalDateTime scheduledDate,
                   Integer durationMinutes, String topic) {
        this.mentorId = mentorId;
        this.menteeId = menteeId;
        this.scheduledDate = scheduledDate;
        this.durationMinutes = durationMinutes;
        this.topic = topic;
        this.status = "PENDING";              // default preserved
    }
}
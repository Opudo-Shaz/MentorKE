package app.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_requests")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class MatchRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ToString.Include
    private Long id;

    @Column(name = "mentee_id", length = 50, nullable = false)
    @ToString.Include
    private String menteeId;

    @Column(name = "mentor_id", length = 50)
    @ToString.Include
    private String mentorId;

    @Column(name = "requested_specialization", length = 100)
    @ToString.Include
    private String requestedSpecialization;

    @Column(name = "status", length = 50)
    @ToString.Include
    private String status;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public MatchRequest(String menteeId, String mentorId, String requestedSpecialization) {
        this.menteeId = menteeId;
        this.mentorId = mentorId;
        this.requestedSpecialization = requestedSpecialization;
        this.status = "PENDING";
    }
}
package app.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "goal_progress_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalProgressLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private MenteeGoal goal;

    private String note;

    @Column(name = "progress_snapshot")
    private int progressSnapshot;

    @Column(name = "logged_at")
    private LocalDateTime loggedAt;

    @PrePersist
    public void prePersist() {
        loggedAt = LocalDateTime.now();
    }
}
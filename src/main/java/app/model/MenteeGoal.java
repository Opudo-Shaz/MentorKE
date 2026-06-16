package app.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentee_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenteeGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mentee_id", nullable = false)
    private Long menteeId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "status")
    private String status = "IN_PROGRESS";

    @Column(name = "overall_progress")
    private int overallProgress = 0;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<GoalMilestone> milestones = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "IN_PROGRESS";
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateProgress(int progress) {
        this.overallProgress = Math.min(Math.max(progress, 0), 100);
        if (this.overallProgress == 100) this.status = "COMPLETED";
    }

    public void recalculateProgressFromMilestones() {
        if (milestones == null || milestones.isEmpty()) return;
        long completed = milestones.stream().filter(GoalMilestone::isCompleted).count();
        int progress = (int) ((completed * 100) / milestones.size());
        updateProgress(progress);
    }
}
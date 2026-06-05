package app.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_trail")
@Getter
@Setter
@ToString(exclude = "details")        
@NoArgsConstructor
public class AuditTrail implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", length = 50, nullable = false)
    private String entityType;

    @Column(name = "entity_id", length = 100, nullable = false)
    private String entityId;

    @Column(name = "operation", length = 20, nullable = false)
    private String operation;

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)          // timestamp is managed by Hibernate, block external sets
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Builder
    public AuditTrail(String entityType, String entityId, String operation, String userId, String details) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.operation = operation;
        this.userId = userId;
        this.details = details;
    }
}
package app.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ToString.Include
    private Long id;

    @Column(name = "sender_id", length = 50, nullable = false)
    @ToString.Include
    private String senderId;

    @Column(name = "recipient_id", length = 50, nullable = false)
    @ToString.Include
    private String recipientId;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;                   // excluded from toString — body can be large

    @Column(name = "is_read")
    @ToString.Include
    private Boolean isRead;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", nullable = false)
    @ToString.Include
    private LocalDateTime createdAt;

    @Builder
    public Message(String senderId, String recipientId, String message) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.message = message;
        this.isRead = false;                  // default preserved
    }
}
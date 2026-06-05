package app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ToString.Include
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "username", length = 100, nullable = false, unique = true)
    @ToString.Include
    private String username;

    @NotBlank
    @Size(max = 255)
    @Column(name = "password", length = 255, nullable = false)
    private String password;                  // excluded from toString — never log passwords

    @NotBlank
    @Size(max = 50)
    @Column(name = "role", length = 50, nullable = false)
    @ToString.Include
    private String role;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(name = "email", length = 150, nullable = false, unique = true)
    @ToString.Include
    private String email;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    @ToString.Include
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public User(Long id, String username, String password, String role, String email, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.status = status;
    }
}
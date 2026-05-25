package app.soapapi;

import app.model.User;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * SOAP DTO for User information
 */
@Getter
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserSoapDto {

    // Getters and Setters
    @XmlElement
    private Long id;

    @XmlElement
    private String username;

    @XmlElement
    private String email;

    @XmlElement
    private String role;

    @XmlElement
    private String status;

    @XmlElement
    private LocalDateTime createdAt;

    @XmlElement
    private LocalDateTime updatedAt;

    public UserSoapDto() {}

    public UserSoapDto(Long id, String username, String email, String role, String status, 
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserSoapDto fromEntity(User user) {
        return new UserSoapDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}


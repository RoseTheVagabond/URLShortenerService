package org.example.s29624tpo11.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

@Entity
@Table(name = "links")
public class Link {

    @Id
    @Column(length = 10)
    private String id;

    @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters long")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Pattern(regexp = "^https://.*", message = "URL must start with https://")
    @Column(name = "target_url", nullable = false)
    @UniqueElements(message = "Link with this URL already exists in the system")
    private String targetUrl;

    @Column(name = "password")
    @Pattern(regexp = "^.*[a-z].*$", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = "^(.*[A-Z]){2,}.*$", message = "Password must contain at least two uppercase letters")
    @Pattern(regexp = "^(.*[0-9]){3,}.*$", message = "Password must contain at least three digits")
    @Pattern(regexp = "^(.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]){4,}.*$", message = "Password must contain at least four special characters")
    @Length(min = 10, message = "Password must be at least 10 characters long")
    private String password;

    @Column(name = "visits", nullable = false)
    private Long visits = 0L;

    public Link() {}

    public Link(String id, String name, String targetUrl, String password) {
        this.id = id;
        this.name = name;
        this.targetUrl = targetUrl;
        this.password = password;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getVisits() { return visits; }
    public void setVisits(Long visits) { this.visits = visits; }

    public boolean hasPassword() {
        return password != null && !password.isEmpty();
    }

    public void incrementVisits() {
        this.visits++;
    }
}
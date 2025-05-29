package org.example.s29624tpo11.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "links", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Link {

    @Id
    @Column(length = 10)
    private String id;

    @NotBlank
    @Size(max = 200)
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    @Column(name = "target_url", nullable = false)
    private String targetUrl;

    @Column(name = "password")
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
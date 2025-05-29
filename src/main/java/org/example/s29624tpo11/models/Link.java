package org.example.s29624tpo11.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "links")
public class Link {

    @Id
    @Column(length = 10)
    private String id;

    @Size(min = 5, max = 20, message = "{name.size}")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Pattern(regexp = "^https://.*", message = "{url.https}")
    @Column(name = "target_url", nullable = false, unique = true)
    private String targetUrl;

    @Column(name = "password")
    @Pattern.List({
            @Pattern(regexp = "^$|^.{10,}$", message = "{password.length.min}"),
            @Pattern(regexp = "^$|^.*[a-z].*$", message = "{password.lowercase.required}"),
            @Pattern(regexp = "^$|^(.*[A-Z]){2,}.*$", message = "{password.uppercase.min}"),
            @Pattern(regexp = "^$|^(.*[0-9]){3,}.*$", message = "{password.digits.min}"),
            @Pattern(regexp = "^$|^(.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]){4,}.*$", message = "{password.special.min}")
    })
    @Length(max = 100, message = "Password cannot exceed 100 characters")
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
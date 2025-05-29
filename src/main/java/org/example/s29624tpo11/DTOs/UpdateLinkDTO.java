package org.example.s29624tpo11.DTOs;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateLinkDTO {

    @Size(max = 200, message = "Name cannot exceed 200 characters")
    private String name;

    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    private String targetUrl;

    @Size(max = 100, message = "Password cannot exceed 100 characters")
    private String password;

    public UpdateLinkDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}


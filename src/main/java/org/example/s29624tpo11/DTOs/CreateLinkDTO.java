package org.example.s29624tpo11.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public class CreateLinkDTO {

    @NotBlank(message = "{name.notblank}")
    @Size(min = 5, max = 20, message = "{name.size}")
    private String name;

    @NotBlank(message = "{url.notblank}")
    @Pattern(regexp = "^https://.*", message = "{url.https}")
    private String targetUrl;

    @Pattern.List({
            @Pattern(regexp = "^$|^.{10,}$", message = "{password.length.min}"),
            @Pattern(regexp = "^$|^.*[a-z].*$", message = "{password.lowercase.required}"),
            @Pattern(regexp = "^$|^(.*[A-Z]){2,}.*$", message = "{password.uppercase.min}"),
            @Pattern(regexp = "^$|^(.*[0-9]){3,}.*$", message = "{password.digits.min}"),
            @Pattern(regexp = "^$|^(.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]){4,}.*$", message = "{password.special.min}")
    })
    @Length(max = 100, message = "Password cannot exceed 100 characters")
    private String password;

    public CreateLinkDTO() {}

    public CreateLinkDTO(String name, String targetUrl, String password) {
        this.name = name;
        this.targetUrl = targetUrl;
        this.password = password;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
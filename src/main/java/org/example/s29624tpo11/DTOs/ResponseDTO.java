package org.example.s29624tpo11.DTOs;

import org.example.s29624tpo11.models.Link;

public class ResponseDTO {

    private String id;
    private String name;
    private String targetUrl;
    private String redirectUrl;
    private Long visits;

    public ResponseDTO() {}

    public ResponseDTO(Link link, String baseUrl) {
        this.id = link.getId();
        this.name = link.getName();
        this.targetUrl = link.getTargetUrl();
        this.redirectUrl = baseUrl + "/red/" + link.getId();
        this.visits = link.getVisits();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }

    public Long getVisits() { return visits; }
    public void setVisits(Long visits) { this.visits = visits; }
}

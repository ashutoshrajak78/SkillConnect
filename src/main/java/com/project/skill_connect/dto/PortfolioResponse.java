package com.project.skill_connect.dto;

import java.time.LocalDateTime;

public class PortfolioResponse {

    private Long id;
    private String title;
    private String description;
    private String technologies;
    private String projectUrl;
    private String githubUrl;
    private Long freelancerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PortfolioResponse(
            Long id,
            String title,
            String description,
            String technologies,
            String projectUrl,
            String githubUrl,
            Long freelancerId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.technologies = technologies;
        this.projectUrl = projectUrl;
        this.githubUrl = githubUrl;
        this.freelancerId = freelancerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTechnologies() {
        return technologies;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public Long getFreelancerId() {
        return freelancerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
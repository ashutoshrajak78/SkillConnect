package com.project.skill_connect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PortfolioRequest {

    @NotBlank(message = "Project title is required")
    @Size(max = 150)
    private String title;

    @NotBlank(message = "Project description is required")
    @Size(max = 2000)
    private String description;

    @NotBlank(message = "Technologies are required")
    @Size(max = 500)
    private String technologies;

    private String projectUrl;

    private String githubUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }
}
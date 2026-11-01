package com.project.skill_connect.dto;

import java.time.LocalDateTime;

import com.project.skill_connect.entities.ApplicationStatus;

public class RecentApplicationResponse {

    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private Long freelancerId;
    private String freelancerName;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    public RecentApplicationResponse(
            Long applicationId,
            Long jobId,
            String jobTitle,
            Long freelancerId,
            String freelancerName,
            ApplicationStatus status,
            LocalDateTime appliedAt) {

        this.applicationId = applicationId;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.freelancerId = freelancerId;
        this.freelancerName = freelancerName;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public Long getFreelancerId() {
        return freelancerId;
    }

    public String getFreelancerName() {
        return freelancerName;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }
}
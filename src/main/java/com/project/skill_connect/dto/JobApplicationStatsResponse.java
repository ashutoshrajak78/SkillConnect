package com.project.skill_connect.dto;

public class JobApplicationStatsResponse {

    private Long jobId;
    private String jobTitle;
    private long applicationCount;

    public JobApplicationStatsResponse(
            Long jobId,
            String jobTitle,
            long applicationCount) {

        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.applicationCount = applicationCount;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public long getApplicationCount() {
        return applicationCount;
    }
}
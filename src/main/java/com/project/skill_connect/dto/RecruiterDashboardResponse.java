package com.project.skill_connect.dto;

import java.util.List;

public class RecruiterDashboardResponse {

    private long totalJobs;
    private long totalApplications;
    private long pendingApplications;
    private long acceptedApplications;
    private long rejectedApplications;

    private List<RecentApplicationResponse> recentApplications;
    private List<JobApplicationStatsResponse> jobStatistics;

    public RecruiterDashboardResponse(
            long totalJobs,
            long totalApplications,
            long pendingApplications,
            long acceptedApplications,
            long rejectedApplications,
            List<RecentApplicationResponse> recentApplications,
            List<JobApplicationStatsResponse> jobStatistics) {

        this.totalJobs = totalJobs;
        this.totalApplications = totalApplications;
        this.pendingApplications = pendingApplications;
        this.acceptedApplications = acceptedApplications;
        this.rejectedApplications = rejectedApplications;
        this.recentApplications = recentApplications;
        this.jobStatistics = jobStatistics;
    }

    public long getTotalJobs() {
        return totalJobs;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public long getPendingApplications() {
        return pendingApplications;
    }

    public long getAcceptedApplications() {
        return acceptedApplications;
    }

    public long getRejectedApplications() {
        return rejectedApplications;
    }

    public List<RecentApplicationResponse> getRecentApplications() {
        return recentApplications;
    }

    public List<JobApplicationStatsResponse> getJobStatistics() {
        return jobStatistics;
    }
}
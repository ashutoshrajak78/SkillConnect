package com.project.skill_connect.dto;

public class AdminDashboardResponse {

    private long totalUsers;
    private long totalAdmins;
    private long totalRecruiters;
    private long totalFreelancers;
    private long totalClients;

    private long totalJobs;
    private long totalApplications;
    private long pendingApplications;
    private long acceptedApplications;
    private long rejectedApplications;

    public AdminDashboardResponse(
            long totalUsers,
            long totalAdmins,
            long totalRecruiters,
            long totalFreelancers,
            long totalClients,
            long totalJobs,
            long totalApplications,
            long pendingApplications,
            long acceptedApplications,
            long rejectedApplications) {

        this.totalUsers = totalUsers;
        this.totalAdmins = totalAdmins;
        this.totalRecruiters = totalRecruiters;
        this.totalFreelancers = totalFreelancers;
        this.totalClients = totalClients;
        this.totalJobs = totalJobs;
        this.totalApplications = totalApplications;
        this.pendingApplications = pendingApplications;
        this.acceptedApplications = acceptedApplications;
        this.rejectedApplications = rejectedApplications;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalAdmins() {
        return totalAdmins;
    }

    public long getTotalRecruiters() {
        return totalRecruiters;
    }

    public long getTotalFreelancers() {
        return totalFreelancers;
    }

    public long getTotalClients() {
        return totalClients;
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
}
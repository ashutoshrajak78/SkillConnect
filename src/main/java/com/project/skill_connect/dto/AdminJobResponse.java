package com.project.skill_connect.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.project.skill_connect.entities.Job;
import com.project.skill_connect.entities.JobStatus;

public class AdminJobResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private BigDecimal salary;
    private String employmentType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private JobStatus status;

    private Long recruiterId;
    private String recruiterName;
    private String recruiterEmail;

    public AdminJobResponse(Job job) {
        this.id = job.getId();
        this.title = job.getTitle();
        this.description = job.getDescription();
        this.location = job.getLocation();
        this.salary = job.getSalary();
        this.employmentType = job.getEmploymentType();
        this.createdAt = job.getCreatedAt();
        this.updatedAt = job.getUpdatedAt();
        this.status = job.getStatus();

        if (job.getRecruiter() != null) {
            this.recruiterId = job.getRecruiter().getId();
            this.recruiterName = job.getRecruiter().getName();
            this.recruiterEmail = job.getRecruiter().getEmail();
        }
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public BigDecimal getSalary() { return salary; }
    public String getEmploymentType() { return employmentType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public JobStatus getStatus() { return status; }
    public Long getRecruiterId() { return recruiterId; }
    public String getRecruiterName() { return recruiterName; }
    public String getRecruiterEmail() { return recruiterEmail; }
}

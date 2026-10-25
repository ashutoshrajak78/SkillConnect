package com.project.skill_connect.dto;

import java.time.LocalDateTime;

import com.project.skill_connect.entities.ApplicationStatus;
import com.project.skill_connect.entities.JobApplication;

public class JobApplicationResponse {

private Long id;
private Long jobId;
private String jobTitle;
private String freelancerName;
private String freelancerEmail;
private String coverLetter;
private ApplicationStatus status;
private LocalDateTime appliedAt;

public JobApplicationResponse(JobApplication application) {
    this.id = application.getId();
    this.jobId = application.getJob().getId();
    this.jobTitle = application.getJob().getTitle();
    this.freelancerName = application.getFreelancer().getName();
    this.freelancerEmail = application.getFreelancer().getEmail();
    this.coverLetter = application.getCoverLetter();
    this.status = application.getStatus();
    this.appliedAt = application.getAppliedAt();
}

public Long getId() {
    return id;
}

public Long getJobId() {
    return jobId;
}

public String getJobTitle() {
    return jobTitle;
}

public String getFreelancerName() {
    return freelancerName;
}

public String getFreelancerEmail() {
    return freelancerEmail;
}

public String getCoverLetter() {
    return coverLetter;
}

public ApplicationStatus getStatus() {
    return status;
}

public LocalDateTime getAppliedAt() {
    return appliedAt;
}

}

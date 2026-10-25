package com.project.skill_connect.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(
name = "job_applications",
uniqueConstraints = {
@UniqueConstraint(
name = "uk_freelancer_job",
columnNames = {"freelancer_id", "job_id"}
)
}
)
public class JobApplication {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "freelancer_id", nullable = false)
private User freelancer;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "job_id", nullable = false)
private Job job;

@NotBlank(message = "Cover letter cannot be empty")
@Size(max = 2000, message = "Cover letter cannot exceed 2000 characters")
@Column(nullable = false, length = 2000)
private String coverLetter;

@Enumerated(EnumType.STRING)
@Column(nullable = false)
private ApplicationStatus status = ApplicationStatus.PENDING;

@Column(nullable = false, updatable = false)
private LocalDateTime appliedAt;

@PrePersist
protected void onCreate() {
    appliedAt = LocalDateTime.now();
}

public Long getId() {
    return id;
}

public User getFreelancer() {
    return freelancer;
}

public void setFreelancer(User freelancer) {
    this.freelancer = freelancer;
}

public Job getJob() {
    return job;
}

public void setJob(Job job) {
    this.job = job;
}

public String getCoverLetter() {
    return coverLetter;
}

public void setCoverLetter(String coverLetter) {
    this.coverLetter = coverLetter;
}

public ApplicationStatus getStatus() {
    return status;
}

public void setStatus(ApplicationStatus status) {
    this.status = status;
}

public LocalDateTime getAppliedAt() {
    return appliedAt;
}


}

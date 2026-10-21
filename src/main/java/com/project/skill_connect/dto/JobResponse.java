package com.project.skill_connect.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private BigDecimal salary;
    private String employmentType;
    private String recruiterName;
    private LocalDateTime createdAt;

    public JobResponse() {
    }

    public JobResponse(
            Long id,
            String title,
            String description,
            String location,
            BigDecimal salary,
            String employmentType,
            String recruiterName,
            LocalDateTime createdAt) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.salary = salary;
        this.employmentType = employmentType;
        this.recruiterName = recruiterName;
        this.createdAt = createdAt;
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

    public String getLocation() {
        return location;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
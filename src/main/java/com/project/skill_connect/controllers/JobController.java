
package com.project.skill_connect.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.project.skill_connect.dto.JobRequest;
import com.project.skill_connect.dto.JobResponse;

import com.project.skill_connect.services.JobService;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Create a job - Recruiter only
    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping
    public JobResponse createJob(@Valid @RequestBody JobRequest request, Principal principal) {
        return jobService.createJob(request, principal.getName());
    }

    // Get all jobs - Any authenticated user
    @GetMapping
    public List<JobResponse> getAllJobs() {
        return jobService.getAllJobs();
    }

    // Update a job - Only the job owner can update it
    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/{id}")
    public JobResponse updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request,
            Principal principal) {

        return jobService.updateJob(id, request, principal.getName());
    }

    // Delete a job - Only the job owner can delete it

    @PreAuthorize("hasRole('RECRUITER')")
    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            Principal principal) {

        jobService.deleteJob(id, principal.getName());

        return org.springframework.http.ResponseEntity.noContent().build();
    }


    // Get job by ID - Any authenticated user
    @GetMapping("/{id}")
    public JobResponse getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }
}



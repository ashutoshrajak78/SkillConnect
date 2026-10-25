
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.project.skill_connect.dto.JobRequest;
import com.project.skill_connect.dto.JobResponse;

import com.project.skill_connect.services.JobService;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.data.domain.Page;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.util.Set;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/jobs")
@Validated
public class JobController {

    private final JobService jobService;

    private static final Set<String> ALLOWED_SORT_FIELDS =
        Set.of("createdAt", "salary", "title");

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

    @GetMapping("/search")
    public Page<JobResponse> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String employmentType,
           @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 100, message = "Page size cannot exceed 100")
            int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

                if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid sort field. Allowed fields: createdAt, salary, title"
                    );
                }

                if (!direction.equalsIgnoreCase("asc")
                        && !direction.equalsIgnoreCase("desc")) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid sort direction. Allowed values: asc, desc"
                    );
                }

        return jobService.searchJobs(
                title,
                location,
                employmentType,
                page,
                size,
                sortBy,
                direction
        );
    }

    
}



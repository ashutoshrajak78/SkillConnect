package com.project.skill_connect.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.skill_connect.dto.JobApplicationRequest;
import com.project.skill_connect.dto.JobApplicationResponse;
import com.project.skill_connect.dto.UpdateApplicationStatusRequest;
import com.project.skill_connect.services.JobApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

private final JobApplicationService applicationService;

public JobApplicationController(JobApplicationService applicationService) {
    this.applicationService = applicationService;
}

// Freelancer applies for a job
@PreAuthorize("hasRole('FREELANCER')")
@PostMapping("/jobs/{jobId}")
public JobApplicationResponse applyForJob(
        @PathVariable Long jobId,
        @Valid @RequestBody JobApplicationRequest request,
        Principal principal) {

    return applicationService.applyForJob(
            jobId,
            request.getCoverLetter(),
            principal.getName()
    );
}

// Freelancer views their own applications
@PreAuthorize("hasRole('FREELANCER')")
@GetMapping("/my")
public List<JobApplicationResponse> getMyApplications(Principal principal) {
    return applicationService.getMyApplications(principal.getName());
}

@PreAuthorize("hasRole('RECRUITER')")
@GetMapping("/job/{jobId}")
public List<JobApplicationResponse> getApplicationsForJob(
        @PathVariable Long jobId,
        Principal principal) {

    return applicationService.getApplicationsForJob(
            jobId,
            principal.getName()
    );
}

@PreAuthorize("hasRole('RECRUITER')")
@PatchMapping("/{applicationId}/status")
public JobApplicationResponse updateApplicationStatus(
        @PathVariable Long applicationId,
        @Valid @RequestBody UpdateApplicationStatusRequest request,
        Principal principal) {

    return applicationService.updateApplicationStatus(
            applicationId,
            request.getStatus(),
            principal.getName()
    );
}

}

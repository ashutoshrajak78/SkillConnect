package com.project.skill_connect.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.skill_connect.entities.JobStatus;
import com.project.skill_connect.services.AdminJobModerationService;
import com.project.skill_connect.dto.AdminJobResponse;

@RestController
@RequestMapping("/api/admin/jobs")
public class AdminJobModerationController {

    private final AdminJobModerationService moderationService;

    public AdminJobModerationController(
            AdminJobModerationService moderationService) {
        this.moderationService = moderationService;
    }

    // Get jobs by status
    @GetMapping
    public ResponseEntity<Page<AdminJobResponse>> getJobsByStatus(
            @RequestParam(defaultValue = "PENDING") JobStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending());

        return ResponseEntity.ok(
                moderationService.getJobsByStatus(status, pageable));
    }

    // Approve job
    @PatchMapping("/{jobId}/approve")
    public ResponseEntity<AdminJobResponse> approveJob(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                moderationService.approveJob(jobId));
    }

    // Reject job
    @PatchMapping("/{jobId}/reject")
    public ResponseEntity<AdminJobResponse> rejectJob(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                moderationService.rejectJob(jobId));
    }
}
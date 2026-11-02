package com.project.skill_connect.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.skill_connect.dto.AdminJobResponse;
import com.project.skill_connect.entities.Job;
import com.project.skill_connect.entities.JobStatus;
import com.project.skill_connect.entities.Role;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.exceptions.ResourceNotFoundException;
import com.project.skill_connect.repositories.JobRepository;
import com.project.skill_connect.repositories.UserRepository;

@Service
public class AdminJobModerationService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public AdminJobModerationService(
            JobRepository jobRepository,
            UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    private void checkAdminAccess() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException(
                    "Only admins can moderate jobs");
        }
    }

    // Get jobs by status
    @Transactional(readOnly = true)
    public Page<AdminJobResponse> getJobsByStatus(
            JobStatus status, Pageable pageable) {

        checkAdminAccess();

        return jobRepository.findByStatus(status, pageable)
                .map(AdminJobResponse::new);
    }

    // Approve a job
    @Transactional
    public AdminJobResponse approveJob(Long jobId) {

        checkAdminAccess();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job not found with id: " + jobId));

        job.setStatus(JobStatus.APPROVED);

        Job savedJob = jobRepository.save(job);

        return new AdminJobResponse(savedJob);
    }

    // Reject a job
    @Transactional
    public AdminJobResponse rejectJob(Long jobId) {

        checkAdminAccess();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job not found with id: " + jobId));

        job.setStatus(JobStatus.REJECTED);

        Job savedJob = jobRepository.save(job);

        return new AdminJobResponse(savedJob);
    }
}
package com.project.skill_connect.services;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.skill_connect.dto.AdminDashboardResponse;
import com.project.skill_connect.entities.ApplicationStatus;
import com.project.skill_connect.entities.Role;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.exceptions.ResourceNotFoundException;
import com.project.skill_connect.repositories.JobApplicationRepository;
import com.project.skill_connect.repositories.JobRepository;
import com.project.skill_connect.repositories.UserRepository;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            JobRepository jobRepository,
            JobApplicationRepository jobApplicationRepository) {

        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException(
                    "Only administrators can access this dashboard");
        }

        long totalUsers = userRepository.count();

        long totalAdmins = userRepository.countByRole(Role.ADMIN);

        long totalRecruiters = userRepository.countByRole(Role.RECRUITER);

        long totalFreelancers = userRepository.countByRole(Role.FREELANCER);

        long totalClients = userRepository.countByRole(Role.CLIENT);

        long totalJobs = jobRepository.count();

        long totalApplications = jobApplicationRepository.count();

        long pendingApplications =
                jobApplicationRepository.countByStatus(
                        ApplicationStatus.PENDING);

        long acceptedApplications =
                jobApplicationRepository.countByStatus(
                        ApplicationStatus.ACCEPTED);

        long rejectedApplications =
                jobApplicationRepository.countByStatus(
                        ApplicationStatus.REJECTED);

        return new AdminDashboardResponse(
                totalUsers,
                totalAdmins,
                totalRecruiters,
                totalFreelancers,
                totalClients,
                totalJobs,
                totalApplications,
                pendingApplications,
                acceptedApplications,
                rejectedApplications
        );
    }
}
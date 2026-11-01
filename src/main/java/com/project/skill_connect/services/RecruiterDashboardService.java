package com.project.skill_connect.services;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.skill_connect.dto.JobApplicationStatsResponse;
import com.project.skill_connect.dto.RecentApplicationResponse;
import com.project.skill_connect.dto.RecruiterDashboardResponse;
import com.project.skill_connect.entities.ApplicationStatus;
import com.project.skill_connect.entities.Job;
import com.project.skill_connect.entities.JobApplication;
import com.project.skill_connect.entities.Role;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.exceptions.ResourceNotFoundException;
import com.project.skill_connect.repositories.JobApplicationRepository;
import com.project.skill_connect.repositories.JobRepository;
import com.project.skill_connect.repositories.UserRepository;

@Service
public class RecruiterDashboardService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    public RecruiterDashboardService(
            JobRepository jobRepository,
            JobApplicationRepository jobApplicationRepository,
            UserRepository userRepository) {

        this.jobRepository = jobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public RecruiterDashboardResponse getDashboard() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (recruiter.getRole() != Role.RECRUITER) {
            throw new AccessDeniedException(
                    "Only recruiters can access the dashboard");
        }

        Long recruiterId = recruiter.getId();

        long totalJobs = jobRepository.countByRecruiterId(recruiterId);

        long totalApplications =
                jobApplicationRepository.countByJobRecruiterId(recruiterId);

        long pendingApplications =
                jobApplicationRepository.countByJobRecruiterIdAndStatus(
                        recruiterId, ApplicationStatus.PENDING);

        long acceptedApplications =
                jobApplicationRepository.countByJobRecruiterIdAndStatus(
                        recruiterId, ApplicationStatus.ACCEPTED);

        long rejectedApplications =
                jobApplicationRepository.countByJobRecruiterIdAndStatus(
                        recruiterId, ApplicationStatus.REJECTED);

        List<RecentApplicationResponse> recentApplications =
                jobApplicationRepository
                        .findTop5ByJobRecruiterIdOrderByAppliedAtDesc(recruiterId)
                        .stream()
                        .map(this::mapToRecentApplication)
                        .toList();

        Map<Long, Long> applicationCounts =
                jobApplicationRepository.countApplicationsPerJob(recruiterId)
                        .stream()
                        .collect(Collectors.toMap(
                                row -> (Long) row[0],
                                row -> (Long) row[1]
                        ));

        List<JobApplicationStatsResponse> jobStatistics =
                jobRepository.findByRecruiterId(recruiterId)
                        .stream()
                        .map(job -> new JobApplicationStatsResponse(
                                job.getId(),
                                job.getTitle(),
                                applicationCounts.getOrDefault(job.getId(), 0L)
                        ))
                        .toList();

        return new RecruiterDashboardResponse(
                totalJobs,
                totalApplications,
                pendingApplications,
                acceptedApplications,
                rejectedApplications,
                recentApplications,
                jobStatistics
        );
    }

    private RecentApplicationResponse mapToRecentApplication(
            JobApplication application) {

        return new RecentApplicationResponse(
                application.getId(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getFreelancer().getId(),
                application.getFreelancer().getName(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}
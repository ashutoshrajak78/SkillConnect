package com.project.skill_connect.services;

import java.util.List;
import java.util.Objects;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.skill_connect.entities.ApplicationStatus;
import com.project.skill_connect.entities.Job;
import com.project.skill_connect.entities.JobApplication;
import com.project.skill_connect.entities.Role;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.exceptions.DuplicateApplicationException;
import com.project.skill_connect.exceptions.ResourceNotFoundException;
import com.project.skill_connect.repositories.JobApplicationRepository;
import com.project.skill_connect.repositories.JobRepository;
import com.project.skill_connect.repositories.UserRepository;

import com.project.skill_connect.dto.JobApplicationResponse;

@Service
public class JobApplicationService {

private final JobApplicationRepository applicationRepository;
private final JobRepository jobRepository;
private final UserRepository userRepository;

public JobApplicationService(
        JobApplicationRepository applicationRepository,
        JobRepository jobRepository,
        UserRepository userRepository) {
    this.applicationRepository = applicationRepository;
    this.jobRepository = jobRepository;
    this.userRepository = userRepository;
}

// Freelancer applies for a job
@Transactional
public JobApplicationResponse applyForJob(
        Long jobId, String coverLetter, String freelancerEmail) {

    User freelancer = userRepository.findByEmail(freelancerEmail)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Freelancer not found"));

    if (freelancer.getRole() != Role.FREELANCER) {
        throw new AccessDeniedException(
                "Only freelancers can apply for jobs");
    }

    Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Job not found with ID: " + jobId));

    if (applicationRepository.existsByFreelancerIdAndJobId(
            freelancer.getId(), jobId)) {
        throw new DuplicateApplicationException(
                "You have already applied for this job");
    }

    JobApplication application = new JobApplication();
    application.setFreelancer(freelancer);
    application.setJob(job);
    application.setCoverLetter(coverLetter);
    application.setStatus(ApplicationStatus.PENDING);

   JobApplication savedApplication = applicationRepository.save(application);
        return new JobApplicationResponse(savedApplication);
}

// Freelancer views their own applications
@Transactional(readOnly = true)
public List<JobApplicationResponse> getMyApplications(String freelancerEmail) {

    User freelancer = userRepository.findByEmail(freelancerEmail)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Freelancer not found"));

    if (freelancer.getRole() != Role.FREELANCER) {
        throw new AccessDeniedException(
                "Only freelancers can view their applications");
    }

    return applicationRepository.findByFreelancerId(freelancer.getId()).stream()
            .map(JobApplicationResponse::new)
            .toList();
}

public List<JobApplicationResponse> getApplicationsForJob(
        Long jobId, String recruiterEmail) {

    Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Job not found with id: " + jobId));

    if (!Objects.equals(
            job.getRecruiter().getEmail(), recruiterEmail)) {
        throw new AccessDeniedException(
                "You are not authorized to view applications for this job");
    }

    return applicationRepository.findByJobId(jobId)
            .stream()
            .map(JobApplicationResponse::new)
            .toList();
}

public JobApplicationResponse updateApplicationStatus(
        Long applicationId,
        ApplicationStatus status,
        String recruiterEmail) {

    JobApplication application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Application not found with id: " + applicationId));

    String jobOwnerEmail = application.getJob()
            .getRecruiter()
            .getEmail();

    if (!Objects.equals(jobOwnerEmail, recruiterEmail)) {
        throw new AccessDeniedException(
                "You are not authorized to update this application");
    }

    application.setStatus(status);

    JobApplication updatedApplication = applicationRepository.save(application);

    return new JobApplicationResponse(updatedApplication);
}

}

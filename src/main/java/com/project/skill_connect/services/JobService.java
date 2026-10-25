
package com.project.skill_connect.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.skill_connect.entities.Job;
import com.project.skill_connect.repositories.JobRepository;

import com.project.skill_connect.dto.JobRequest;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.repositories.UserRepository;

import com.project.skill_connect.dto.JobResponse;
import java.util.Objects;
import com.project.skill_connect.exceptions.ResourceNotFoundException;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // Get all jobs
    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(this::toJobResponse)
                .toList();
    }

    // Get a job by ID
    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found with ID: " + id));

        return toJobResponse(job);
    }

    // Create a job
   @Transactional
    public JobResponse createJob(JobRequest request, String recruiterEmail) {

        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        if (recruiter.getRole() != com.project.skill_connect.entities.Role.RECRUITER) {
            throw new IllegalStateException("Only recruiters can create jobs");
        }

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setEmploymentType(request.getEmploymentType());
        job.setRecruiter(recruiter);

        Job savedJob = jobRepository.save(job);
        return toJobResponse(savedJob);
    }

    @Transactional
    public JobResponse updateJob(Long jobId, JobRequest request, String recruiterEmail) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        if (!Objects.equals(job.getRecruiter().getEmail(), recruiterEmail)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only update your own jobs");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setEmploymentType(request.getEmploymentType());

        Job updatedJob = jobRepository.save(job);

        return toJobResponse(updatedJob);
    }


    @Transactional
    public void deleteJob(Long jobId, String recruiterEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        if (!Objects.equals(job.getRecruiter().getEmail(), recruiterEmail)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only delete your own jobs");
        }

        jobRepository.delete(job);
    }


    private JobResponse toJobResponse(Job job) {
    return new JobResponse(
            job.getId(),
            job.getTitle(),
            job.getDescription(),
            job.getLocation(),
            job.getSalary(),
            job.getEmploymentType(),
            job.getRecruiter().getName(),
            job.getCreatedAt()
    );
    }

    public List<JobResponse> searchJobs(
        String title,
        String location,
        String employmentType) {

    return jobRepository.searchJobs(
            normalizeFilter(title),
            normalizeFilter(location),
            normalizeFilter(employmentType)
    ).stream()
     .map(this::toJobResponse)
     .toList();
    }

    private String normalizeFilter(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}



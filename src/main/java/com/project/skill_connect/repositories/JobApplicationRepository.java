package com.project.skill_connect.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.skill_connect.entities.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByFreelancerIdAndJobId(Long freelancerId, Long jobId);
    
    List<JobApplication> findByFreelancerId(Long freelancerId);

    List<JobApplication> findByJobId(Long jobId);

    Optional<JobApplication> findByIdAndFreelancerId(Long applicationId, Long freelancerId);
}

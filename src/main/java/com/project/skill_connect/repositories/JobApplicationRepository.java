package com.project.skill_connect.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.skill_connect.entities.ApplicationStatus;
import com.project.skill_connect.entities.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {


boolean existsByFreelancerIdAndJobId(Long freelancerId, Long jobId);

List<JobApplication> findByFreelancerId(Long freelancerId);

List<JobApplication> findByJobId(Long jobId);

Optional<JobApplication> findByIdAndFreelancerId(Long applicationId, Long freelancerId);

// Total applications received by a recruiter
long countByJobRecruiterId(Long recruiterId);

// Applications grouped by status
long countByJobRecruiterIdAndStatus(
        Long recruiterId,
        ApplicationStatus status
);

// Latest applications received by a recruiter
List<JobApplication> findTop5ByJobRecruiterIdOrderByAppliedAtDesc(
        Long recruiterId
);

// Application statistics for each job
@Query("""
    SELECT ja.job.id, COUNT(ja)
    FROM JobApplication ja
    WHERE ja.job.recruiter.id = :recruiterId
    GROUP BY ja.job.id
    """)
List<Object[]> countApplicationsPerJob(
        @Param("recruiterId") Long recruiterId
);


}


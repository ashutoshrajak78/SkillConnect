
package com.project.skill_connect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.skill_connect.entities.Job;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.project.skill_connect.entities.JobStatus;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("""
        SELECT j FROM Job j
        WHERE j.status = com.project.skill_connect.entities.JobStatus.APPROVED
        AND (:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:employmentType IS NULL OR LOWER(j.employmentType) = LOWER(:employmentType))
        """)
    Page<Job> searchJobs(
            @Param("title") String title,
            @Param("location") String location,
            @Param("employmentType") String employmentType,
            Pageable pageable
    );

    long countByRecruiterId(Long recruiterId);

    List<Job> findByRecruiterId(Long recruiterId);

    // Find jobs by moderation status with pagination
    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    // Count jobs by moderation status
    long countByStatus(JobStatus status);

    // Get only approved jobs
    List<Job> findByStatus(JobStatus status);
}


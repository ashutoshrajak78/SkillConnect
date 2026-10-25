
package com.project.skill_connect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.skill_connect.entities.Job;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("""
        SELECT j FROM Job j
        WHERE (:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:employmentType IS NULL OR LOWER(j.employmentType) = LOWER(:employmentType))
        """)
    Page<Job> searchJobs(
            @Param("title") String title,
            @Param("location") String location,
            @Param("employmentType") String employmentType,
            Pageable pageable
    );
}


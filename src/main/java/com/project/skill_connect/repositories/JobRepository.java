
package com.project.skill_connect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.skill_connect.entities.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

}


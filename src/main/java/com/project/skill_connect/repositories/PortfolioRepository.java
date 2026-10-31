package com.project.skill_connect.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.skill_connect.entities.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    // Find all portfolio projects belonging to a freelancer
    List<Portfolio> findByFreelancerId(Long freelancerId);

    // Find a particular project belonging to a specific freelancer
    Optional<Portfolio> findByIdAndFreelancerId(Long id, Long freelancerId);
}
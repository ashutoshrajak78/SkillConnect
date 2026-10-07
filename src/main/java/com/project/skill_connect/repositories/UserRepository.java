package com.project.skill_connect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.skill_connect.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    
}

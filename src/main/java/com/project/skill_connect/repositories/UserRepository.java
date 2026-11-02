package com.project.skill_connect.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.skill_connect.entities.User;
import com.project.skill_connect.entities.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    long countByRole(Role role);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String name, String email, Pageable pageable);

    Page<User> findByRole(Role role, Pageable pageable);
}

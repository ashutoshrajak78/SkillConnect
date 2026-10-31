package com.project.skill_connect.dto;

import java.time.LocalDateTime;

import com.project.skill_connect.entities.Role;

public class ProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String contact;
    private String bio;
    private String skills;
    private String profilePicture;
    private Role role;
    private LocalDateTime createdAt;

    public ProfileResponse(Long id, String name, String email,
            String contact, String bio, String skills,
            String profilePicture, Role role, LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.bio = bio;
        this.skills = skills;
        this.profilePicture = profilePicture;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getBio() {
        return bio;
    }

    public String getSkills() {
        return skills;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
package com.project.skill_connect.dto;

import java.time.LocalDateTime;

import com.project.skill_connect.entities.Role;
import com.project.skill_connect.entities.User;

public class AdminUserResponse {

    private Long id;
    private String name;
    private String email;
    private String contact;
    private Role role;
    private LocalDateTime createdAt;
    private boolean active;

    public AdminUserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.contact = user.getContact();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.active = user.isActive();
    }

    public boolean isActive() {
        return active;
    }

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

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

package com.project.skill_connect.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.skill_connect.dto.AdminUserResponse;
import com.project.skill_connect.dto.UpdateUserRoleRequest;
import com.project.skill_connect.entities.Role;
import com.project.skill_connect.services.AdminUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // Get all users with pagination
    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending());

        return ResponseEntity.ok(adminUserService.getAllUsers(pageable));
    }

    // Search users by name or email
    @GetMapping("/search")
    public ResponseEntity<Page<AdminUserResponse>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending());

        return ResponseEntity.ok(
                adminUserService.searchUsers(keyword, pageable));
    }

    // Filter users by role
    @GetMapping("/role/{role}")
    public ResponseEntity<Page<AdminUserResponse>> getUsersByRole(
            @PathVariable Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending());

        return ResponseEntity.ok(
                adminUserService.getUsersByRole(role, pageable));
    }

    // Get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserResponse> getUserById(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                adminUserService.getUserById(userId));
    }

    // Update user role
    @PutMapping("/{userId}/role")
    public ResponseEntity<AdminUserResponse> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request) {

        return ResponseEntity.ok(
                adminUserService.updateUserRole(userId, request));
    }

    // Delete user(permanently delete user is currently disabled from AdminUserController,AdminUserService,AdminUserServiceTest)
//     @DeleteMapping("/{userId}")
//     public ResponseEntity<Void> deleteUser(
//             @PathVariable Long userId) {

//         adminUserService.deleteUser(userId);

//         return ResponseEntity.noContent().build();
//     }

    // Deactivate user account
    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<AdminUserResponse> deactivateUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                adminUserService.deactivateUser(userId));
    }

    // Activate user account
    @PatchMapping("/{userId}/activate")
    public ResponseEntity<AdminUserResponse> activateUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                adminUserService.activateUser(userId));
    }
}
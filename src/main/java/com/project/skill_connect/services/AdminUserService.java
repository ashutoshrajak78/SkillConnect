package com.project.skill_connect.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.skill_connect.dto.AdminUserResponse;
import com.project.skill_connect.dto.UpdateUserRoleRequest;
import com.project.skill_connect.entities.Role;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.exceptions.ResourceNotFoundException;
import com.project.skill_connect.repositories.UserRepository;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User getCurrentAdmin() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admins can manage users");
        }

        return admin;
    }

    // Get all users with pagination
    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {

        getCurrentAdmin();

        return userRepository.findAll(pageable)
                .map(AdminUserResponse::new);
    }

    // Search users by name or email
    public Page<AdminUserResponse> searchUsers(String keyword, Pageable pageable) {

        getCurrentAdmin();

        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword, keyword, pageable)
                .map(AdminUserResponse::new);
    }

    // Filter users by role
    public Page<AdminUserResponse> getUsersByRole(Role role, Pageable pageable) {

        getCurrentAdmin();

        return userRepository.findByRole(role, pageable)
                .map(AdminUserResponse::new);
    }

    // Get user details
    public AdminUserResponse getUserById(Long userId) {

        getCurrentAdmin();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + userId));

        return new AdminUserResponse(user);
    }

    // Update user role
    @Transactional
    public AdminUserResponse updateUserRole(
            Long userId, UpdateUserRoleRequest request) {

        User admin = getCurrentAdmin();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + userId));

      if (admin.getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "Admin cannot change their own role");
        }

        if (user.getRole() == Role.ADMIN
                && request.getRole() != Role.ADMIN
                && userRepository.countByRole(Role.ADMIN) <= 1) {

            throw new AccessDeniedException(
                    "Cannot demote the last remaining admin");
        }

        user.setRole(request.getRole());

        User updatedUser = userRepository.save(user);

        return new AdminUserResponse(updatedUser);
    }

    // Delete user
//     @Transactional
//     public void deleteUser(Long userId) {

//         User admin = getCurrentAdmin();

//         User user = userRepository.findById(userId)
//                 .orElseThrow(() -> new ResourceNotFoundException(
//                         "User not found with ID: " + userId));

//         if (admin.getId().equals(user.getId())) {
//             throw new AccessDeniedException(
//                     "Admin cannot delete their own account");
//         }

//         if (user.getRole() == Role.ADMIN
//                 && userRepository.countByRole(Role.ADMIN) <= 1) {

//             throw new AccessDeniedException(
//                     "Cannot delete the last remaining admin");
//         }

//         userRepository.delete(user);
//     }

    @Transactional
    public AdminUserResponse deactivateUser(Long userId) {

        User admin = getCurrentAdmin();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + userId));

        if (admin.getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "Admin cannot deactivate their own account");
        }

        if (user.getRole() == Role.ADMIN
                && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new AccessDeniedException(
                    "Cannot deactivate the last remaining admin");
        }

        user.setActive(false);

        User updatedUser = userRepository.save(user);

        return new AdminUserResponse(updatedUser);
    }

    @Transactional
    public AdminUserResponse activateUser(Long userId) {

        getCurrentAdmin();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + userId));

        user.setActive(true);

        User updatedUser = userRepository.save(user);

        return new AdminUserResponse(updatedUser);
    }
}
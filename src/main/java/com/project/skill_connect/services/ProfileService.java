package com.project.skill_connect.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.skill_connect.dto.ProfileResponse;
import com.project.skill_connect.dto.ProfileUpdateRequest;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.repositories.UserRepository;

import com.project.skill_connect.dto.PublicFreelancerProfileResponse;
import com.project.skill_connect.entities.Role;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get currently authenticated user
    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // Get own profile
    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {

        User user = getAuthenticatedUser();

        return mapToProfileResponse(user);
    }

    // Update own profile
    @Transactional
    public ProfileResponse updateMyProfile(ProfileUpdateRequest request) {

        User user = getAuthenticatedUser();

        user.setName(request.getName());
        user.setContact(request.getContact());
        user.setBio(request.getBio());
        user.setSkills(request.getSkills());
        user.setProfilePicture(request.getProfilePicture());

        User updatedUser = userRepository.save(user);

        return mapToProfileResponse(updatedUser);
    }

    // Convert User entity to ProfileResponse DTO
    private ProfileResponse mapToProfileResponse(User user) {

        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getContact(),
                user.getBio(),
                user.getSkills(),
                user.getProfilePicture(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public PublicFreelancerProfileResponse getFreelancerProfile(Long userId) {

        User user = userRepository.findById(userId)
                .filter(u -> u.getRole() == Role.FREELANCER)
                .orElseThrow(() ->
                        new RuntimeException("Freelancer profile not found"));

        return new PublicFreelancerProfileResponse(
                user.getId(),
                user.getName(),
                user.getBio(),
                user.getSkills(),
                user.getProfilePicture()
        );
    }
}
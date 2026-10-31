package com.project.skill_connect.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.skill_connect.dto.ProfileResponse;
import com.project.skill_connect.dto.ProfileUpdateRequest;
import com.project.skill_connect.services.ProfileService;

import org.springframework.web.bind.annotation.PathVariable;
import com.project.skill_connect.dto.PublicFreelancerProfileResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // Get logged-in user's profile
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile() {

        ProfileResponse profile = profileService.getMyProfile();

        return ResponseEntity.ok(profile);
    }

    // Update logged-in user's profile
    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @Valid @RequestBody ProfileUpdateRequest request) {

        ProfileResponse updatedProfile =
                profileService.updateMyProfile(request);

        return ResponseEntity.ok(updatedProfile);
    }

    // Get public freelancer profile
    @GetMapping("/{userId}")
    public ResponseEntity<PublicFreelancerProfileResponse> getFreelancerProfile(
            @PathVariable Long userId) {

        PublicFreelancerProfileResponse profile =
                profileService.getFreelancerProfile(userId);

        return ResponseEntity.ok(profile);
    }

    

}
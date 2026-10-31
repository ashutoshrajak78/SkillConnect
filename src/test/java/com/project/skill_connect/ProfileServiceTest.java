package com.project.skill_connect;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.project.skill_connect.dto.ProfileResponse;
import com.project.skill_connect.dto.ProfileUpdateRequest;
import com.project.skill_connect.dto.PublicFreelancerProfileResponse;
import com.project.skill_connect.entities.Role;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.repositories.UserRepository;

import com.project.skill_connect.services.ProfileService;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileService profileService;

    private User freelancer;

    @BeforeEach
    void setUp() {
        freelancer = new User();

        freelancer.setName("Ayush Shukla");
        freelancer.setEmail("freelancer.test@example.com");
        freelancer.setContact("9876543210");
        freelancer.setBio("Java Backend Developer");
        freelancer.setSkills("Java, Spring Boot, MySQL");
        freelancer.setRole(Role.FREELANCER);

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "freelancer.test@example.com",
                null
            )
        );
    }

    @Test
    void getMyProfileShouldReturnProfile() {

        when(userRepository.findByEmail("freelancer.test@example.com"))
                .thenReturn(Optional.of(freelancer));

        ProfileResponse response = profileService.getMyProfile();

        assertEquals("Ayush Shukla", response.getName());
        assertEquals("freelancer.test@example.com", response.getEmail());
        assertEquals(Role.FREELANCER, response.getRole());
    }

   @Test
    void updateMyProfileShouldUpdateDetails() {

        when(userRepository.findByEmail("freelancer.test@example.com"))
                .thenReturn(Optional.of(freelancer));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setName("Ayush Updated");
        request.setContact("9999999999");
        request.setBio("Experienced Java Developer");
        request.setSkills("Java, Spring Boot, REST API");
        request.setProfilePicture(null);

        ProfileResponse response =
                profileService.updateMyProfile(request);

        assertEquals("Ayush Updated", response.getName());
        assertEquals("9999999999", response.getContact());

        verify(userRepository).save(freelancer);
    }

    @Test
    void getFreelancerProfileShouldReturnPublicDetails() {

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(freelancer));

        PublicFreelancerProfileResponse response =
                profileService.getFreelancerProfile(10L);

        assertEquals("Ayush Shukla", response.getName());
        assertEquals("Java Backend Developer", response.getBio());
        assertEquals("Java, Spring Boot, MySQL", response.getSkills());
    }

    @Test
    void getFreelancerProfileShouldRejectNonFreelancer() {

        freelancer.setRole(Role.RECRUITER);

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(freelancer));

        assertThrows(
            RuntimeException.class,
            () -> profileService.getFreelancerProfile(10L)
        );
    }
}
package com.project.skill_connect;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.project.skill_connect.entities.*;
import com.project.skill_connect.repositories.JobRepository;
import com.project.skill_connect.repositories.UserRepository;
import com.project.skill_connect.services.AdminJobModerationService;
import com.project.skill_connect.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class AdminJobModerationServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminJobModerationService moderationService;

    private User admin;
    private Job job;

    @BeforeEach
    void setUp() {

        admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);
        admin.setActive(true);

        job = new Job();
        job.setId(1L);
        job.setTitle("Java Backend Developer");
        job.setStatus(JobStatus.PENDING);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin@test.com", null, List.of()));

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(admin));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getJobsByStatus_shouldReturnJobs() {

        Page<Job> jobs = new PageImpl<>(List.of(job));

        when(jobRepository.findByStatus(
                JobStatus.PENDING, PageRequest.of(0, 10)))
                .thenReturn(jobs);

        Page<?> result = moderationService.getJobsByStatus(
                JobStatus.PENDING, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void approveJob_shouldApproveJob() {

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(jobRepository.save(any(Job.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = moderationService.approveJob(1L);

        assertEquals(JobStatus.APPROVED, result.getStatus());
        verify(jobRepository).save(job);
    }

    @Test
    void rejectJob_shouldRejectJob() {

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(jobRepository.save(any(Job.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = moderationService.rejectJob(1L);

        assertEquals(JobStatus.REJECTED, result.getStatus());
        verify(jobRepository).save(job);
    }

    @Test
    void approveJob_shouldThrowWhenJobNotFound() {

        when(jobRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> moderationService.approveJob(99L));
    }

    @Test
    void rejectJob_shouldThrowWhenJobNotFound() {

        when(jobRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> moderationService.rejectJob(99L));
    }

    @Test
    void nonAdmin_shouldNotModerateJobs() {

        User recruiter = new User();
        recruiter.setEmail("recruiter@test.com");
        recruiter.setRole(Role.RECRUITER);

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(recruiter));

        assertThrows(
                AccessDeniedException.class,
                () -> moderationService.approveJob(1L));
    }
}
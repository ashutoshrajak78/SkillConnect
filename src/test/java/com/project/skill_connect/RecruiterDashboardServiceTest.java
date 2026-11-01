package com.project.skill_connect;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.project.skill_connect.entities.*;
import com.project.skill_connect.exceptions.ResourceNotFoundException;
import com.project.skill_connect.repositories.*;
import com.project.skill_connect.services.RecruiterDashboardService;
import com.project.skill_connect.dto.*;

@ExtendWith(MockitoExtension.class)
class RecruiterDashboardServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecruiterDashboardService dashboardService;

    private User recruiter;
    private User freelancer;
    private Job job;
    private JobApplication application;

    @BeforeEach
    void setUp() {

        recruiter = new User();
        recruiter.setName("Test Recruiter");
        recruiter.setEmail("recruiter@example.com");
        recruiter.setRole(Role.RECRUITER);
        setEntityId(recruiter, 1L);

        freelancer = new User();
        freelancer.setName("Test Freelancer");
        freelancer.setEmail("freelancer@example.com");
        freelancer.setRole(Role.FREELANCER);
        setEntityId(freelancer, 2L);

        job = new Job();
        job.setTitle("Java Backend Developer");
        job.setRecruiter(recruiter);
        setEntityId(job, 10L);

        application = new JobApplication();
        application.setJob(job);
        application.setFreelancer(freelancer);
        application.setStatus(ApplicationStatus.PENDING);
        application.setCoverLetter("Interested in this opportunity.");
        setEntityId(application, 100L);
    }

    private void setEntityId(Object entity, Long id) {
        try {
            Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void authenticate(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null)
        );
    }

    private void mockDashboardData() {

        when(userRepository.findByEmail("recruiter@example.com"))
                .thenReturn(Optional.of(recruiter));

        when(jobRepository.countByRecruiterId(1L))
                .thenReturn(3L);

        when(jobApplicationRepository.countByJobRecruiterId(1L))
                .thenReturn(12L);

        when(jobApplicationRepository.countByJobRecruiterIdAndStatus(
                1L, ApplicationStatus.PENDING))
                .thenReturn(5L);

        when(jobApplicationRepository.countByJobRecruiterIdAndStatus(
                1L, ApplicationStatus.ACCEPTED))
                .thenReturn(4L);

        when(jobApplicationRepository.countByJobRecruiterIdAndStatus(
                1L, ApplicationStatus.REJECTED))
                .thenReturn(3L);

        when(jobApplicationRepository
                .findTop5ByJobRecruiterIdOrderByAppliedAtDesc(1L))
                .thenReturn(List.of(application));

        when(jobApplicationRepository.countApplicationsPerJob(1L))
                .thenReturn(List.<Object[]>of(
                        new Object[]{10L, 12L}
                ));

        Job secondJob = new Job();
        secondJob.setTitle("Spring Boot Developer");
        secondJob.setRecruiter(recruiter);
        setEntityId(secondJob, 11L);

        when(jobRepository.findByRecruiterId(1L))
                .thenReturn(List.of(job, secondJob));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getDashboardShouldReturnCorrectStatistics() {

        authenticate("recruiter@example.com");
        mockDashboardData();

        RecruiterDashboardResponse response =
                dashboardService.getDashboard();

        assertEquals(3L, response.getTotalJobs());
        assertEquals(12L, response.getTotalApplications());
        assertEquals(5L, response.getPendingApplications());
        assertEquals(4L, response.getAcceptedApplications());
        assertEquals(3L, response.getRejectedApplications());
    }

    @Test
    void getDashboardShouldReturnRecentApplications() {

        authenticate("recruiter@example.com");
        mockDashboardData();

        RecruiterDashboardResponse response =
                dashboardService.getDashboard();

        assertEquals(1, response.getRecentApplications().size());

        RecentApplicationResponse recent =
                response.getRecentApplications().get(0);

        assertEquals(100L, recent.getApplicationId());
        assertEquals("Java Backend Developer", recent.getJobTitle());
        assertEquals("Test Freelancer", recent.getFreelancerName());
        assertEquals(ApplicationStatus.PENDING, recent.getStatus());
    }

    @Test
    void getDashboardShouldIncludeJobsWithoutApplications() {

        authenticate("recruiter@example.com");
        mockDashboardData();

        RecruiterDashboardResponse response =
                dashboardService.getDashboard();

        assertEquals(2, response.getJobStatistics().size());

        JobApplicationStatsResponse first =
                response.getJobStatistics().get(0);

        JobApplicationStatsResponse second =
                response.getJobStatistics().get(1);

        assertEquals(12L, first.getApplicationCount());
        assertEquals(0L, second.getApplicationCount());
    }

    @Test
    void getDashboardShouldRejectFreelancer() {

        recruiter.setRole(Role.FREELANCER);

        authenticate("recruiter@example.com");

        when(userRepository.findByEmail("recruiter@example.com"))
                .thenReturn(Optional.of(recruiter));

        assertThrows(
                AccessDeniedException.class,
                () -> dashboardService.getDashboard()
        );

        verifyNoInteractions(jobRepository, jobApplicationRepository);
    }

    @Test
    void getDashboardShouldRejectClient() {

        recruiter.setRole(Role.CLIENT);

        authenticate("recruiter@example.com");

        when(userRepository.findByEmail("recruiter@example.com"))
                .thenReturn(Optional.of(recruiter));

        assertThrows(
                AccessDeniedException.class,
                () -> dashboardService.getDashboard()
        );
    }

    @Test
    void getDashboardShouldThrowWhenUserNotFound() {

        authenticate("unknown@example.com");

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> dashboardService.getDashboard()
        );
    }
}
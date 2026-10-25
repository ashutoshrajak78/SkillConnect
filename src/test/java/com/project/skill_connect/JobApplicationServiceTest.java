
package com.project.skill_connect;

import com.project.skill_connect.dto.JobApplicationResponse;
import com.project.skill_connect.entities.ApplicationStatus;
import com.project.skill_connect.entities.Job;
import com.project.skill_connect.entities.JobApplication;
import com.project.skill_connect.entities.Role;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.exceptions.DuplicateApplicationException;
import com.project.skill_connect.exceptions.ResourceNotFoundException;
import com.project.skill_connect.repositories.JobApplicationRepository;
import com.project.skill_connect.repositories.JobRepository;
import com.project.skill_connect.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import com.project.skill_connect.services.JobApplicationService;

import com.project.skill_connect.services.JobApplicationService;
import java.lang.reflect.Field;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobApplicationService jobApplicationService;

    private User freelancer;
    private Job job;

    @BeforeEach
    void setUp() {
        freelancer = new User();
        setEntityId(freelancer, 1L);
        freelancer.setName("Test Freelancer");
        freelancer.setEmail("freelancer@example.com");
        freelancer.setRole(Role.FREELANCER);

        job = new Job();
        job.setId(10L);
        job.setTitle("Java Backend Developer");
    }

    @Test
    void applyForJobShouldSaveApplicationSuccessfully() {
        when(userRepository.findByEmail("freelancer@example.com"))
                .thenReturn(Optional.of(freelancer));

        when(jobRepository.findById(10L))
                .thenReturn(Optional.of(job));

        when(applicationRepository.existsByFreelancerIdAndJobId(1L, 10L))
                .thenReturn(false);

        when(applicationRepository.save(any(JobApplication.class)))
                .thenAnswer(invocation -> {
                    JobApplication application = invocation.getArgument(0);
                    setEntityId(application, 100L);
                    return application;
                });

        JobApplicationResponse response = jobApplicationService.applyForJob(
                10L,
                "I have strong Java and Spring Boot skills.",
                "freelancer@example.com"
        );

        assertNotNull(response);
        // assertEquals(100L, response.getId());
        assertEquals(10L, response.getJobId());
        assertEquals("Java Backend Developer", response.getJobTitle());
        assertEquals("Test Freelancer", response.getFreelancerName());
        assertEquals("freelancer@example.com", response.getFreelancerEmail());
        assertEquals(
                "I have strong Java and Spring Boot skills.",
                response.getCoverLetter()
        );
        assertEquals(ApplicationStatus.PENDING, response.getStatus());

        verify(applicationRepository).save(any(JobApplication.class));
    }

    @Test
    void duplicateApplicationShouldThrowException() {
        when(userRepository.findByEmail("freelancer@example.com"))
                .thenReturn(Optional.of(freelancer));

        when(jobRepository.findById(10L))
                .thenReturn(Optional.of(job));

        when(applicationRepository.existsByFreelancerIdAndJobId(1L, 10L))
                .thenReturn(true);

        assertThrows(
                DuplicateApplicationException.class,
                () -> jobApplicationService.applyForJob(
                        10L, "My cover letter", "freelancer@example.com"
                )
        );

        verify(applicationRepository, never()).save(any(JobApplication.class));
    }

    @Test
    void applyingForNonexistentJobShouldThrowException() {
        when(userRepository.findByEmail("freelancer@example.com"))
                .thenReturn(Optional.of(freelancer));

        when(jobRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.applyForJob(
                        999L, "My cover letter", "freelancer@example.com"
                )
        );

        verify(applicationRepository, never()).save(any(JobApplication.class));
    }

    @Test
    void recruiterShouldNotBeAllowedToApplyForJob() {
        User recruiter = new User();

        recruiter.setEmail("recruiter@example.com");
        recruiter.setRole(Role.RECRUITER);

        when(userRepository.findByEmail("recruiter@example.com"))
                .thenReturn(Optional.of(recruiter));

        assertThrows(
                AccessDeniedException.class,
                () -> jobApplicationService.applyForJob(
                        10L, "My cover letter", "recruiter@example.com"
                )
        );

        verifyNoInteractions(jobRepository);
        verify(applicationRepository, never()).save(any(JobApplication.class));
    }

    private void setEntityId(Object entity, Long id) {
        try {
            Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to set test entity ID", e);
        }
    }

    
    @Test
    void recruiterCanViewApplicationsForOwnJob() {
        User recruiter = new User();
        recruiter.setEmail("recruiter@example.com");
        job.setRecruiter(recruiter);

        when(jobRepository.findById(10L))
                .thenReturn(Optional.of(job));
        when(applicationRepository.findByJobId(10L))
                .thenReturn(java.util.Collections.emptyList());

        var responses = jobApplicationService.getApplicationsForJob(
                10L, "recruiter@example.com"
        );

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(applicationRepository).findByJobId(10L);
    }

    @Test
    void recruiterCannotViewApplicationsForAnotherRecruitersJob() {
        User owner = new User();
        owner.setEmail("owner@example.com");
        job.setRecruiter(owner);

        when(jobRepository.findById(10L))
                .thenReturn(Optional.of(job));

        assertThrows(
                AccessDeniedException.class,
                () -> jobApplicationService.getApplicationsForJob(
                        10L, "different-recruiter@example.com"
                )
        );

        verify(applicationRepository, never()).findByJobId(anyLong());
    }

    @Test
    void recruiterCanAcceptApplicationForOwnJob() {
        User recruiter = new User();
        recruiter.setEmail("recruiter@example.com");
        job.setRecruiter(recruiter);

        JobApplication application = new JobApplication();
        setEntityId(application, 200L);
        application.setJob(job);
        application.setFreelancer(freelancer);
        application.setCoverLetter("My Java experience");
        application.setStatus(ApplicationStatus.PENDING);

        when(applicationRepository.findById(200L))
                .thenReturn(Optional.of(application));
        when(applicationRepository.save(any(JobApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        JobApplicationResponse response =
                jobApplicationService.updateApplicationStatus(
                        200L,
                        ApplicationStatus.ACCEPTED,
                        "recruiter@example.com"
                );

        assertEquals(ApplicationStatus.ACCEPTED, response.getStatus());
        verify(applicationRepository).save(application);
    }

    @Test
    void recruiterCannotUpdateApplicationForAnotherRecruitersJob() {
        User owner = new User();
        owner.setEmail("owner@example.com");
        job.setRecruiter(owner);

        JobApplication application = new JobApplication();
        setEntityId(application, 201L);
        application.setJob(job);
        application.setFreelancer(freelancer);
        application.setCoverLetter("My Java experience");
        application.setStatus(ApplicationStatus.PENDING);

        when(applicationRepository.findById(201L))
                .thenReturn(Optional.of(application));

        assertThrows(
                AccessDeniedException.class,
                () -> jobApplicationService.updateApplicationStatus(
                        201L,
                        ApplicationStatus.REJECTED,
                        "different-recruiter@example.com"
                )
        );

        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        verify(applicationRepository, never()).save(any(JobApplication.class));
    }

}

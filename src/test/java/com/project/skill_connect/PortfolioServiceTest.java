package com.project.skill_connect;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

import com.project.skill_connect.dto.PortfolioRequest;
import com.project.skill_connect.dto.PortfolioResponse;
import com.project.skill_connect.entities.Portfolio;
import com.project.skill_connect.entities.Role;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.exceptions.ResourceNotFoundException;
import com.project.skill_connect.repositories.PortfolioRepository;
import com.project.skill_connect.repositories.UserRepository;
import com.project.skill_connect.services.PortfolioService;

import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private User freelancer;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {

        freelancer = new User();
        freelancer.setName("Test Freelancer");
        freelancer.setEmail("freelancer@example.com");
        freelancer.setRole(Role.FREELANCER);

        setEntityId(freelancer, 1L);

        portfolio = new Portfolio();
        setEntityId(portfolio, 1L);
        portfolio.setTitle("Skill Connect");
        portfolio.setDescription("Freelance marketplace project");
        portfolio.setTechnologies("Java, Spring Boot, MySQL");
        portfolio.setFreelancer(freelancer);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "freelancer@example.com",
                        null
                )
        );
    }

    @Test
    void createPortfolioShouldSaveProject() {

        when(userRepository.findByEmail("freelancer@example.com"))
                .thenReturn(Optional.of(freelancer));

        when(portfolioRepository.save(any(Portfolio.class)))
                .thenAnswer(invocation -> {
                    Portfolio saved = invocation.getArgument(0);
                    setEntityId(saved, 1L);
                    return saved;
                });

        PortfolioRequest request = new PortfolioRequest();
        request.setTitle("Skill Connect");
        request.setDescription("Freelance marketplace project");
        request.setTechnologies("Java, Spring Boot, MySQL");

        PortfolioResponse response =
                portfolioService.createPortfolio(request);

        assertEquals("Skill Connect", response.getTitle());
        assertEquals(1L, response.getFreelancerId());

        verify(portfolioRepository).save(any(Portfolio.class));
    }

    @Test
    void getMyPortfolioShouldReturnProjects() {

        when(userRepository.findByEmail("freelancer@example.com"))
                .thenReturn(Optional.of(freelancer));

        when(portfolioRepository.findByFreelancerId(1L))
                .thenReturn(List.of(portfolio));

        List<PortfolioResponse> response =
                portfolioService.getMyPortfolio();

        assertEquals(1, response.size());
        assertEquals("Skill Connect", response.get(0).getTitle());
    }

    @Test
    void updatePortfolioShouldUpdateProject() {

        when(userRepository.findByEmail("freelancer@example.com"))
                .thenReturn(Optional.of(freelancer));

        when(portfolioRepository.findByIdAndFreelancerId(1L, 1L))
                .thenReturn(Optional.of(portfolio));

        when(portfolioRepository.save(any(Portfolio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PortfolioRequest request = new PortfolioRequest();
        request.setTitle("Updated Skill Connect");
        request.setDescription("Updated description");
        request.setTechnologies("Java, Spring Boot, REST API");

        PortfolioResponse response =
                portfolioService.updatePortfolio(1L, request);

        assertEquals("Updated Skill Connect", response.getTitle());

        verify(portfolioRepository).save(portfolio);
    }

    @Test
    void deletePortfolioShouldDeleteProject() {

        when(userRepository.findByEmail("freelancer@example.com"))
                .thenReturn(Optional.of(freelancer));

        when(portfolioRepository.findByIdAndFreelancerId(1L, 1L))
                .thenReturn(Optional.of(portfolio));

        portfolioService.deletePortfolio(1L);

        verify(portfolioRepository).delete(portfolio);
    }

    @Test
    void updatePortfolioShouldRejectAnotherUsersProject() {

        when(userRepository.findByEmail("freelancer@example.com"))
                .thenReturn(Optional.of(freelancer));

        when(portfolioRepository.findByIdAndFreelancerId(1L, 1L))
                .thenReturn(Optional.empty());

        PortfolioRequest request = new PortfolioRequest();

        assertThrows(
                ResourceNotFoundException.class,
                () -> portfolioService.updatePortfolio(1L, request)
        );
    }

    @Test
    void createPortfolioShouldRejectNonFreelancer() {

        freelancer.setRole(Role.CLIENT);

        when(userRepository.findByEmail("freelancer@example.com"))
                .thenReturn(Optional.of(freelancer));

        PortfolioRequest request = new PortfolioRequest();

        assertThrows(
                AccessDeniedException.class,
                () -> portfolioService.createPortfolio(request)
        );

        verify(portfolioRepository, never()).save(any(Portfolio.class));
    }

    @Test
    void getFreelancerPortfolioShouldRejectInvalidFreelancer() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> portfolioService.getFreelancerPortfolio(99L)
        );
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

}
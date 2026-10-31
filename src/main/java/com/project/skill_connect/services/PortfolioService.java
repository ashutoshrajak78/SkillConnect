package com.project.skill_connect.services;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.skill_connect.dto.PortfolioRequest;
import com.project.skill_connect.dto.PortfolioResponse;
import com.project.skill_connect.entities.Portfolio;
import com.project.skill_connect.entities.Role;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.repositories.PortfolioRepository;
import com.project.skill_connect.repositories.UserRepository;

import com.project.skill_connect.exceptions.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    public PortfolioService(
            PortfolioRepository portfolioRepository,
            UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    // Get currently authenticated user
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    // Ensure only freelancers can manage portfolios
    private User getAuthenticatedFreelancer() {
        User user = getAuthenticatedUser();

        if (user.getRole() != Role.FREELANCER) {
            throw new AccessDeniedException(
                    "Only freelancers can manage portfolio projects");
        }

        return user;
    }

    // Add portfolio project
    @Transactional
    public PortfolioResponse createPortfolio(PortfolioRequest request) {

        User freelancer = getAuthenticatedFreelancer();

        Portfolio portfolio = new Portfolio();

        portfolio.setTitle(request.getTitle());
        portfolio.setDescription(request.getDescription());
        portfolio.setTechnologies(request.getTechnologies());
        portfolio.setProjectUrl(request.getProjectUrl());
        portfolio.setGithubUrl(request.getGithubUrl());
        portfolio.setFreelancer(freelancer);

        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        return mapToResponse(savedPortfolio);
    }

    // Get own portfolio projects
    @Transactional(readOnly = true)
    public List<PortfolioResponse> getMyPortfolio() {

        User freelancer = getAuthenticatedFreelancer();

        return portfolioRepository
                .findByFreelancerId(freelancer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Update portfolio project
    @Transactional
    public PortfolioResponse updatePortfolio(
            Long portfolioId,
            PortfolioRequest request) {

        User freelancer = getAuthenticatedFreelancer();

        Portfolio portfolio = portfolioRepository
                .findByIdAndFreelancerId(
                        portfolioId,
                        freelancer.getId())
               .orElseThrow(() ->
        new ResourceNotFoundException("Portfolio project not found"));

        portfolio.setTitle(request.getTitle());
        portfolio.setDescription(request.getDescription());
        portfolio.setTechnologies(request.getTechnologies());
        portfolio.setProjectUrl(request.getProjectUrl());
        portfolio.setGithubUrl(request.getGithubUrl());

        Portfolio updatedPortfolio =
                portfolioRepository.save(portfolio);

        return mapToResponse(updatedPortfolio);
    }

    // Delete portfolio project
    @Transactional
    public void deletePortfolio(Long portfolioId) {

        User freelancer = getAuthenticatedFreelancer();

        Portfolio portfolio = portfolioRepository
                .findByIdAndFreelancerId(
                        portfolioId,
                        freelancer.getId())
               .orElseThrow(() ->
        new ResourceNotFoundException("Portfolio project not found"));

        portfolioRepository.delete(portfolio);
    }

    // Get public portfolio of a freelancer
    @Transactional(readOnly = true)
    public List<PortfolioResponse> getFreelancerPortfolio(Long freelancerId) {

       User freelancer = userRepository.findById(freelancerId)
        .filter(user -> user.getRole() == Role.FREELANCER)
        .orElseThrow(() ->
                new ResourceNotFoundException("Freelancer not found"));
        return portfolioRepository
                .findByFreelancerId(freelancer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Convert Entity to DTO
    private PortfolioResponse mapToResponse(Portfolio portfolio) {

        return new PortfolioResponse(
                portfolio.getId(),
                portfolio.getTitle(),
                portfolio.getDescription(),
                portfolio.getTechnologies(),
                portfolio.getProjectUrl(),
                portfolio.getGithubUrl(),
                portfolio.getFreelancer().getId(),
                portfolio.getCreatedAt(),
                portfolio.getUpdatedAt()
        );
    }
}
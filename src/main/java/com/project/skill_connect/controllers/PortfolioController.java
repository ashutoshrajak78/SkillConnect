package com.project.skill_connect.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.skill_connect.dto.PortfolioRequest;
import com.project.skill_connect.dto.PortfolioResponse;
import com.project.skill_connect.services.PortfolioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // Create portfolio project
    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(
            @Valid @RequestBody PortfolioRequest request) {

        PortfolioResponse response =
                portfolioService.createPortfolio(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get logged-in freelancer's portfolio
    @GetMapping("/me")
    public ResponseEntity<List<PortfolioResponse>> getMyPortfolio() {

        return ResponseEntity.ok(
                portfolioService.getMyPortfolio());
    }

    // Update portfolio project
    @PutMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponse> updatePortfolio(
            @PathVariable Long portfolioId,
            @Valid @RequestBody PortfolioRequest request) {

        PortfolioResponse response =
                portfolioService.updatePortfolio(portfolioId, request);

        return ResponseEntity.ok(response);
    }

    // Delete portfolio project
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(
            @PathVariable Long portfolioId) {

        portfolioService.deletePortfolio(portfolioId);

        return ResponseEntity.noContent().build();
    }

    // View a freelancer's public portfolio
    @GetMapping("/freelancer/{freelancerId}")
    public ResponseEntity<List<PortfolioResponse>> getFreelancerPortfolio(
            @PathVariable Long freelancerId) {

        return ResponseEntity.ok(
                portfolioService.getFreelancerPortfolio(freelancerId));
    }
}
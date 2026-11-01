package com.project.skill_connect.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.skill_connect.dto.RecruiterDashboardResponse;
import com.project.skill_connect.services.RecruiterDashboardService;

@RestController
@RequestMapping("/api/recruiter")
public class RecruiterDashboardController {

    private final RecruiterDashboardService dashboardService;

    public RecruiterDashboardController(
            RecruiterDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<RecruiterDashboardResponse> getDashboard() {

        RecruiterDashboardResponse response =
                dashboardService.getDashboard();

        return ResponseEntity.ok(response);
    }
}
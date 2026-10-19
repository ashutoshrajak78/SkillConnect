
package com.project.skill_connect.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "JWT authentication successful! Welcome to SkillConnect.";
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/recruiter")
    public String recruiterEndpoint() {
        return "Welcome, Recruiter!";
    }

    @PreAuthorize("hasRole('FREELANCER')")
    @GetMapping("/freelancer")
    public String freelancerEndpoint() {
        return "Welcome, Freelancer!";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/client")
    public String clientEndpoint() {
        return "Welcome, Client!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminEndpoint() {
        return "Welcome, Admin!";
    }
}



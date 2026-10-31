package com.project.skill_connect.dto;

public class PublicFreelancerProfileResponse {

    private Long id;
    private String name;
    private String bio;
    private String skills;
    private String profilePicture;

    public PublicFreelancerProfileResponse(
            Long id,
            String name,
            String bio,
            String skills,
            String profilePicture) {

        this.id = id;
        this.name = name;
        this.bio = bio;
        this.skills = skills;
        this.profilePicture = profilePicture;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getSkills() {
        return skills;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
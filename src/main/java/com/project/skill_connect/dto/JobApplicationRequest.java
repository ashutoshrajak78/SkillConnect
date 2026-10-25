package com.project.skill_connect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JobApplicationRequest {

@NotBlank(message = "Cover letter cannot be empty")
@Size(max = 2000, message = "Cover letter cannot exceed 2000 characters")
private String coverLetter;

public String getCoverLetter() {
    return coverLetter;
}

public void setCoverLetter(String coverLetter) {
    this.coverLetter = coverLetter;
}

}

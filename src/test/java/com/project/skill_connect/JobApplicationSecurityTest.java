
package com.project.skill_connect;

import com.project.skill_connect.services.JobApplicationService;
import com.project.skill_connect.security.JwtService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class JobApplicationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobApplicationService jobApplicationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void myApplicationsWithoutTokenShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/applications/my")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    
    @Test
    void myApplicationsWithInvalidTokenShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/applications/my")
                    .header("Authorization", "Bearer invalid.jwt.token")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

}

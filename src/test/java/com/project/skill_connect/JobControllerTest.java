
package com.project.skill_connect;

import com.project.skill_connect.controllers.JobController;
import com.project.skill_connect.services.JobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.project.skill_connect.security.JwtService;
import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(JobController.class)
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService jobService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void invalidSortFieldShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/jobs/search")
                .param("sortBy", "password")
                .param("direction", "desc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(jobService);
    }

    @Test
    void invalidSortDirectionShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/jobs/search")
                .param("sortBy", "salary")
                .param("direction", "wrong"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(jobService);
    }

    @Test
    void negativePageShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/jobs/search")
                .param("page", "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(jobService);
    }

    @Test
    void zeroPageSizeShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/jobs/search")
                .param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(jobService);
    }

    @Test
    void oversizedPageShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/jobs/search")
                .param("size", "101"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(jobService);
    }
}

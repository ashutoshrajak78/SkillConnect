package com.project.skill_connect;

import com.project.skill_connect.controllers.AuthController;
import com.project.skill_connect.services.UserService;
import com.project.skill_connect.security.JwtService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.project.skill_connect.entities.User;
import com.project.skill_connect.entities.Role;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.lang.reflect.Field;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.mockito.ArgumentMatchers.anyString;
import com.project.skill_connect.exceptions.InvalidCredentialsException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void registerShouldReturnCreatedOrOk() throws Exception {
        User savedUser = new User();
        setEntityId(savedUser, 1L);
        savedUser.setName("Test Freelancer");
        savedUser.setEmail("test@example.com");
        savedUser.setContact("9876543210");
        savedUser.setRole(Role.FREELANCER);

        when(userService.registerUser(any(User.class)))
                .thenReturn(savedUser);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Test Freelancer",
                        "email": "test@example.com",
                        "password": "TestPassword123!",
                        "contact": "9876543210",
                        "role": "FREELANCER"
                    }
                    """))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Freelancer"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("FREELANCER"));
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
    void loginShouldReturnJwtToken() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");

        when(userService.authenticateUser(
                "test@example.com", "TestPassword123!"))
                .thenReturn(user);

        when(jwtService.generateToken(anyString()))
                .thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@example.com",
                        "password": "TestPassword123!"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void loginWithInvalidCredentialsShouldReturnUnauthorized()
            throws Exception {

        when(userService.authenticateUser(
                "test@example.com", "WrongPassword123!"))
                .thenThrow(new InvalidCredentialsException(
                        "Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@example.com",
                        "password": "WrongPassword123!"
                    }
                    """))
                .andExpect(status().isUnauthorized());

        verify(jwtService, never()).generateToken(anyString());
    }




}
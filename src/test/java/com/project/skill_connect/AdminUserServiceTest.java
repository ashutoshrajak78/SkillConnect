package com.project.skill_connect;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.project.skill_connect.entities.Role;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.exceptions.ResourceNotFoundException;
import com.project.skill_connect.repositories.UserRepository;
import com.project.skill_connect.services.AdminUserService;

import com.project.skill_connect.dto.UpdateUserRoleRequest;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    private User admin;
    private User freelancer;

    @BeforeEach
    void setUp() {

        admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);
        admin.setActive(true);

        freelancer = new User();
        freelancer.setName("Freelancer");
        freelancer.setEmail("freelancer@test.com");
        freelancer.setRole(Role.FREELANCER);
        freelancer.setActive(true);

        ReflectionTestUtils.setField(admin, "id", 1L);
        ReflectionTestUtils.setField(freelancer, "id", 2L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin@test.com", null, List.of()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void mockAdmin() {
        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(admin));
    }

    private User createSecondAdmin() {

        User secondAdmin = new User();
        secondAdmin.setName("Second Admin");
        secondAdmin.setEmail("secondadmin@test.com");
        secondAdmin.setRole(Role.ADMIN);
        secondAdmin.setActive(true);

        ReflectionTestUtils.setField(secondAdmin, "id", 2L);

        return secondAdmin;
    }

    @Test
    void getAllUsers_shouldReturnUsers() {

        mockAdmin();

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> users = new PageImpl<>(List.of(admin, freelancer));

        when(userRepository.findAll(pageable)).thenReturn(users);

        Page<?> result = adminUserService.getAllUsers(pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void searchUsers_shouldReturnMatchingUsers() {

        mockAdmin();

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> users = new PageImpl<>(List.of(freelancer));

        when(userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "free", "free", pageable))
                .thenReturn(users);

        Page<?> result = adminUserService.searchUsers("free", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getUsersByRole_shouldReturnFilteredUsers() {

        mockAdmin();

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> users = new PageImpl<>(List.of(freelancer));

        when(userRepository.findByRole(Role.FREELANCER, pageable))
                .thenReturn(users);

        Page<?> result = adminUserService.getUsersByRole(
                Role.FREELANCER, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getUserById_shouldReturnUser() {

        mockAdmin();

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(freelancer));

        var result = adminUserService.getUserById(2L);

        assertEquals("freelancer@test.com", result.getEmail());
    }

    @Test
    void getUserById_shouldThrowWhenUserNotFound() {

        mockAdmin();

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> adminUserService.getUserById(99L));
    }

    @Test
    void nonAdmin_shouldNotAccessUserManagement() {

        User recruiter = new User();
        recruiter.setEmail("recruiter@test.com");
        recruiter.setRole(Role.RECRUITER);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "recruiter@test.com", null, List.of()));

        when(userRepository.findByEmail("recruiter@test.com"))
                .thenReturn(Optional.of(recruiter));

        assertThrows(AccessDeniedException.class,
                () -> adminUserService.getAllUsers(
                        PageRequest.of(0, 10)));
    }

    @Test
    void updateUserRole_shouldUpdateRole() {

        mockAdmin();

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(freelancer));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRole(Role.RECRUITER);

        var result = adminUserService.updateUserRole(2L, request);

        assertEquals(Role.RECRUITER, result.getRole());
        verify(userRepository).save(freelancer);
    }

    @Test
    void updateUserRole_shouldPreventSelfRoleChange() {

        mockAdmin();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(admin));

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRole(Role.FREELANCER);

        assertThrows(AccessDeniedException.class,
                () -> adminUserService.updateUserRole(1L, request));
    }

    @Test
    void updateUserRole_shouldPreventDemotingLastAdmin() {

        mockAdmin();

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(createSecondAdmin()));

        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L);

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRole(Role.FREELANCER);

        assertThrows(AccessDeniedException.class,
                () -> adminUserService.updateUserRole(2L, request));
    }

    @Test
    void deactivateUser_shouldDeactivateAccount() {

        mockAdmin();

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(freelancer));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = adminUserService.deactivateUser(2L);

        assertFalse(result.isActive());
        verify(userRepository).save(freelancer);
    }

    @Test
    void activateUser_shouldActivateAccount() {

        mockAdmin();

        freelancer.setActive(false);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(freelancer));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = adminUserService.activateUser(2L);

        assertTrue(result.isActive());
        verify(userRepository).save(freelancer);
    }

    @Test
    void deactivateUser_shouldPreventSelfDeactivation() {

        mockAdmin();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(admin));

        assertThrows(AccessDeniedException.class,
                () -> adminUserService.deactivateUser(1L));
    }

    @Test
    void deactivateUser_shouldPreventDeactivatingLastAdmin() {

        mockAdmin();

        User secondAdmin = createSecondAdmin();

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(secondAdmin));

        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L);

        assertThrows(AccessDeniedException.class,
                () -> adminUserService.deactivateUser(2L));
    }

    // @Test
    // void deleteUser_shouldDeleteNormalUser() {

    //     mockAdmin();

    //     when(userRepository.findById(2L))
    //             .thenReturn(Optional.of(freelancer));

    //     adminUserService.deleteUser(2L);

    //     verify(userRepository).delete(freelancer);
    // }

    // @Test
    // void deleteUser_shouldPreventSelfDeletion() {

    //     mockAdmin();

    //     when(userRepository.findById(1L))
    //             .thenReturn(Optional.of(admin));

    //     assertThrows(AccessDeniedException.class,
    //             () -> adminUserService.deleteUser(1L));
    // }

    // @Test
    // void deleteUser_shouldPreventDeletingLastAdmin() {

    //     mockAdmin();

    //     User secondAdmin = createSecondAdmin();

    //     when(userRepository.findById(2L))
    //             .thenReturn(Optional.of(secondAdmin));

    //     when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L);

    //     assertThrows(AccessDeniedException.class,
    //             () -> adminUserService.deleteUser(2L));
    // }
}

package com.project.skill_connect.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.skill_connect.entities.Role;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.exceptions.AdminRegistrationNotAllowedException;
import com.project.skill_connect.exceptions.EmailAlreadyExistsException;
import com.project.skill_connect.repositories.UserRepository;

@Service 
public class UserService {
    
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + user.getEmail());
        }

        if(user.getRole() == Role.ADMIN) {
            throw new AdminRegistrationNotAllowedException("Admin registration is not allowed.");
        }
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}

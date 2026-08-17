package com.expense.expensesplitter.service;

import com.expense.expensesplitter.config.JwtUtil;
import com.expense.expensesplitter.dto.AuthRequest;
import com.expense.expensesplitter.dto.AuthResponse;
import com.expense.expensesplitter.exception.ResourceNotFoundException;
import com.expense.expensesplitter.model.User;
import com.expense.expensesplitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

  public AuthResponse login(AuthRequest request) {

    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User", "email not found: " + request.getEmail()));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new BadCredentialsException("Invalid email or password");
    }

    String token = jwtUtil.generateToken(user.getUsername(), user.getId());

    return AuthResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .username(user.getUsername())
            .token(token)
            .tokenType("Bearer")
            .message("Login successful")
            .build();
}

    public AuthResponse register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getId());

        // Send welcome email asynchronously
        try {
            emailService.sendWelcomeEmail(savedUser);
        } catch (Exception e) {
            // Log error but don't fail registration
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }

        return AuthResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .username(savedUser.getUsername())
                .token(token)
                .tokenType("Bearer")
                .message("Registration successful")
                .build();
    }
}
package com.quantnexus.service;

import com.quantnexus.domain.User;
import com.quantnexus.domain.enums.UserStatus;
import com.quantnexus.dto.auth.LoginRequest;
import com.quantnexus.dto.auth.LoginResponse;
import com.quantnexus.dto.auth.RegisterRequest;
import com.quantnexus.dto.auth.RegistrationResponse;
import com.quantnexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
     * Registers a new user with advanced security features.
     */
    public RegistrationResponse register(RegisterRequest request){
        //1. Check if user already exists : ensures email uniqueness
        if(userRepository.existsByEmail(request.email())){
            throw new RuntimeException("An account with this email already exists.");
        }

        // 2. Build and save the user entity
        User user = User.builder()
                .email(request.email())
                .fullName(request.fullName())
                .passwordHash(passwordEncoder.encode(request.password())) // Bcrypt Hashing
                .role(request.role())
                .userStatus(UserStatus.ACTIVE) // Defaulting to Active
                .failedLoginAttempts(0)        // Initializing security metrics
                .build();

        User savedUser = userRepository.save(user);

        // 3. Return the professional RegistrationResponse record
        return new RegistrationResponse(
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().name(),
                savedUser.getBaseCurrency(),
                "Welcome to QuantNexus💕",
                savedUser.getCreatedAt() // or use savedUser.getCreatedAt() if you prefer
        );
    }

    /*
     * Login with credentials and returns a token-ready LoginResponse.
     */
    public LoginResponse login(LoginRequest request){
        //1. Identify the user
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));

        // 2. Verify the cryptographic hash
        if(!passwordEncoder.matches(request.password(), user.getPasswordHash())){
            throw new RuntimeException("Invalid email or password.");
        }

        // 3. Update security metadata
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // 4. Return the structured LoginResponse record
        return new LoginResponse(
                "DUMMY-JWT-TOKEN-PHASE1",
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getBaseCurrency(),
                "Welcome back, " + user.getFullName()
        );
    }

}

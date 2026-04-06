package com.quantnexus.service;

import com.quantnexus.domain.User;
import com.quantnexus.domain.enums.UserStatus;
import com.quantnexus.dto.auth.LoginRequest;
import com.quantnexus.dto.auth.LoginResponse;
import com.quantnexus.dto.auth.RegisterRequest;
import com.quantnexus.dto.auth.RegistrationResponse;
import com.quantnexus.repository.UserRepository;
import com.quantnexus.security.JwtBlacklistService;
import com.quantnexus.security.JwtService;
import com.quantnexus.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtBlacklistService jwtBlacklistService;

    /*
     * Registers a new user with advanced security features.
     */
    @Transactional
    public RegistrationResponse register(RegisterRequest request){
        log.info("Auth: Initiating registration process for [{}]", request.email());

        // 1. Check if user already exists
        if(userRepository.existsByEmail(request.email())){
            log.warn("Auth Block: Registration failed. Email [{}] is already registered.", request.email());
            throw new IllegalArgumentException("An account with this email already exists.");
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

        log.info("Auth: Successfully provisioned new user [{}] with internal ID [{}]", savedUser.getEmail(), savedUser.getId());

        // 3. Return the professional RegistrationResponse record
        return new RegistrationResponse(
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().name(),
                savedUser.getBaseCurrency(),
                "Welcome to QuantNexus💕! Your account is ready.",
                savedUser.getCreatedAt()
        );
    }

    /**
     * Authenticates credentials and issues a stateless Identity Passport (JWT).
     */
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public LoginResponse login(LoginRequest request){
        log.debug("Auth: Login attempt for [{}]", request.email());

        // 1. Identify User First (Required for tracking failures)
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if(user.getUserStatus() == UserStatus.INACTIVE){
            throw new IllegalArgumentException("Account is locked. Please contact support.");
        }

        // 2. Attempt Authentication
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        }catch (Exception e){
            // 3. Handle Failure (Dirty Checking saves this automatically despite the exception)
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if(user.getFailedLoginAttempts() >= 5){
                log.warn("Maximum login attempt reached, account has been locked");
                user.setUserStatus(UserStatus.INACTIVE);
            }
            log.warn("Auth Block: Authentication failed for [{}]", request.email());
            throw new IllegalArgumentException("Invalid email or password.");
        }

        // 4. Handle Success and update metadata
        user.setLastLoginAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);

        SecurityUser securityUser = new SecurityUser(user);
        String jwtToken = jwtService.generateToken(securityUser);

        log.info("Auth: JWT issued successfully for [{}]", user.getEmail());

        return new LoginResponse(
                jwtToken,
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getBaseCurrency(),
                "Welcome back, " + user.getFullName()
        );
    }


    // -----------------------------------------
    // LOGOUT
    // -----------------------------------------
    /*
     * Logout = invalidate the current access token
     * @Param authorizationHeader  The raw "Authorization: Bearer <token>" header
    */
    public void logout(String authHeader){
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            log.warn("⚠️ Security Alert: Malformed logout request intercepted.");
            throw new IllegalArgumentException("No valid Authorization header provided.");
        }

        String jwtToken = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(jwtToken);

        jwtBlacklistService.blacklistToken(jwtToken);

        log.info("🔒 Cryptographic Identity Passport Revoked for session.");
        log.info("✅ User logged out successfully: {}", userEmail);

    }


}

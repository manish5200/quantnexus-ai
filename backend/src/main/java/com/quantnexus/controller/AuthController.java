package com.quantnexus.controller;

import com.quantnexus.dto.auth.LoginRequest;
import com.quantnexus.dto.auth.LoginResponse;
import com.quantnexus.dto.auth.RegisterRequest;
import com.quantnexus.dto.auth.RegistrationResponse;
import com.quantnexus.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Entry point for User Authentication and Registration.
 * Accessible without JWT for these specific endpoints.
 * @author Manish Singh
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    //Registers a new user and returns their profile summary.
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse>registerUser(
            @Valid @RequestBody RegisterRequest request
            ){
        log.info("API Request: Registering new user");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    //Authenticates a user and returns a JWT Bearer token.
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("API Request: User login");
        return ResponseEntity.ok(authService.login(request));
    }

    //Manually Logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String,String>>logout(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        authService.logout(authHeader);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully. See you soon! 👋"));
    }

}

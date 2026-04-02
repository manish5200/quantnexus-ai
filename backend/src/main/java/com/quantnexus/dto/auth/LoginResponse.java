package com.quantnexus.dto.auth;

import com.quantnexus.domain.enums.BaseCurrency;

/**
 * @author Manish Singh
 */
public record LoginResponse(
        String token,          // The JWT Bearer token
        String email,          // User identity
        String fullName,       // For personalized "Welcome, Manish" UI
        String role,           // For RBAC identification
        BaseCurrency currency, // Provides "INR" for data and "₹" for UI symbols
        String message         // Human-readable status or personalized greeting
) {}
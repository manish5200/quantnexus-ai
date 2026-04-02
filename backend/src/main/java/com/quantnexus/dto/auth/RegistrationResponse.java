package com.quantnexus.dto.auth;

import com.quantnexus.domain.enums.BaseCurrency;
import java.time.LocalDateTime;

/**
 * @author Manish Singh
 */
public record RegistrationResponse(
        String email,
        String fullName,
        String role,           // Added: Confirms the assigned access level
        BaseCurrency baseCurrency,
        String message,
        LocalDateTime registeredAt
) {}
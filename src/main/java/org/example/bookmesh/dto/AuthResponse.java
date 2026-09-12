package org.example.bookmesh.dto;

import org.example.bookmesh.model.Role;

public record AuthResponse(
        String token,
        Long userId,
        String fullName,
        String email,
        Role role
) {
}
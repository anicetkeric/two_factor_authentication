package com.bootlabs.model.dto;

public record AuthenticationResponseDTO(
        String accessToken,
        String refreshToken,
        boolean isMfa
) {
}

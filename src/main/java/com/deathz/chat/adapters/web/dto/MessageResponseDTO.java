package com.deathz.chat.adapters.web.dto;

import java.time.LocalDateTime;

public record MessageResponseDTO(
        String content,
        LocalDateTime createdAt) {
}

package com.deathz.chat.adapters.web.dto;

import java.time.LocalDateTime;

public record MessageResponseDTO(
        int sequenceNumber,
        String content,
        LocalDateTime createdAt) {
}

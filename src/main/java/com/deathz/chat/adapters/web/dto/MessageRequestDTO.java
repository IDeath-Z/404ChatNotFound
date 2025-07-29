package com.deathz.chat.adapters.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record MessageRequestDTO(
                UUID conversationId,
                @NotBlank String content) {

}

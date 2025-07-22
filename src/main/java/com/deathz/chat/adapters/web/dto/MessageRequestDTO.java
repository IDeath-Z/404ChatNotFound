package com.deathz.chat.adapters.web.dto;

import java.util.UUID;

public record MessageRequestDTO(
        UUID conversationId,
        String content) {

}

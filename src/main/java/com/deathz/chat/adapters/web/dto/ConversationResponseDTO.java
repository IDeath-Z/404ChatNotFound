package com.deathz.chat.adapters.web.dto;

import java.util.List;
import java.util.UUID;

public record ConversationResponseDTO(
        UUID id,
        String title,
        List<MessageResponseDTO> messages) {

}

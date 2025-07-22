package com.deathz.chat.adapters.web.dto;

import java.util.List;

public record ConversationResponseDTO(
        String title,
        List<MessageResponseDTO> messages) {

}

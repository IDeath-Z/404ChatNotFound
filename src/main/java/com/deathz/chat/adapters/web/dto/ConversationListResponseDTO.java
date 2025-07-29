package com.deathz.chat.adapters.web.dto;

import java.util.List;
import java.util.UUID;

public record ConversationListResponseDTO(
                UUID id,
                List<MessageResponseDTO> messages) {

}

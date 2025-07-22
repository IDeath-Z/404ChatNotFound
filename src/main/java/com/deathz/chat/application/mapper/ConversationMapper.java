package com.deathz.chat.application.mapper;

import org.springframework.stereotype.Component;

import com.deathz.chat.adapters.web.dto.ConversationResponseDTO;
import com.deathz.chat.domain.Conversation;

@Component
public class ConversationMapper {

    public ConversationResponseDTO toResponseDTO(Conversation conversation) {
        return new ConversationResponseDTO(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getMessages().stream()
                        .map(message -> new MessageMapper().toResponseDTO(message))
                        .toList());
    }
}

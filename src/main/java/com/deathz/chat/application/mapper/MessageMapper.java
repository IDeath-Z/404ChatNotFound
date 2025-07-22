package com.deathz.chat.application.mapper;

import org.springframework.stereotype.Component;

import com.deathz.chat.adapters.web.dto.MessageResponseDTO;
import com.deathz.chat.domain.Message;

@Component
public class MessageMapper {

    public MessageResponseDTO toResponseDTO(Message message) {
        return new MessageResponseDTO(
                message.getContent(),
                message.getCreatedAt());
    }
}

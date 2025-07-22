package com.deathz.chat.application.service;

import java.util.List;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.deathz.chat.adapters.web.dto.ConversationRequestDTO;
import com.deathz.chat.adapters.web.dto.ConversationResponseDTO;
import com.deathz.chat.adapters.web.dto.MessageRequestDTO;
import com.deathz.chat.adapters.web.dto.MessageResponseDTO;
import com.deathz.chat.application.mapper.ConversationMapper;
import com.deathz.chat.application.mapper.MessageMapper;
import com.deathz.chat.domain.Conversation;
import com.deathz.chat.domain.Message;
import com.deathz.chat.infrastructure.persistence.ConversationRepository;
import com.deathz.chat.infrastructure.persistence.MessageRepository;

@Service
public class ChatService {

    @Autowired
    private OllamaChatModel chatModel;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Value("${LLM_MODEL}")
    private String llmModel;

    public ConversationResponseDTO addConversation(ConversationRequestDTO request) {

        Conversation conversation = new Conversation(request.title(), llmModel);

        return conversationMapper.toResponseDTO(
                conversationRepository.save(conversation));
    }

    public List<MessageResponseDTO> addMessage(MessageRequestDTO request) {

        Conversation conversation = conversationRepository.findById(request.conversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        Message userMessage = new Message(conversation, request.content(), true);
        messageRepository.save(userMessage);

        String llmResponse = chatModel.call(userMessage.getContent());

        Message llmMessage = new Message(conversation, llmResponse, false);
        messageRepository.save(llmMessage);

        return List.of(messageMapper.toResponseDTO(userMessage), messageMapper.toResponseDTO(llmMessage));
    }
}

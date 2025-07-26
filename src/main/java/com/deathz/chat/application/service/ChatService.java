package com.deathz.chat.application.service;

import java.util.List;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.deathz.chat.adapters.web.dto.ConversationRequestDTO;
import com.deathz.chat.adapters.web.dto.ConversationResponseDTO;
import com.deathz.chat.adapters.web.dto.MessageRequestDTO;
import com.deathz.chat.adapters.web.dto.MessageResponseDTO;
import com.deathz.chat.application.mapper.ConversationMapper;
import com.deathz.chat.application.mapper.MessageMapper;
import com.deathz.chat.domain.Conversation;
import com.deathz.chat.domain.Message;
import com.deathz.chat.domain.exceptions.ConversationNotFoundException;
import com.deathz.chat.domain.exceptions.FailedToParseRequestException;
import com.deathz.chat.domain.exceptions.UnsupportedFileTypeException;
import com.deathz.chat.infrastructure.persistence.ConversationRepository;
import com.deathz.chat.infrastructure.persistence.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.deathz.chat.application.service.ModelService.Model;

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

    @Autowired
    private FileService fileService;

    @Autowired
    private ModelService modelService;

    public List<MessageResponseDTO> choseModel(String strRequest, MultipartFile file) {

        if (file == null || file.isEmpty()) {

            return processTextContent(strRequest, null);
        }

        if (fileService.getFileType(file).startsWith("image/")) {

            return processImageContent(strRequest, file);
        } else if (fileService.getFileType(file).startsWith("text/")) {

            return processTextContent(strRequest, file);
        } else if (fileService.getFileType(file).startsWith("application/")) {

            return processTextContent(strRequest, file);
        } else {

            throw new UnsupportedFileTypeException();
        }

    }

    public ConversationResponseDTO addConversation(ConversationRequestDTO request) {

        Conversation conversation = new Conversation(request.title(), modelService.getCurrentModel());

        return conversationMapper.toResponseDTO(
                conversationRepository.save(conversation));
    }

    public List<MessageResponseDTO> processTextContent(String strRequest, MultipartFile file) {

        MessageRequestDTO request = parseRequest(strRequest, MessageRequestDTO.class);

        StringBuilder finalMessage = new StringBuilder(request.content());

        Conversation conversation = conversationRepository.findById(request.conversationId())
                .orElseThrow(() -> new ConversationNotFoundException(request.conversationId()));

        Message userMessage = new Message(conversation, finalMessage.toString(), true);
        messageRepository.save(userMessage);

        if (file != null && !file.isEmpty()) {

            finalMessage.append(fileService.extractText(file));
        }

        ChatResponse response = chatModel.call(
                new Prompt(
                        finalMessage.toString(),
                        OllamaOptions.builder()
                                .build()));

        String llmResponse = response.getResult().getOutput().getText();

        Message llmMessage = new Message(conversation, llmResponse, false);
        messageRepository.save(llmMessage);

        return conversation.getMessages().stream()
                .map(messageMapper::toResponseDTO)
                .toList();
    }

    public List<MessageResponseDTO> processImageContent(String strRequest, MultipartFile file) {

        MessageRequestDTO request = parseRequest(strRequest, MessageRequestDTO.class);

        Conversation conversation = conversationRepository.findById(request.conversationId())
                .orElseThrow(() -> new ConversationNotFoundException(request.conversationId()));

        Message userMessage = new Message(conversation, request.content(), true);
        messageRepository.save(userMessage);

        Media imageMedia = fileService.buildMedia(file);

        UserMessage multimodalMessage = UserMessage.builder()
                .text(request.content())
                .media(imageMedia)
                .build();

        ChatResponse response = chatModel.call(
                new Prompt(
                        multimodalMessage,
                        OllamaOptions.builder()
                                .model(Model.GEMMA3.getValue())
                                .build()));

        String llmResponse = response.getResult().getOutput().getText();

        Message llmMessage = new Message(conversation, llmResponse, false);
        messageRepository.save(llmMessage);

        return conversation.getMessages().stream()
                .map(messageMapper::toResponseDTO)
                .toList();
    }

    private <T> T parseRequest(String strRequest, Class<T> classType) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(strRequest, classType);
        } catch (Exception e) {

            throw new FailedToParseRequestException(e);
        }
    }
}

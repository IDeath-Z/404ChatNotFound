package com.deathz.chat.application.service;

import java.util.List;

import org.springframework.ai.ollama.OllamaChatModel;
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

        if (fileService.getFileType(file).startsWith("image/")) {

            throw new UnsupportedFileTypeException();
            // return processImageContent(strRequest, file);
        } else if (fileService.getFileType(file).startsWith("text/")) {

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

        modelService.setCurrentModel(Model.LLAMA3.getValue());

        MessageRequestDTO request = parseRequest(strRequest, MessageRequestDTO.class);

        StringBuilder finalMessage = new StringBuilder(request.content());

        Conversation conversation = conversationRepository.findById(request.conversationId())
                .orElseThrow(() -> new ConversationNotFoundException(request.conversationId()));

        Message userMessage = new Message(conversation, finalMessage.toString(), true);
        messageRepository.save(userMessage);

        finalMessage.append(fileService.extractText(file));

        String llmResponse = chatModel.call(finalMessage.toString());

        Message llmMessage = new Message(conversation, llmResponse, false);
        messageRepository.save(llmMessage);

        return conversation.getMessages().stream()
                .map(messageMapper::toResponseDTO)
                .toList();
    }

    public List<MessageResponseDTO> processImageContent(String strRequest, MultipartFile file) {

        // Need to implement image processing logic later, for now: null

        return null;
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

package com.deathz.chat.application.service;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.deathz.chat.adapters.web.dto.ConversationRequestDTO;
import com.deathz.chat.adapters.web.dto.ConversationResponseDTO;
import com.deathz.chat.adapters.web.dto.ConversationListResponseDTO;
import com.deathz.chat.adapters.web.dto.MessageRequestDTO;
import com.deathz.chat.application.mapper.ConversationMapper;
import com.deathz.chat.application.mapper.MessageMapper;
import com.deathz.chat.domain.Conversation;
import com.deathz.chat.domain.Message;
import com.deathz.chat.domain.exceptions.ConversationNotFoundException;
import com.deathz.chat.domain.exceptions.FailedToParseRequestException;
import com.deathz.chat.infrastructure.persistence.ConversationRepository;
import com.deathz.chat.infrastructure.persistence.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.deathz.chat.application.service.ModelService.Model;

@Service
public class ChatService {

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

    public ConversationListResponseDTO chat(String strRequest, MultipartFile file) {

        MessageRequestDTO request = parseRequest(strRequest, MessageRequestDTO.class);
        Conversation conversation = verifyConversation(request);

        Message userMessage = new Message(conversation, request.content(), true);
        messageRepository.save(userMessage);

        UserMessage message = buildUserMessage(request, file);
        ;

        String response = modelService
                .getChatModel()
                .call(new Prompt(message, OllamaOptions.builder()
                        .model(modelService.getCurrentModel())
                        .build()))
                .getResult()
                .getOutput()
                .getText();

        Message llmMessage = new Message(conversation, response, false);
        messageRepository.save(llmMessage);
        conversation.getMessages().add(userMessage);
        conversation.getMessages().add(llmMessage);
        conversationRepository.save(conversation);

        return new ConversationListResponseDTO(conversation.getId(), conversation.getMessages().stream()
                .map(messageMapper::toResponseDTO)
                .toList());
    }

    private UserMessage buildUserMessage(MessageRequestDTO request, MultipartFile file) {

        if (fileService.getFileType(file).startsWith("image/")) {

            modelService.setCurrentModel(Model.GEMMA3.getValue());

            return UserMessage.builder()
                    .text(request.content())
                    .media(fileService.buildMedia(file))
                    .build();

        } else if (fileService.getFileType(file).startsWith("application/")) {

            modelService.setCurrentModel(Model.LLAMA3.getValue());

            return UserMessage.builder()
                    .text(request.content() + " " + fileService.extractText(file))
                    .build();
        } else {

            modelService.setCurrentModel(Model.LLAMA3.getValue());

            return UserMessage.builder()
                    .text(request.content())
                    .build();
        }
    }

    public ConversationResponseDTO addConversation(ConversationRequestDTO request) {

        Conversation conversation = new Conversation(request.title(), modelService.getCurrentModel());

        return conversationMapper.toResponseDTO(
                conversationRepository.save(conversation));
    }

    private Conversation verifyConversation(MessageRequestDTO request) {

        if (request.conversationId() == null) {

            return conversationRepository.save(new Conversation(createTitle(request.content()),
                    modelService.getCurrentModel()));
        } else {

            return conversationRepository.findById(request.conversationId())
                    .orElseThrow(() -> new ConversationNotFoundException(request.conversationId()));
        }
    }

    // public ConversationListResponseDTO processTextContent(String strRequest,
    // MultipartFile file) {

    // StringBuilder finalMessage = new StringBuilder(request.content());

    // Message userMessage = new Message(conversation, finalMessage.toString(),
    // true);
    // messageRepository.save(userMessage);

    // if (file != null && !file.isEmpty()) {

    // finalMessage.append(fileService.extractText(file));
    // }

    // ChatResponse response = modelService.getChatModel().call(
    // new Prompt(
    // finalMessage.toString(),
    // OllamaOptions.builder()
    // .build()));

    // String llmResponse = response.getResult().getOutput().getText();

    // Message llmMessage = new Message(conversation, llmResponse, false);
    // messageRepository.save(llmMessage);

    // return new ConversationListResponseDTO(conversation.getId(),
    // conversation.getMessages().stream()
    // .map(messageMapper::toResponseDTO)
    // .toList());
    // }

    private <T> T parseRequest(String strRequest, Class<T> classType) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(strRequest, classType);
        } catch (Exception e) {

            throw new FailedToParseRequestException(e);
        }
    }

    private String createTitle(String title) {
        String prompt = "Create a short and concise title (max 25 characters) for the conversation based on the following request: "
                + title;

        String generated = modelService.getChatModel().call(
                new Prompt(prompt, OllamaOptions.builder()
                        .model(Model.GEMMA3.getValue())
                        .topK(10)
                        .topP(0.1)
                        .temperature(0.1)
                        .build()))
                .getResult().getOutput().getText();

        return generated.length() > 255 ? generated.substring(0, 255) : generated;
    }
}

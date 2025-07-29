package com.deathz.chat.adapters.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.deathz.chat.adapters.web.dto.ConversationRequestDTO;
import com.deathz.chat.application.service.ChatService;
import com.deathz.chat.application.service.ModelService;

@RestController
@CrossOrigin
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ModelService modelService;

    @PostMapping("/conversation")
    public ResponseEntity<?> generateConversation(@RequestBody ConversationRequestDTO request) {

        return ResponseEntity.ok().body(chatService.addConversation(request));
    }

    // to use: {"conversationId":"UUID","content":"Message"}
    @PostMapping(value = "/message", consumes = "multipart/form-data")
    public ResponseEntity<?> generateMessage(@RequestPart("message") String messageRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {

        return ResponseEntity.ok().body(chatService.chat(messageRequest, file));
    }

    @GetMapping("/model")
    public String getName() {

        return modelService.getCurrentModel();
    }

    // @GetMapping(value = "/generateStream", produces =
    // MediaType.TEXT_EVENT_STREAM_VALUE)
    // public Flux<MessageResponseDTO> generateStream(@RequestParam(value =
    // "message") String message) {

    // Prompt prompt = new Prompt(new UserMessage(message));
    // return chatModel.stream(prompt)
    // .map(generation -> new
    // MessageResponseDTO(generation.getResult().getOutput().getText()));
    // }
}

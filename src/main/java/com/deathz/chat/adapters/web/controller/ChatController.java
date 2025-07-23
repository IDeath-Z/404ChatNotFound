package com.deathz.chat.adapters.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.deathz.chat.adapters.web.dto.ConversationRequestDTO;
import com.deathz.chat.adapters.web.dto.MessageRequestDTO;
import com.deathz.chat.application.service.ChatService;

import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@CrossOrigin
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PutMapping("conversation")
    public ResponseEntity<?> generateConversation(@RequestBody ConversationRequestDTO request) {

        return ResponseEntity.ok().body(chatService.addConversation(request));
    }

    @PutMapping("message")
    public ResponseEntity<?> generateMessage(@RequestBody MessageRequestDTO request) {

        return ResponseEntity.ok().body(chatService.addMessage(request));
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty!");
        }
        return ResponseEntity.ok("File uploaded: " + file.getOriginalFilename());
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

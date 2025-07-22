package com.deathz.chat.adapters.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deathz.chat.adapters.web.dto.ConversationRequestDTO;
import com.deathz.chat.adapters.web.dto.MessageRequestDTO;
import com.deathz.chat.application.service.ChatService;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

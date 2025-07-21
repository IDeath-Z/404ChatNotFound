package com.deathz.chat.adapters.web.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deathz.chat.adapters.web.dto.MessageResponseDTO;

import reactor.core.publisher.Flux;

@RestController
@CrossOrigin
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private OllamaChatModel chatModel;

    @GetMapping("/generate")
    public MessageResponseDTO generate(@RequestParam(value = "message") String message) {

        return new MessageResponseDTO(this.chatModel.call(message));
    }

    @GetMapping(value = "/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MessageResponseDTO> generateStream(@RequestParam(value = "message") String message) {

        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt)
                .map(generation -> new MessageResponseDTO(generation.getResult().getOutput().getText()));
    }
}

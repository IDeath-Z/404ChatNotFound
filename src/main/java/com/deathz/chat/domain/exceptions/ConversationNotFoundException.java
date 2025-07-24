package com.deathz.chat.domain.exceptions;

import java.util.UUID;

public class ConversationNotFoundException extends RuntimeException {

    public ConversationNotFoundException(UUID conversationId) {

        super("Conversation with ID " + conversationId + " not found");
    }

    public ConversationNotFoundException(String message, Throwable cause) {

        super(message, cause);
    }

    public ConversationNotFoundException(Throwable cause) {

        super("Conversation not found", cause);
    }

}

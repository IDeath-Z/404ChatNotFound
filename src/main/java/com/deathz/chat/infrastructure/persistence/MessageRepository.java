package com.deathz.chat.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deathz.chat.domain.Message;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByConversationId(UUID conversationId);

    List<Message> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    List<Message> findByConversationIdAndIsUserMessage(UUID conversationId, boolean isUserMessage);
}

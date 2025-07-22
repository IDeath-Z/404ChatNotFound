package com.deathz.chat.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deathz.chat.domain.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    List<Conversation> findByUserId(UUID userId);
}

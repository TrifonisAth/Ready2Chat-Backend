package com.socialmedia.socialmedia.dto;

import java.time.LocalDateTime;

public record ConversationDTO(long conversationId, MessageDTO[] messages, PersonDTO friend, String lastMessageDate) { }

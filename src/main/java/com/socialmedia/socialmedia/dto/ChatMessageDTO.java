package com.socialmedia.socialmedia.dto;

public record ChatMessageDTO(PersonDTO sender, PersonDTO recipient, String message, long friendshipId, String timestamp) {
}

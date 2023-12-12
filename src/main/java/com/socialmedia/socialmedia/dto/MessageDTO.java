package com.socialmedia.socialmedia.dto;

public record MessageDTO(long messageId, long friendshipId, PersonDTO sender, PersonDTO recipient, String message, String timestamp) {
}

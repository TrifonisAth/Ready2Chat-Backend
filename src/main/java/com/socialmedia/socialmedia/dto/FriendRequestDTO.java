package com.socialmedia.socialmedia.dto;

public record FriendRequestDTO (long requestId, PersonDTO sender, PersonDTO recipient) { }

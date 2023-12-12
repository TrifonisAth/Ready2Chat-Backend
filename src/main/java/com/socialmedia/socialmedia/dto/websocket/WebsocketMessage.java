package com.socialmedia.socialmedia.dto.websocket;

public interface WebsocketMessage {
    long getTo();
    void setTo(long to);
    long getFrom();
    void setFrom(long from);
    String getEvent();
    void setEvent(String event);
}

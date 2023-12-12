package com.socialmedia.socialmedia.dto.websocket.text;

import com.socialmedia.socialmedia.dto.MessageDTO;
import com.socialmedia.socialmedia.dto.websocket.WebsocketMessage;

public class SocketMessage implements WebsocketMessage {
    private String event;
    private MessageDTO data;
    private long to;
    private long from;

    public SocketMessage(String event, MessageDTO data, long to, long from) {
        this.event = event;
        this.data = data;
        this.to = to;
        this.from = from;
    }

    public SocketMessage() {}

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public void setEvent(String event) {
        this.event = event;
    }

    public MessageDTO getData() {
        return data;
    }

    public void setData(MessageDTO data) {
        this.data = data;
    }

    @Override
    public long getTo() {
        return to;
    }

    @Override
    public void setTo(long to) {
        this.to = to;
    }

    @Override
    public long getFrom() {
        return from;
    }

    @Override
    public void setFrom(long from) {
        this.from = from;
    }
}

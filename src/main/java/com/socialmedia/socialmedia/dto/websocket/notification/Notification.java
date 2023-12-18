package com.socialmedia.socialmedia.dto.websocket.notification;

import com.socialmedia.socialmedia.dto.websocket.WebsocketMessage;

public class Notification implements WebsocketMessage {
    private String event;
    private long to;
    private long from;

    public Notification(String event, long from, long to) {
        this.event = event;
        this.to = to;
        this.from = from;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public void setEvent(String event) {
        this.event = event;
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

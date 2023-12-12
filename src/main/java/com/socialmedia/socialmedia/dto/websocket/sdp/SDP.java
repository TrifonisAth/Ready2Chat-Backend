package com.socialmedia.socialmedia.dto.websocket.sdp;

import com.socialmedia.socialmedia.dto.websocket.WebsocketMessage;

public class SDP implements WebsocketMessage {
    private String event;
    private SDPInfo data;
    private long to;
    private long from;


    public SDP() {}

    public SDP(String event, SDPInfo data, long to, long from) {
        this.event = event;
        this.data = data;
        this.to = to;
        this.from = from;
    }

    @Override
    public String toString() {
        return "SDP{" +
                "event='" + event + '\'' +
                ", data=" + data +
                ", to=" + to +
                ", from=" + from +
                '}';
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public void setEvent(String event) {
        this.event = event;
    }

    public SDPInfo getData() {
        return data;
    }

    public void setData(SDPInfo data) {
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

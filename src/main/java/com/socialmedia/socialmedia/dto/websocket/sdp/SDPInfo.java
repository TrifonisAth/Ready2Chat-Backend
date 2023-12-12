package com.socialmedia.socialmedia.dto.websocket.sdp;

public class SDPInfo {
    String sdp;
    String type;

    public SDPInfo() {}

    public SDPInfo(String sdp, String type) {
        this.sdp = sdp;
        this.type = type;
    }

    @Override
    public String toString() {
        return "SDPInfo{" +
                "sdp='" + sdp + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

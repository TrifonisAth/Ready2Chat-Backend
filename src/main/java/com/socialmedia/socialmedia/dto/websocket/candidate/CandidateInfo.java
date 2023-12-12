package com.socialmedia.socialmedia.dto.websocket.candidate;

public class CandidateInfo {
    String candidate;
    String sdpMLineIndex;
    String sdpMid;
    String usernameFragment;

    public CandidateInfo() {}

    public CandidateInfo(String candidate, String sdpMLineIndex, String sdpMid, String usernameFragment) {
        this.candidate = candidate;
        this.sdpMLineIndex = sdpMLineIndex;
        this.sdpMid = sdpMid;
        this.usernameFragment = usernameFragment;
    }

    @Override
    public String toString() {
        return "CandidateInfo{" +
                "candidate='" + candidate + '\'' +
                ", sdpMLineIndex='" + sdpMLineIndex + '\'' +
                ", sdpMid='" + sdpMid + '\'' +
                ", usernameFragment='" + usernameFragment + '\'' +
                '}';
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public void setSdpMLineIndex(String sdpMLineIndex) {
        this.sdpMLineIndex = sdpMLineIndex;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public void setSdpMid(String sdpMid) {
        this.sdpMid = sdpMid;
    }

    public String getUsernameFragment() {
        return usernameFragment;
    }

    public void setUsernameFragment(String usernameFragment) {
        this.usernameFragment = usernameFragment;
    }
}

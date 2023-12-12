package com.socialmedia.socialmedia.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="token_id")
    private Long tokenId;

    @Column(name="token")
    private String token;

    @Column(name="user_id")
    private Long userId;

    @Column(name="creation_date")
    private LocalDateTime creationDate;

    @Column(name="expiration_date")
    private LocalDateTime expirationDate;

    public RefreshToken() {
    }

    public RefreshToken(String token, Long userId, LocalDateTime creationDate, LocalDateTime expirationDate) {
        this.token = token;
        this.userId = userId;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
    }

    public RefreshToken(String token, Long userId) {
        this.token = token;
        this.userId = userId;
        this.creationDate = LocalDateTime.now();
        this.expirationDate = LocalDateTime.now().plusDays(7);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public Long getTokenId() {
        return tokenId;
    }
}

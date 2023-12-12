package com.socialmedia.socialmedia.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
public class VerificationToken {
    @Id
    @Column(name="user_id")
    private Long id;

    @Column(name="token")
    private String token;

    @OneToOne
    @JoinColumn(name="user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name="expiration_time")
    private LocalDateTime expirationTime;

    public VerificationToken(String token, User user) {
        this.id = user.getId();
        this.token = token;
        this.user = user;
        this.expirationTime = LocalDateTime.now().plusMinutes(10);
    }

    public VerificationToken(String token) {
        this.token = token;
        this.expirationTime = LocalDateTime.now().plusMinutes(10);
    }

    public VerificationToken() {
        this.expirationTime = LocalDateTime.now().plusMinutes(10);
    }

    @Override
    public String toString() {
        return "VerificationToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", user=" + user +
                ", expirationTime=" + expirationTime +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationTime);
    }
}

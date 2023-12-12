package com.socialmedia.socialmedia.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendships")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id1", referencedColumnName = "user_id")
    private User user1;

    @ManyToOne
    @JoinColumn(name="user_id2", referencedColumnName = "user_id")
    private User user2;

    @Column(name="date")
    private LocalDateTime date;

    public Friendship(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.date = LocalDateTime.now();
    }
    
    public Friendship() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}

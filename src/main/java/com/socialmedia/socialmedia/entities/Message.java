package com.socialmedia.socialmedia.entities;

import com.socialmedia.socialmedia.dto.MessageDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="conversation_id")
    private Long conversationId;

    @ManyToOne
    @JoinColumn(name="sender_id", referencedColumnName = "user_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name="recipient_id", referencedColumnName = "user_id")
    private User recipient;

    @Column(name="message")
    private String message;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    public Message(long conversationId, User sender, User recipient, String message) {
        // Get the friendship id from the sender and recipient
        this.conversationId = conversationId;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public Message() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User receiver) {
        this.recipient = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public MessageDTO toMessageDTO() {
        return new MessageDTO(
                id,
                conversationId,
                sender.toPersonDTO(),
                recipient.toPersonDTO(),
                message,
                createdAt.toString()
        );
    }
}

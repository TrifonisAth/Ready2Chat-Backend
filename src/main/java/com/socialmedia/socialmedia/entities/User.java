package com.socialmedia.socialmedia.entities;

import com.socialmedia.socialmedia.dto.FriendDTO;
import com.socialmedia.socialmedia.dto.PersonDTO;
import com.socialmedia.socialmedia.dto.user_credentials.UserDets;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(name="name")
    private String displayName;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="is_online")
    private boolean isOnline;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<Friendship> friendshipsAsUser1 = new HashSet<>();

    @OneToMany(mappedBy = "user2", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<Friendship> friendshipsAsUser2 = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "user",
            orphanRemoval = true)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sender", orphanRemoval = true)
    private final Set<FriendRequest> sentFriendRequests = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "receiver", orphanRemoval = true)
    private final Set<FriendRequest> receivedFriendRequests = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sender", orphanRemoval = true)
    private final Set<Message> sentMessages = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipient", orphanRemoval = true)
    private final Set<Message> receivedMessages = new HashSet<>();

    public Set<Message> getSentMessages() {
        return sentMessages;
    }

    public Set<Message> getReceivedMessages() {
        return receivedMessages;
    }

    public Set<FriendRequest> getSentFriendRequests() {
        return sentFriendRequests;
    }

    public Set<FriendRequest> getReceivedFriendRequests() {
        return receivedFriendRequests;
    }

    public Set<FriendRequest> getAllFriendRequests(){
        Set<FriendRequest> allRequests = new HashSet<>(sentFriendRequests);
        allRequests.addAll(receivedFriendRequests);
        return allRequests;
    }

    public Set<Friendship> getFriends() {
        Set<Friendship> friends = new HashSet<>(friendshipsAsUser1);
        friends.addAll(friendshipsAsUser2);
        return friends;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void addRole(String role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        Role newRole = new Role(this, role);
        roles.add(newRole);
    }

    public void removeRole(String role) {
        roles.removeIf(r -> r.getRole().equals(role));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isOnline=" + isOnline +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User() {}

    public User(User other){
        this.id = other.id;
        this.displayName = other.displayName;
        this.email = other.email;
        this.password = other.password;
        this.isOnline = other.isOnline;
        this.createdAt = other.createdAt;
        this.updatedAt = other.updatedAt;
        this.roles = new HashSet<>(other.roles);
    }

    public User(String displayName, String email, String password) {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
    }


    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Friendship> getFriendshipsAsUser1() {
        return friendshipsAsUser1;
    }

    public Set<Friendship> getFriendshipsAsUser2() {
        return friendshipsAsUser2;
    }

    public UserDets toUserDets(){
        return new UserDets(displayName, email, id);
    }

    public FriendDTO toFriendDTO(long friendshipId){
        return new FriendDTO(id, displayName, isOnline, friendshipId);
    }

    public PersonDTO toPersonDTO(){
        return new PersonDTO(id, displayName);
    }

    public VerificationToken getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(VerificationToken verificationToken) {
        this.verificationToken = verificationToken;
    }
}

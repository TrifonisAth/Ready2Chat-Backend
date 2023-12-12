package com.socialmedia.socialmedia.services.interfaces;

import com.socialmedia.socialmedia.dto.FriendRequestDTO;
import com.socialmedia.socialmedia.dto.MessageDTO;
import com.socialmedia.socialmedia.dto.FriendDTO;
import com.socialmedia.socialmedia.dto.user_credentials.RegistrationRequest;
import com.socialmedia.socialmedia.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    User register(RegistrationRequest user);
    void update(long id);
    boolean existsByDisplayName(String displayName);
    boolean existsByEmail(String email);
    void verify(User user);
    void delete(long id);
    boolean existsById(long id);
    void login(User user);
    void logout(User user);
    Optional<User> findUserByDisplayName(String displayName);
    User findUserById(long id);
    Optional<User> findUserByEmail(String email);
    List<User> findAll();
    Set<FriendDTO> getUserFriends(long userId);
    Set<FriendRequestDTO> getFriendRequests(long userId);
    Set<MessageDTO> getUserMessages(long userId);
    List<String> getUserRoles(long userId);
    void addRole(long userId, String role);
    void removeRole(long userId, String role);
}

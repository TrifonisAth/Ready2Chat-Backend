package com.socialmedia.socialmedia.dao.interfaces;

import com.socialmedia.socialmedia.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User findUserById(long id);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByDisplayName(String displayName);
    User getUserWithSentRequests(long id);
    User getUserWithReceivedRequests(long id);
    void save(User user);
    void delete(long id);
    void update(long id);
    void update(User user);
    void login(User user);
    void removeRole(long userId, String role);
    User getUserWithAllRequests(long userId);
    User getUserWithFriends(long userId);
    void addRole(long userId, String role);
    void removeFriendRequest(long friendRequestId);
    void removeFriendship(long friendshipId);
    List<User> findAll();
    void logout(User user);
    User getUserWithMessages(Long id);
    User getUserWithSentMessages(Long id);
    User getUserWithReceivedMessages(Long id);
    List<User> findAllWithRoles();
    User findUserWithRoles(long id);
    Optional<User> findUserByIdWithRoles(long id);
}

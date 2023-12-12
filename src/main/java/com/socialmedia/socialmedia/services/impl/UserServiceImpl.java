package com.socialmedia.socialmedia.services.impl;

import com.socialmedia.socialmedia.dao.impl.MessageDAOImpl;
import com.socialmedia.socialmedia.dao.impl.UserDAOImpl;
import com.socialmedia.socialmedia.dto.ConversationDTO;
import com.socialmedia.socialmedia.dto.FriendRequestDTO;
import com.socialmedia.socialmedia.dto.MessageDTO;
import com.socialmedia.socialmedia.dto.FriendDTO;
import com.socialmedia.socialmedia.dto.user_credentials.RegistrationRequest;
import com.socialmedia.socialmedia.entities.*;
import com.socialmedia.socialmedia.services.interfaces.EmailService;
import com.socialmedia.socialmedia.services.interfaces.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final EmailService emailService;
    private final UserDAOImpl userDAO;
    private final MessageDAOImpl messageDAO;

    public UserServiceImpl(EmailService emailService, UserDAOImpl userDAO, MessageDAOImpl messageDAO) {
        this.emailService = emailService;
        this.userDAO = userDAO;
        this.messageDAO = messageDAO;
    }

    @Override
    public User register(RegistrationRequest u){
        User user = new User(u.getDisplayName(), u.getEmail(), u.getPassword());
        userDAO.save(user);
        userDAO.addRole(user.getId(), "ROLE_TEMP");
        return user;
    }

    @Override
    public void update(long id) {
        userDAO.update(id);
    }

    @Override
    public void login(User user) {
        userDAO.login(user);
    }

    @Override
    public void logout(User user) {
        userDAO.logout(user);
    }


    @Override
    public boolean existsByEmail(String email) {
        return userDAO.findUserByEmail(email).isPresent();
    }

    @Override
    public void verify(User user) {
        userDAO.addRole(user.getId(), "ROLE_VERIFIED");
        userDAO.removeRole(user.getId(), "ROLE_TEMP");
    }

    @Override
    public void delete(long id) {
        userDAO.delete(id);
    }


    @Override
    public boolean existsByDisplayName(String displayName) {
        return userDAO.findUserByDisplayName(displayName).isPresent();
    }

    public Optional<User> findUserByDisplayName(String displayName) {
        return userDAO.findUserByDisplayName(displayName);
    }

    @Override
    public User findUserById(long id) {
        return userDAO.findUserById(id);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userDAO.findUserByEmail(email);
    }

    @Override
    public boolean existsById(long id) {
        return userDAO.findUserById(id) != null;
    }

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    public Set<FriendDTO> getUserFriends(long userId){
        User user = userDAO.getUserWithFriends(userId);
        // Create a set with the friends of the user, excluding the user itself.
        Set<FriendDTO> friends = new HashSet<>();
        for (Friendship friend : user.getFriends()) {
            if (Objects.equals(friend.getUser1().getId(), user.getId())) {
                friends.add(friend.getUser2().toFriendDTO(friend.getId()));
            } else {
                friends.add(friend.getUser1().toFriendDTO(friend.getId()));
            }
        }
        return friends;
    }

    @Override
    public Set<FriendRequestDTO> getFriendRequests(long userId){
        User user = userDAO.getUserWithAllRequests(userId);
        Set<FriendRequestDTO> friendRequests = new HashSet<>();
        for (FriendRequest fReq : user.getAllFriendRequests()) {
            if (Objects.equals(fReq.getReceiver().getId(), user.getId())) {
                friendRequests.add(new FriendRequestDTO(fReq.getId(), fReq.getSender().toPersonDTO(), user.toPersonDTO()));
            } else {
                friendRequests.add(new FriendRequestDTO(fReq.getId(), user.toPersonDTO(), fReq.getReceiver().toPersonDTO()));
            }
        }
        return friendRequests;
    }

    @Override
    public Set<MessageDTO> getUserMessages(long userId){
        User user = userDAO.getUserWithFriends(userId);
        Set<Message> messages = new HashSet<>();
        for (Friendship friend : user.getFriends()) {
            messages.addAll(messageDAO.findConversation(friend.getId()));
        }
        Set<MessageDTO> messageDTOs = new HashSet<>();
        for (Message message : messages) {
            messageDTOs.add(message.toMessageDTO());
        }
        return messageDTOs;
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    // TODO: Move this to a separate class.
    private void deleteUnverifiedUsers() {
        System.out.println("Deleting unverified users");
        List<User> users = userDAO.findAllWithRoles();
        for (User user : users) {
            boolean isVerified = false;
            for (Role role : user.getRoles()) {
                if (role.getRole().equals("ROLE_VERIFIED")) {
                    isVerified = true;
                    break;
                }
            }
            if (!isVerified) {
                userDAO.delete(user.getId());
            }
        }
    }

    @Override
    public List<String> getUserRoles(long userId) {
        User user = userDAO.findUserWithRoles(userId);
        if (user == null) {
            return null;
        }
        List<String> roles = new ArrayList<>();
        for (Role role : user.getRoles()) {
            roles.add(role.getRole());
        }
        return roles;
    }

    @Override
    public void addRole(long userId, String role) {
        userDAO.addRole(userId, role);
    }

    @Override
    public void removeRole(long userId, String role) {
        userDAO.removeRole(userId, role);
    }

    public List<ConversationDTO> getConversations(long userId) {
        User user = userDAO.getUserWithFriends(userId);
        List<ConversationDTO> conversations = new ArrayList<>();
        for (Friendship friend : user.getFriends()) {
            List<Message> messages = messageDAO.findConversation(friend.getId());
            if (messages.size() > 1) messages.sort(Comparator.comparing(Message::getCreatedAt));
            MessageDTO[] messagesDTO = new MessageDTO[messages.size()];
            for (int i = 0; i < messages.size(); i++) {
                messagesDTO[i] = messages.get(i).toMessageDTO();
            }
            User friendUser = Objects.equals(friend.getUser1().getId(), user.getId()) ? friend.getUser2() : friend.getUser1();
            LocalDateTime lastMessageDate = messages.isEmpty() ? friend.getDate() : messages.get(messages.size() - 1).getCreatedAt();
            conversations.add(new ConversationDTO(friend.getId(), messagesDTO, friendUser.toPersonDTO(), lastMessageDate.toString()));
        }
        if (conversations.size() > 1) conversations.sort(Comparator.comparing(ConversationDTO::lastMessageDate));
        return conversations;
    }

    public Set<FriendRequest> getSentFriendRequests(long userId) {
        return userDAO.getUserWithSentRequests(userId).getSentFriendRequests();
    }

}

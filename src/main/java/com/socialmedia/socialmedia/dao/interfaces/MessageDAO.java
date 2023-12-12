package com.socialmedia.socialmedia.dao.interfaces;

import com.socialmedia.socialmedia.entities.Message;

import java.util.List;
import java.util.Set;

public interface MessageDAO {
    void save(Message message);
    void delete(long id);
    Message findById(long id);
    List<Message> findConversation(long friendshipId);
    Set<Message> findMessagesForUser(long userId);
}

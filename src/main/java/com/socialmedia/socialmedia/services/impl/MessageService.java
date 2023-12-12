package com.socialmedia.socialmedia.services.impl;

import com.socialmedia.socialmedia.dao.impl.MessageDAOImpl;
import com.socialmedia.socialmedia.entities.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final MessageDAOImpl messageDAO;

    public MessageService(MessageDAOImpl messageDAO) {
        this.messageDAO = messageDAO;
    }

    public void save(Message message) {
        messageDAO.save(message);
    }

    public void delete(long id) {
        messageDAO.delete(id);
    }
}

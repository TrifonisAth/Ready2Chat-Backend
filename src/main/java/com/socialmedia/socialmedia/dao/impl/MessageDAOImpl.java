package com.socialmedia.socialmedia.dao.impl;

import com.socialmedia.socialmedia.dao.interfaces.MessageDAO;
import com.socialmedia.socialmedia.entities.Message;
import com.socialmedia.socialmedia.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
public class MessageDAOImpl implements MessageDAO {
    private final EntityManager entityManager;
    private final UserDAOImpl userDAO;

    public MessageDAOImpl(EntityManager entityManager, UserDAOImpl userDAO) {
        this.entityManager = entityManager;
        this.userDAO = userDAO;
    }

    @Override
    @Transactional
    public void save(Message message) {
        if (message == null ||
                message.getSender() == null ||
                message.getRecipient() == null |
                        Objects.equals(message.getSender().getId(), message.getRecipient().getId())
        ) {
            return;
        }
        entityManager.persist(message);
    }

    @Override
    @Transactional
    public void delete(long id) {
        Message msg = entityManager.find(Message.class, id);
        if (msg == null) {
            return;
        }
        User sender = userDAO.getUserWithSentMessages(msg.getSender().getId());
        User receiver = userDAO.getUserWithReceivedMessages(msg.getRecipient().getId());
        sender.getSentMessages().remove(msg);
        receiver.getReceivedMessages().remove(msg);
        userDAO.update(sender);
        userDAO.update(receiver);
    }

    @Override
    public Message findById(long id) {
        return entityManager.find(Message.class, id);
    }

    @Override
    public Set<Message> findMessagesForUser(long id) {
        TypedQuery<Message> query = entityManager.createQuery(
                "SELECT m FROM Message m WHERE m.recipient.id = :id OR m.sender.id = :id",
                Message.class
        );
        query.setParameter("id", id);
        return Set.copyOf(query.getResultList());
    }

    @Override
    public List<Message> findConversation(long friendshipId) {
        TypedQuery<Message> query = entityManager.createQuery(
                "SELECT m FROM Message m WHERE m.conversationId = :id",
                Message.class
        );
        query.setParameter("id", friendshipId);
        return query.getResultList();
    }
}

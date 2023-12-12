package com.socialmedia.socialmedia.dao.impl;

import com.socialmedia.socialmedia.dao.interfaces.FriendRequestDAO;
import com.socialmedia.socialmedia.entities.FriendRequest;
import com.socialmedia.socialmedia.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class FriendRequestDAOImpl implements FriendRequestDAO {
    private final EntityManager entityManager;
    private final UserDAOImpl userDAO;

    public FriendRequestDAOImpl(EntityManager entityManager, UserDAOImpl userDAO) {
        this.entityManager = entityManager;
        this.userDAO = userDAO;
    }

    @Override
    @Transactional
    public void save(FriendRequest friendRequest) {
        if (friendRequest == null ||
                friendRequest.getSender() == null ||
                friendRequest.getReceiver() == null |
                        Objects.equals(friendRequest.getSender().getId(), friendRequest.getReceiver().getId())
        ) {
            return;
        }
        entityManager.persist(friendRequest);
    }

    @Override
    @Transactional
    public void delete(long id) {
        FriendRequest req = entityManager.find(FriendRequest.class, id);
        if (req == null) {
            return;
        }
        User sender = userDAO.getUserWithSentRequests(req.getSender().getId());
        User receiver = userDAO.getUserWithReceivedRequests(req.getReceiver().getId());
        sender.getSentFriendRequests().remove(req);
        receiver.getReceivedFriendRequests().remove(req);
        userDAO.update(sender);
        userDAO.update(receiver);
    }

    @Override
    public FriendRequest findById(long id) {
        return entityManager.find(FriendRequest.class, id);
    }


}

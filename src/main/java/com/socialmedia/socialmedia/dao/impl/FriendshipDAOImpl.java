package com.socialmedia.socialmedia.dao.impl;

import com.socialmedia.socialmedia.dao.interfaces.FriendshipDAO;
import com.socialmedia.socialmedia.entities.Friendship;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class FriendshipDAOImpl implements FriendshipDAO {
    private final EntityManager entityManager;

    public FriendshipDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(Friendship friendship) {
        if (friendship == null ||
                friendship.getUser1() == null ||
                friendship.getUser2() == null ||
                Objects.equals(friendship.getUser1().getId(), friendship.getUser2().getId())) {
            return;
        }

        entityManager.persist(friendship);
    }

    @Override
    @Transactional
    public void delete(long id) {
        Friendship friendship = entityManager.find(Friendship.class, id);
        if (friendship == null) {
            return;
        }
        entityManager.remove(friendship);
    }

    @Override
    public Friendship findById(long id) {
        return entityManager.find(Friendship.class, id);
    }

}

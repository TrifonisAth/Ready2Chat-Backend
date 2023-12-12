package com.socialmedia.socialmedia.dao.impl;

import com.socialmedia.socialmedia.dao.interfaces.UserDAO;
import com.socialmedia.socialmedia.entities.FriendRequest;
import com.socialmedia.socialmedia.entities.Friendship;
import com.socialmedia.socialmedia.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO {
    private final EntityManager entityManager;
    private final FriendshipDAOImpl friendshipDAO;

    @Autowired
    public UserDAOImpl(EntityManager entityManager,
                       FriendshipDAOImpl friendshipDAO) {
        this.entityManager = entityManager;
        this.friendshipDAO = friendshipDAO;
    }

    @Override
    public User findUserById(long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findUserByDisplayName(String displayName) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.displayName = :displayName", User.class);
        query.setParameter("displayName", displayName);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findUserByIdWithRoles(long id) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void save(User user) {
        if (user == null) return;
        user.setCreatedAt(LocalDateTime.now());
        entityManager.persist(user);
    }

    @Override
    @Transactional
    public void delete(long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) entityManager.remove(user);
    }

    @Override
    @Transactional
    public void update(long id) {
        User user = entityManager.find(User.class, id);
        if (user == null) return;
        user.setUpdatedAt(LocalDateTime.now());
        entityManager.merge(user);
    }

    /**
     * Returns a user with all of their friends.
     * @param id id of the user
     * @return user with all of their friends
     */
    @Override
    public User getUserWithFriends(long id){
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.friendshipsAsUser1 " +
                        "LEFT JOIN FETCH u.friendshipsAsUser2 " +
                        "WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns a user with all of their sent friend requests.
     * @param id id of the user
     * @return user with all of their sent friend requests
     */
    @Override
    public User getUserWithSentRequests(long id){
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.sentFriendRequests " +
                        "WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns a user with all of their received
     *  and sent friend requests.
     * @param id id of the user
     * @return user with all of their received and sent friend requests
     */
    @Override
    public User getUserWithAllRequests(long id){
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.sentFriendRequests " +
                        "LEFT JOIN FETCH u.receivedFriendRequests " +
                        "WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User getUserWithFriendsAndRequests(long id){
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.friendshipsAsUser1 " +
                        "LEFT JOIN FETCH u.friendshipsAsUser2 " +
                        "LEFT JOIN FETCH u.sentFriendRequests " +
                        "LEFT JOIN FETCH u.receivedFriendRequests " +
                        "WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns a user with all of their received friend requests.
     * @param id id of the user
     * @return user with all of their received friend requests
     */
    @Override
    public User getUserWithReceivedRequests(long id){
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.receivedFriendRequests WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Removes the friendship with the given id.
     * @param friendshipId id of the friendship to remove
     */
    @Override
    @Transactional
    public void removeFriendship(long friendshipId){
        Friendship friendship = friendshipDAO.findById(friendshipId);
        if (friendship == null) return;
        User user1 = friendship.getUser1();
        User user2 = friendship.getUser2();
        user1.getFriendshipsAsUser1().remove(friendship);
        user2.getFriendshipsAsUser2().remove(friendship);
        update(user1);
        update(user2);
    }

    /**
     * Removes the friend request with the given id.
     * @param friendRequestId id of the friend request to remove
     */
    @Override
    @Transactional
    public void removeFriendRequest(long friendRequestId) {
        FriendRequest req = entityManager.find(FriendRequest.class, friendRequestId);
        if (req == null) {
            return;
        }
        User sender = getUserWithSentRequests(req.getSender().getId());
        User receiver = getUserWithReceivedRequests(req.getReceiver().getId());
        sender.getSentFriendRequests().remove(req);
        receiver.getReceivedFriendRequests().remove(req);
        update(sender);
        update(receiver);
    }


    @Override
    @Transactional
    public void update(User user) {
        if (user == null) return;
        user.setUpdatedAt(LocalDateTime.now());
        entityManager.merge(user);
    }

    @Override
    @Transactional
    public void login(User user) {
        if (user != null) {
            user.setOnline(true);
            user.setUpdatedAt(LocalDateTime.now());
            entityManager.merge(user);
        }
    }

    /**
     * Removes the role from the user with the given id.
     * @param userId id of the user
     * @param role role to remove
     */
    @Override
    @Transactional
    public void removeRole(long userId, String role) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id", User.class);
        query.setParameter("id", userId);
        User user = query.getSingleResult();
        user.removeRole(role);
        update(user);
        entityManager.merge(user);
    }

    /**
     * Adds the role to the user with the given id.
     * @param userId id of the user
     * @param role role to add
     */
    @Override
    @Transactional
    public void addRole(long userId, String role) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id", User.class);
        query.setParameter("id", userId);
            User user = query.getSingleResult();
            user.addRole(role);
            update(user);
            entityManager.merge(user);
    }

    @Override
    public List<User> findAll() {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public void logout(User user) {
        if (user != null) {
            user.setOnline(false);
            user.setUpdatedAt(LocalDateTime.now());
            entityManager.merge(user);
        }
    }

    @Override
    public User getUserWithMessages(Long id) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.sentMessages " +
                        "LEFT JOIN FETCH u.receivedMessages " +
                        "WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User getUserWithSentMessages(Long id) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.sentMessages " +
                        "WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User getUserWithReceivedMessages(Long id) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.receivedMessages " +
                        "WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<User> findAllWithRoles() {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.roles", User.class);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User findUserWithRoles(long id) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}

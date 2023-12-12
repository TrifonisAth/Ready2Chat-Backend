package com.socialmedia.socialmedia.services.impl;

import com.socialmedia.socialmedia.dao.impl.FriendRequestDAOImpl;
import com.socialmedia.socialmedia.dao.impl.FriendshipDAOImpl;
import com.socialmedia.socialmedia.dao.impl.UserDAOImpl;
import com.socialmedia.socialmedia.entities.FriendRequest;
import com.socialmedia.socialmedia.entities.Friendship;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.services.interfaces.FriendService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class FriendServiceImpl implements FriendService {
    private final FriendshipDAOImpl friendDAO;
    private final FriendRequestDAOImpl requestDAO;
    private final UserDAOImpl userDAO;

    public FriendServiceImpl(FriendshipDAOImpl friendDAO, FriendRequestDAOImpl requestDAO, UserDAOImpl userDAO) {
        this.friendDAO = friendDAO;
        this.requestDAO = requestDAO;
        this.userDAO = userDAO;
    }

    /**
     * Creates a friend request between the two users.
     * @param from: id of the first user (sender)
     * @param to: id of the second user (recipient)
     */
    public void createRequest(long from, long to) {
        User sender = userDAO.findUserById(from);
        User recipient = userDAO.findUserById(to);
        FriendRequest req = new FriendRequest(sender, recipient);
        requestDAO.save(req);
    }

    /**
     *  Accepts a friend request with the given id.
     *  Creates a friendship between the sender and receiver of the request.
     *  Deletes the request.
     * @param id: id of the request to accept
     */
    public void acceptRequest(long id){
        FriendRequest req = requestDAO.findById(id);
        User sender = req.getSender();
        User recipient = req.getReceiver();
        Friendship friendship = new Friendship(sender, recipient);
        friendDAO.save(friendship);
        requestDAO.delete(req.getId());
    }

    /**
     *  Deletes the request with the given id.
     * @param id: id of the request to delete
     */
    public void removeRequest(long id){
        requestDAO.delete(id);
    }

    /**
     *  Returns a set with every friend of the user with the given id.
     *  Includes both sent and received requests.
     * @param userId: id of the user
     * @return set of friends
     */
    public Set<FriendRequest> getUserFriendRequests(long userId){
        User user = userDAO.getUserWithAllRequests(userId);
        return user.getAllFriendRequests();
    }

    /**
     *  Returns a set with every friend of the user with the given id.
     * @param userId: id of the user
     * @return set of friends
     */
    public Set<User> getUserFriends(long userId){
        User user = userDAO.getUserWithFriends(userId);
        // Create a set with the friends of the user, excluding the user itself.
        Set<User> friends = new HashSet<>();
        for (Friendship friend : user.getFriends()) {
            if (Objects.equals(friend.getUser1().getId(), user.getId())) {
                friends.add(friend.getUser2());
            } else {
                friends.add(friend.getUser1());
            }
        }
        return friends;
    }

    /**
     *  Deletes the friendship with the given id.
     * @param friendshipId: id of the friendship to delete
     */
    public void removeFriend(long friendshipId){
        friendDAO.delete(friendshipId);
    }

    @Override
    public FriendRequest findRequest(long id, Long friendId) {
        User user = userDAO.getUserWithSentRequests(id);
        return user.getSentFriendRequests().stream()
                .filter(r -> Objects.equals(r.getReceiver().getId(), friendId))
                .findFirst()
                .orElse(null);
    }

//    private boolean existingFriends(User user1, User user2) {
//        User smallerSet = user1.getFriends().size() < user2.getFriends().size() ? user1 : user2;
//        User largerSet = smallerSet == user1 ? user2 : user1;
//        boolean res = false;
//        for (Friendship friend : smallerSet.getFriends()) {
//            if (Objects.equals(friend.getUser1().getId(), largerSet.getId()) ||
//                    Objects.equals(friend.getUser2().getId(), largerSet.getId())) {
//                res = true;
//                break;
//            }
//        }
//        return res;
//    }

    private boolean existingRequest(User user1, User user2) {
        User smallerSet = user1.getSentFriendRequests().size()
                + user1.getReceivedFriendRequests().size()
                < user2.getSentFriendRequests().size()
                + user2.getReceivedFriendRequests().size() ? user1 : user2;
        User largerSet = smallerSet == user1 ? user2 : user1;
        boolean res = false;
        for (FriendRequest req : smallerSet.getSentFriendRequests()) {
            if (Objects.equals(req.getReceiver().getId(), largerSet.getId())) {
                res = true;
                break;
            }
        }
        for (FriendRequest req : user1.getReceivedFriendRequests()) {
            if (Objects.equals(req.getSender().getId(), largerSet.getId())) {
                res = true;
                break;
            }
        }
        return res;
    }
}

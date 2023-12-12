package com.socialmedia.socialmedia.services.interfaces;

import com.socialmedia.socialmedia.entities.FriendRequest;

public interface FriendService {
    void createRequest(long from, long to);
    void acceptRequest(long reqId);
    void removeRequest(long reqId);
    void removeFriend(long friendshipId);

    FriendRequest findRequest(long id, Long friendId);
}

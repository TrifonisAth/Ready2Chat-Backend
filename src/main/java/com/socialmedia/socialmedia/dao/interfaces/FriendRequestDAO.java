package com.socialmedia.socialmedia.dao.interfaces;

import com.socialmedia.socialmedia.entities.FriendRequest;

public interface FriendRequestDAO {
    void save(FriendRequest req);
    void delete(long id);
    FriendRequest findById(long id);
}

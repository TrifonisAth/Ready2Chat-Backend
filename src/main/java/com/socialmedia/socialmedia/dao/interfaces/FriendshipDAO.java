package com.socialmedia.socialmedia.dao.interfaces;

import com.socialmedia.socialmedia.entities.Friendship;

public interface FriendshipDAO {
    void save(Friendship friendship);
    void delete(long id);
    Friendship findById(long id);
}

package com.socialmedia.socialmedia.dao.interfaces;

import com.socialmedia.socialmedia.entities.VerificationToken;

public interface VerificationTokenDAO {
    void save(VerificationToken token);
    void delete(long id);
    void update(VerificationToken token);
    VerificationToken findTokenById(long id);
    VerificationToken findByUserId(long id);
}

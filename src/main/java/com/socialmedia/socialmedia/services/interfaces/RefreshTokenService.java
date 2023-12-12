package com.socialmedia.socialmedia.services.interfaces;

import com.socialmedia.socialmedia.entities.RefreshToken;

import java.util.Map;

public interface RefreshTokenService {
    void delete(long id);

    void deleteAll();

    void save(RefreshToken token);

    Map<Long, RefreshToken> findAll();

    RefreshToken find(long id);
}

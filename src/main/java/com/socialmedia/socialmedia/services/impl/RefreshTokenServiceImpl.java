package com.socialmedia.socialmedia.services.impl;

import com.socialmedia.socialmedia.dao.interfaces.RefreshTokenDAO;
import com.socialmedia.socialmedia.entities.RefreshToken;
import com.socialmedia.socialmedia.services.interfaces.RefreshTokenService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenDAO tokenDAO;

    public RefreshTokenServiceImpl(RefreshTokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    @Override
    public void delete(long id) {
        tokenDAO.deleteById(id);
    }

    @Override
    public void deleteAll() {
        tokenDAO.deleteAll();
    }

    @Override
    public Map<Long, RefreshToken> findAll() {
        return tokenDAO.findAll().stream().collect(Collectors.toMap(RefreshToken::getTokenId, token -> token));
    }

    @Override
    public void save(RefreshToken token) {
        tokenDAO.save(token);
    }

    @Override
    public RefreshToken find(long id) {
        return tokenDAO.findById(id).orElse(null);
    }


}

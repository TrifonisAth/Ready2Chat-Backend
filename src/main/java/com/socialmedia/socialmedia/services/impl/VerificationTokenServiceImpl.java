package com.socialmedia.socialmedia.services.impl;

import com.socialmedia.socialmedia.dao.impl.VerificationTokenDAOImpl;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.entities.VerificationToken;
import com.socialmedia.socialmedia.services.interfaces.VerificationTokenService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
    VerificationTokenDAOImpl tokenDAO;

    public VerificationTokenServiceImpl(VerificationTokenDAOImpl tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    public VerificationToken generateToken(User user) {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();
        // Generate a 6-digit random number.
        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10);
            stringBuilder.append(digit);
        }
        String str = stringBuilder.toString();
        VerificationToken token = new VerificationToken(str);
        token.setUser(user);
        token.setId(user.getId());
        tokenDAO.save(token);
        return token;
    }


    public VerificationToken findTokenById(long id) {
        return tokenDAO.findTokenById(id);
    }

    public void delete(long tokenId) {
        tokenDAO.delete(tokenId);
    }

}

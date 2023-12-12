package com.socialmedia.socialmedia.dao.interfaces;

import com.socialmedia.socialmedia.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenDAO extends JpaRepository<RefreshToken, Long> {

}

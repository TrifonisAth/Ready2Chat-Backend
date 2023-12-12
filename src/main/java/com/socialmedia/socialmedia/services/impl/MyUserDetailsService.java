package com.socialmedia.socialmedia.services.impl;

import com.socialmedia.socialmedia.dao.impl.UserDAOImpl;
import com.socialmedia.socialmedia.model.SecurityUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserDAOImpl userDao;

    public MyUserDetailsService(UserDAOImpl userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userDao.findUserByIdWithRoles(Long.parseLong(userId))
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

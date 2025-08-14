package com.blog.web.security;

import com.blog.api.model.User;
import com.blog.core.service.UserService;
import com.blog.persistence.entity.UserEntity;
import com.blog.persistence.repository.JpaUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JpaUserRepository jpaUserRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOpt = jpaUserRepository.findByUsernameWithRolesAndPermissions(username);
        if (userEntityOpt.isEmpty()) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        UserEntity userEntity = userEntityOpt.get();
        return UserPrincipal.create(userEntity);
    }
    
    @Transactional
    public UserDetails loadUserById(Long id) {
        Optional<UserEntity> userEntityOpt = jpaUserRepository.findById(id);
        if (userEntityOpt.isEmpty()) {
            throw new UsernameNotFoundException("用户不存在: " + id);
        }
        
        UserEntity userEntity = userEntityOpt.get();
        return UserPrincipal.create(userEntity);
    }
}
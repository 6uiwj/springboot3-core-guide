package com.springboot.jpa.service;

import com.springboot.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        LOGGER.info("[loadUserByUsername] loadUserByUsername 수행. username : {}", username);
        //db에서 유저 조회 후 반환
        return userRepository.getByUid(username); //userDetails의 구현체로 만든 Usr 객체 반환
    }
}

package org.example.fitvisionback.utils;

import org.example.fitvisionback.exceptions.UserConectedNotExists;
import org.example.fitvisionback.user.entity.User;
import org.example.fitvisionback.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetUserConected {

    @Autowired
    UserRepository userRepository;

    public User getUserConected() {
        User userConected = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.userRepository.findById(userConected.getId()).orElseThrow(UserConectedNotExists::new);
    }
}

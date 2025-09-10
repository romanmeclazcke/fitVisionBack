package org.example.fitvisionback.user.service;

import org.example.fitvisionback.user.dto.CreateUserDto;

public interface UserService {
    void registerUser(CreateUserDto createUserDto);
}

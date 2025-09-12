package org.example.fitvisionback.user.service;

import org.example.fitvisionback.user.dto.CreateUserDto;
import org.example.fitvisionback.user.dto.UserResponseDto;

public interface UserService {
    void registerUser(CreateUserDto createUserDto);

    UserResponseDto getUserByEmail(String userEmail);
}

package org.example.fitvisionback.user.controller;

import org.example.fitvisionback.user.dto.CreateUserDto;
import org.example.fitvisionback.user.dto.UserResponseDto;
import org.example.fitvisionback.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @RequestBody CreateUserDto createUserDto
            ) {
        this.userService.registerUser(createUserDto);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/email/{userEmail}")
    public ResponseEntity<UserResponseDto> getUserByEmail(
            @PathVariable String userEmail) {
       return ResponseEntity.ok(this.userService.getUserByEmail(userEmail));
    }
}

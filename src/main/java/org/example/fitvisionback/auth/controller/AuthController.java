package org.example.fitvisionback.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.fitvisionback.auth.dto.ChangePasswordDto;
import org.example.fitvisionback.auth.service.AuthService;
import org.example.fitvisionback.auth.dto.AuthResponse;
import org.example.fitvisionback.auth.dto.LoginDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto request) {
        authService.changePassword(request);
        return ResponseEntity.ok("Contraseña cambiada exitosamente");
    }

}

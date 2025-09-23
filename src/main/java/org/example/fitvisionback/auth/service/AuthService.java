package org.example.fitvisionback.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.fitvisionback.auth.dto.AuthResponse;
import org.example.fitvisionback.auth.dto.ChangePasswordDto;
import org.example.fitvisionback.auth.dto.LoginDto;
import org.example.fitvisionback.exceptions.InvalidCredentialsException;
import org.example.fitvisionback.user.entity.User;
import org.example.fitvisionback.user.repository.UserRepository;
import org.example.fitvisionback.utils.GetUserConected;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final GetUserConected userConected;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Credenciales inválidas: email o contraseña incorrectos.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Usuario no encontrado."));

        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void changePassword(ChangePasswordDto request) {
        User user = userConected.getUserConected();

        boolean matchPassword= this.passwordEncoder.matches(request.getOldPassword(),user.getPassword());

        if (!matchPassword){
            throw new InvalidCredentialsException("Contraseña incorrecta.");
        }

        user.setPassword(this.passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}

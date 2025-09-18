package org.example.fitvisionback.user.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.fitvisionback.credits.model.Credits;
import org.example.fitvisionback.credits.service.CreditsService;
import org.example.fitvisionback.user.dto.CreateUserDto;
import org.example.fitvisionback.user.dto.UserResponseDto;
import org.example.fitvisionback.user.entity.User;
import org.example.fitvisionback.user.mapper.UserMapper;
import org.example.fitvisionback.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private UserMapper userMapper;
    private CreditsService credtisService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, CreditsService credtisService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.credtisService = credtisService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }



    @Override
    @Transactional
    public void registerUser(CreateUserDto createUserDto) {
        if (this.userRepository.findByEmail(createUserDto.getEmail()).isPresent()) {
            throw new EntityExistsException("User with email " + createUserDto.getEmail() + " already exists");
        }


        User user = this.userMapper.toEntity(createUserDto);
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));

        // Hago flush para que al guardar los creditos no haya inconsistencia de datos
        this.userRepository.saveAndFlush(user);

        //Creo los creditos iniciales para el usuario registrado
        Credits credits = Credits.builder()
                .user(user)
                .credits(0)
                .build();

        this.credtisService.save(credits);
    }

    @Override
    public UserResponseDto getUserByEmail(String userEmail) {
        User user = this.userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + userEmail + " not found"));

        return this.userMapper.toDto(user);
    }

    public int sum(int a, int b) {
        return a + b;
    }
}

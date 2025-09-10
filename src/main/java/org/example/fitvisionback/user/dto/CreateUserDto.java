package org.example.fitvisionback.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.fitvisionback.user.entity.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    private String email;
    private String name;
    private String password;
    private Role role;
}

package org.example.fitvisionback.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Data
@NoArgsConstructor
@A
public class ChangePasswordDto {
    private String oldPassword;
    private String newPassword;
}

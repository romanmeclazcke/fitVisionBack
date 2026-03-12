package org.example.fitvisionback.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordDto {
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
}

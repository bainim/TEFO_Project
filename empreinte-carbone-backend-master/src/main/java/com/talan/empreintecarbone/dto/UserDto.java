package com.talan.empreintecarbone.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class UserDto {
    private String username;
    private String password;
    private String tokenResetPassword;
}

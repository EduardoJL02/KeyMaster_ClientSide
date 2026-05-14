package com.iesjc.keymasterclient.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {
    // Hemos eliminado el @NotBlank
    private String username;
    private String password;
}
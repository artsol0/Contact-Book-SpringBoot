package com.artsolo.phonecontacts.auth;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {
    @NotNull(message = "Username is mandatory")
    String username;
    @NotNull(message = "Password is mandatory")
    String password;
}

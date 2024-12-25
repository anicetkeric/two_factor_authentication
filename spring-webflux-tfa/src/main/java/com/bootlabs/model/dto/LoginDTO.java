package com.bootlabs.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @NotNull
        @Email
        @Size(min = 5, max = 254)
        String email,

        @NotNull
        @Size(min = 8, max = 26)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
                message = "The password must contain at least 8 characters, a letter, a number and a symbol.")
        String password
) {
}

package com.bootlabs.model.dto;

import com.bootlabs.model.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String id;

    @Email
    @NotNull
    @Size(min = 1, max = 50)
    private String username;

    @Size(max = 100)
    private String fullName;

    private String bio;

    @JsonIgnore
    private Set<RoleEnum> roles = new HashSet<>();

    private boolean enabled = false;

    private boolean mfaEnabled = false;

    private boolean accountNonExpired;

    private boolean credentialsNonExpired;

    private boolean accountNonLocked;
}
package com.bootlabs.document;

import com.bootlabs.model.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A user.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "app_user")
public class User implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Email
    @NotNull
    @Size(min = 1, max = 50)
    @Indexed(unique = true)
    private String username;

    @Getter(onMethod = @__(@JsonIgnore))
    @NotNull
    @Size(min = 4, max = 60)
    private String password;

    @Size(max = 100)
    private String fullName;

    private String bio;

    @JsonIgnore
    private Set<RoleEnum> roles = new HashSet<>();

    private boolean enabled = false;

    private UserTfa mfa;

    private boolean accountNonExpired;

    private boolean credentialsNonExpired;

    private boolean accountNonLocked;


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountNonExpired;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountNonLocked;
    }

    /*
     * Get roles and add them as a Set of GrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.name())).collect(Collectors.toSet());
    }

}

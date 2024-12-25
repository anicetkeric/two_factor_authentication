package com.bootlabs.mapper;

import com.bootlabs.document.User;
import com.bootlabs.model.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link User} and its DTO {@link UserDTO}.
 */
@Mapper
public interface UserMapper {


    User toEntity(UserDTO dto);

    @Mapping(source = "mfa.status", target = "mfaEnabled")
    UserDTO toDto(User entity);

}

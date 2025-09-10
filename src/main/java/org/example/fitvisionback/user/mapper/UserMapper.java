package org.example.fitvisionback.user.mapper;

import org.example.fitvisionback.user.dto.CreateUserDto;
import org.example.fitvisionback.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserDto user);
}

package org.example.fitvisionback.user.mapper;

import org.example.fitvisionback.credits.model.Credits;
import org.example.fitvisionback.user.dto.CreateUserDto;
import org.example.fitvisionback.user.dto.UserResponseDto;
import org.example.fitvisionback.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserDto user);

    @Mapping(target = "credits", source = "credits", qualifiedByName = "mapCredits")
    UserResponseDto toDto(User user);


    @Named("mapCredits")
    default Integer mapCredits(Credits credits) {
        return credits != null ? credits.getCredits() : 0;
    }
}

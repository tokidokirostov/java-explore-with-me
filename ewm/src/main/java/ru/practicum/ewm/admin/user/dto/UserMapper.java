package ru.practicum.ewm.admin.user.dto;

import ru.practicum.ewm.admin.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(UserCreateDto userCreateDto) {
        return new UserDto(
                userCreateDto.getEmail(),
                userCreateDto.getName(),
                userCreateDto.getId());
    }

    public static User toUser(UserDto userDto) {
        return new User(userDto.getId(),
                userDto.getEmail(),
                userDto.getName());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getEmail(),
                user.getName(),
                user.getId());
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}

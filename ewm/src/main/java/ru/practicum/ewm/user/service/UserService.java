package ru.practicum.ewm.user.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.RequestError;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;


    //Добавление нового пользователя
    public UserDto addUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    //Получение информации о пользователе
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id))));
    }

    //Получение информации о пользователях
    public List<UserDto> getAllUsers(List<String> ids, String from, String size) {
        try {
            List<User> userList = new ArrayList<>();
            List<Long> idsUrl = new ArrayList<>();
            Integer pageFrom = Integer.parseInt(from);
            Integer sizeUrl = Integer.parseInt(size);
            if (!(ids == null)) {
                for (String id : ids) {
                    idsUrl.add(Long.parseLong(id));
                }
                for (Long idk : idsUrl) {
                    userList.add(userRepository.findById(idk)
                            .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", idk))));
                }
                return userList.stream()
                        .map(user -> UserMapper.toUserDto(user))
                        .collect(Collectors.toList());
            } else {
                Sort sort = Sort.unsorted();
                Pageable page = PageRequest.of(pageFrom, sizeUrl, sort);
                Page<User> userPage;
                int allPage;
                userPage = userRepository.findAll(page);
                allPage = userPage.getTotalPages();
                if (pageFrom >= allPage) {
                    if (allPage > 0) {
                        --allPage;
                    }
                    userPage = userRepository.findAll(PageRequest.of(allPage, sizeUrl, sort));
                }
                return userPage.stream()
                        .map(user -> UserMapper.toUserDto(user))
                        .collect(Collectors.toList());
            }
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Удаление пользователя
    @Transactional
    public void deleteUser(String stringId) {
        try {
            Long id = Long.parseLong(stringId);
            if (userRepository.findById(id).isEmpty()) {
                throw new UserNotFoundException(String.format("Event with id=%d was not found.", id));
            } else {
                userRepository.deleteById(id);
            }
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }
}

package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    UserDto firstUserDto;
    UserDto secondUserDto;
    UserDto thirdUserDto;
    UserDto firstUserDtoSaved;
    UserDto secondUserDtoSaved;
    UserDto thirdUserDtoSaved;
    User firstUser;
    User secondUser;
    User thirdUser;
    User firstUserSaved;
    User secondUserSaved;
    User thirdUserSaved;

    @BeforeEach
    void setUp() {
        firstUserDto = UserDto.builder()
                .name("user1")
                .email("email1@gmail.com")
                .build();

        firstUserDtoSaved = UserDto.builder()
                .id(1L)
                .name("user1")
                .email("email1@gmail.com")
                .build();

        firstUser = User.builder()
                .name(firstUserDto.getName())
                .email(firstUserDto.getEmail())
                .build();

        firstUserSaved = User.builder()
                .id(firstUserDtoSaved.getId())
                .name(firstUserDtoSaved.getName())
                .email(firstUserDtoSaved.getEmail())
                .build();

        secondUserDto = UserDto.builder()
                .name("user2")
                .email("email2@gmail.com")
                .build();
        thirdUserDto = UserDto.builder()
                .name("user3")
                .email("email3@gmail.com")
                .build();

        secondUserDtoSaved = UserDto.builder()
                .id(2L)
                .name("user2")
                .email("email2@gmail.com")
                .build();
        thirdUserDtoSaved = UserDto.builder()
                .id(3L)
                .name("user3")
                .email("email3@gmail.com")
                .build();

        secondUser = User.builder()
                .name(secondUserDtoSaved.getName())
                .email(secondUserDtoSaved.getEmail())
                .build();
        thirdUser = User.builder()
                .name(thirdUserDtoSaved.getName())
                .email(thirdUserDtoSaved.getEmail())
                .build();

        secondUserSaved = User.builder()
                .id(secondUserDtoSaved.getId())
                .name(secondUserDtoSaved.getName())
                .email(secondUserDtoSaved.getEmail())
                .build();
        thirdUserSaved = User.builder()
                .id(thirdUserDtoSaved.getId())
                .name(thirdUserDtoSaved.getName())
                .email(thirdUserDtoSaved.getEmail())
                .build();
    }

    @Test
    void createUser() {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(firstUserSaved);

        UserDto actualDto = userService.create(firstUserDto);

        Assertions.assertNotNull(actualDto);
        Assertions.assertEquals(firstUserDtoSaved.getId(), actualDto.getId());
        Assertions.assertEquals(firstUserDto.getName(), actualDto.getName());
        Assertions.assertEquals(firstUserDto.getEmail(), actualDto.getEmail());
    }

    @Test
    void getAllUsers() {
        List<UserDto> expectedList;
        List<UserDto> actualList;

        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(firstUserSaved, secondUserSaved, thirdUserSaved));

        expectedList = List.of(firstUserDtoSaved, secondUserDtoSaved, thirdUserDtoSaved);
        actualList = userService.getAll();

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void getUser() {
        UserDto expectedDto;
        UserDto actualDto;

        Mockito
                .when(userRepository.findById(firstUserSaved.getId()))
                .thenReturn(Optional.ofNullable(firstUserSaved));

        expectedDto = firstUserDtoSaved;
        actualDto = userService.getById(firstUserDtoSaved.getId());

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void updateUser() {

        Mockito.when(userRepository.findById(firstUserDtoSaved.getId())).thenReturn(Optional.of(firstUserSaved));

        secondUserDtoSaved.setId(firstUserDtoSaved.getId());

        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).then(AdditionalAnswers.returnsFirstArg());

        UserDto actualDto = userService.update(firstUserDtoSaved.getId(), secondUserDtoSaved);

        Assertions.assertNotNull(actualDto);
        Assertions.assertEquals(secondUserDtoSaved.getId(), actualDto.getId());
        Assertions.assertEquals(secondUserDtoSaved.getName(), actualDto.getName());
        Assertions.assertEquals(secondUserDtoSaved.getEmail(), actualDto.getEmail());
    }

    @Test
    void deleteUser() {
        userService.delete(firstUserDtoSaved.getId());

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(firstUserDtoSaved.getId());
    }
}

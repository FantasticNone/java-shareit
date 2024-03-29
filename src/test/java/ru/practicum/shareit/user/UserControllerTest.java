package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mvc;

    UserDto userDto;

    UserDto userDto2;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe@example.com")
                .build();

        userDto2 = UserDto.builder()
                        .id(2)
                        .name("User 2")
                        .email("email2@email.com")
                        .build();
    }

    @Test
    void createNewUser() throws Exception {
        Mockito.when(userService.create(userDto)).thenReturn(userDto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @SneakyThrows
    @Test
    void createUser_whenNotValidName_Expect400BadRequest() {

        List<UserDto> users = new ArrayList<>();
        users.add(userDto);
        users.add(userDto2);

        userDto.setName("   ");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(userDto);
    }

    @SneakyThrows
    @Test
    void createUser_whenNotValidEmail_Expect400BadRequest() {

        List<UserDto> users = new ArrayList<>();
        users.add(userDto);
        users.add(userDto2);

        userDto.setEmail("mail");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(userDto);
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserDto> expectedList;

        UserDto newUserResponseDto = UserDto.builder()
                .id(2L)
                .name("adam")
                .email("adamsandler@mail.com")
                .build();

        expectedList = List.of(userDto, newUserResponseDto);

        Mockito
                .when(userService.getAll())
                .thenReturn(expectedList);

        mvc.perform(get("/users"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[1].id").value(newUserResponseDto.getId()));
    }

    @Test
    void getUser() throws Exception {
        Mockito
                .when(userService.getById(userDto.getId()))
                .thenReturn(userDto);

        mvc.perform(get("/users/{userId}", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void updateUser() throws Exception {
        Mockito
                .when(userService.update(userDto.getId(), userDto))
                .thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", userDto.getId()))
                .andExpect(status().isOk());

        verify(userService, Mockito.times(1))
                .delete(userDto.getId());
    }
}
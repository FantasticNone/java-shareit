package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class})
class ErrorHandlerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    UserService userService;

    @Test
    void emailExistsHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("test")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.create(request))
                .thenThrow(new EmailIsAlreadyRegisteredException("Email is already taken"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof EmailIsAlreadyRegisteredException))
                .andExpect(result -> Assertions.assertEquals("Email is already taken", result.getResolvedException().getMessage()));
    }

    @Test
    void userNotFound() throws Exception {
        UserDto request = UserDto.builder()
                .name("test")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.create(request))
                .thenThrow(new NotFoundException("User id %d not found"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> Assertions.assertEquals("User id %d not found", result.getResolvedException().getMessage()));
    }

    @Test
    void itemNotFoundHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("test")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.create(request))
                .thenThrow(new NotFoundException("Item not found"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> Assertions.assertEquals("Item not found", result.getResolvedException().getMessage()));
    }


    @Test
    void itemBookingHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("test")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.create(request))
                .thenThrow(new ItemBookingException("Item booking exception"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ItemBookingException))
                .andExpect(result -> Assertions.assertEquals("Item booking exception", result.getResolvedException().getMessage()));
    }

    @Test
    void bookingNotFoundHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("test")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.create(request))
                .thenThrow(new NotFoundException("Booking not found"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> Assertions.assertEquals("Booking not found", result.getResolvedException().getMessage()));
    }

    @Test
    void dataHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("test")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.create(request))
                .thenThrow(new DataException("Data exception"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof DataException))
                .andExpect(result -> Assertions.assertEquals("Data exception", result.getResolvedException().getMessage()));
    }
}

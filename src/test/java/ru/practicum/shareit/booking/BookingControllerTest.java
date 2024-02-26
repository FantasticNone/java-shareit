package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.valid.PageableValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @MockBean
    PageableValidator pageableValidator;

    @Autowired
    private MockMvc mvc;

    private User owner;
    private User booker;
    private BookingRequestDto bookingRequestDto;
    private BookingDto bookingDto;
    private Item item;

    @BeforeEach
    void setUp() {
        LocalDateTime localDateTime = LocalDateTime.now();

        owner = User.builder()
                .id(1L)
                .name("user")
                .email("email")
                .build();

        booker = User.builder()
                .id(2L)
                .name("user1")
                .email("email1")
                .build();

        item = Item.builder()
                .id(1L)
                .name("drill")
                .description("for drilling")
                .owner(owner)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .id(1L)
                .itemId(1L)
                .start(localDateTime.plusDays(2))
                .end(localDateTime.plusDays(4))
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .booker(UserMapper.toUserDto(booker))
                .item(ItemMapper.toItemDto(item))
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status("WAITING")
                .build();
    }


   @Test
    void testCreateBooking() throws Exception {
        String requestBody = mapper.writeValueAsString(bookingRequestDto);

        when(bookingService.create(bookingRequestDto)).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(bookingDto.getId()))
                .andReturn();
    }

    @Test
    void acceptOrDeclineBooking() throws Exception {
        BookingDto approvedBooking = BookingDto.builder()
                .id(1L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status("APPROVED")
                .booker(UserMapper.toUserDto(booker))
                .item(ItemMapper.toItemDto(item))
                .build();

        when(bookingService.approve(owner.getId(), bookingDto.getId(), true))
                .thenReturn(approvedBooking);

        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @SneakyThrows
    @Test
    void getBooking() {

        long bookingId = 1L;
        long userId = 1L;
        when(bookingService.getBooking(bookingId, userId)).thenReturn(bookingDto);
        String result = mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(bookingService, times(1)).getBooking(bookingId, userId);
        assertThat(result, equalTo(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getAllUserBookings() throws Exception {
        List<BookingDto> expectedList;

        BookingDto newBooking = BookingDto.builder()
                .id(1L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status("APPROVED")
                .booker(UserMapper.toUserDto(booker))
                .item(ItemMapper.toItemDto(item))
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        expectedList = List.of(bookingDto, newBooking);
        when(bookingService.getAllByUser(booker.getId(), BookingState.ALL, pageable))
                .thenReturn(expectedList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(expectedList.get(1).getId()));
    }

    @Test
    void getAllOwnerBookings() throws Exception {
        List<BookingDto> expectedList;

        BookingDto newBooking = BookingDto.builder()
                .id(2L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status("APPROVED")
                .booker(UserMapper.toUserDto(booker))
                .item(ItemMapper.toItemDto(item))
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        expectedList = List.of(bookingDto, newBooking);

        when(bookingService.getAllByOwner(booker.getId(), BookingState.ALL, pageable))
                .thenReturn(expectedList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(expectedList.get(1).getId()));
    }
}

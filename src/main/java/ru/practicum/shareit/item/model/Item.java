package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.utils.Marker;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
}
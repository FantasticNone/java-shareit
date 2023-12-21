package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.utils.Marker;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(groups = {Marker.Update.class})
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    @NotBlank(groups = {Marker.Update.class, Marker.Create.class})
    @Email(groups = {Marker.Update.class, Marker.Create.class})
    private String email;
}

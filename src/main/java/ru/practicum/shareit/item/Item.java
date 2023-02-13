package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;

import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    long id;
    String name;
    String description;
    Boolean available;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User owner;
}

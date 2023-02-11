package ru.practicum.shareit.item.Comment;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    long id;

    @Column//(nullable = false)
    String text;

    @ManyToOne//(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "item_id")
    Item item; // посмотреть тут ошибку!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @ManyToOne
    @JoinColumn(name = "user_id")//, nullable = false)
    User author;

    @Column//(nullable = false)
    LocalDateTime created;


}

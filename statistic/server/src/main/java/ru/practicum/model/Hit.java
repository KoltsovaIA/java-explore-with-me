package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hit", schema = "public")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Hit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "app", length = 512)
    private String app;

    @Column(name = "uri", length = 512)
    private String uri;

    @Column(name = "ip", length = 22)
    private String ip;

    private LocalDateTime timestamp;
}
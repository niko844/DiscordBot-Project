package com.example.discordbot.commands.account.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
}

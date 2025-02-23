package com.example.discordbot.commands.account.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "img_url")
    private String imageUrl;

    @Column(name = "category")
    private String category;

    @Column(name = "source")
    private String source;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "message_id")
    private String messageId;
}

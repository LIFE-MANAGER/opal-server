package com.lifeManager.opalyouth.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_idx")
    private Long itemIdx;

    @Column(name = "name", nullable = false, length = 25)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Builder
    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }
}

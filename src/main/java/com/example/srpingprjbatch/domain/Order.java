package com.example.srpingprjbatch.domain;

import lombok.Getter;

import javax.persistence.*;


@Getter
@Table(name = "my_order")
@Entity
public class Order {


    @Id
    @Column(name = "ord_idx")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ordIdx;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "price")
    private int price;


}

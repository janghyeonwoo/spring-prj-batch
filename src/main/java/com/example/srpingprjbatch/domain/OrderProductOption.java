package com.example.srpingprjbatch.domain;

import lombok.Getter;

import javax.persistence.*;


@Getter
@Table(name = "order_product_option")
@Entity
public class OrderProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long opoIdx;



    @ManyToOne(fetch = FetchType.LAZY)
    private OrderProduct orderProduct;
}

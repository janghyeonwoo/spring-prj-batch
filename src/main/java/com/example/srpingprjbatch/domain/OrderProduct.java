package com.example.srpingprjbatch.domain;

import lombok.Getter;

import javax.persistence.*;


@Getter
@Table(name = "order_product")
@Entity
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long oprIdx;

    @Column(name = "prd_name")
    private Long prdName;

    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

}

package com.example.srpingprjbatch.domain;

import javax.persistence.*;


@Table(name = "order_history")
@Entity
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hisIdx;

    @Column(name = "his_name")
    private String hisName;

}

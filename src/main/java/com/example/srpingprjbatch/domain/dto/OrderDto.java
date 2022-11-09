package com.example.srpingprjbatch.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Setter
@Getter
public class OrderDto {
    private Long ordIdx;
    private String userName;
    private int price;
}

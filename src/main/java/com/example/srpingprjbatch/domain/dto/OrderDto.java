package com.example.srpingprjbatch.domain.dto;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long ordIdx;
    private String userName;
    private int price;
    private int status;
}

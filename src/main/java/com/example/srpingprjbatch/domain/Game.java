package com.example.srpingprjbatch.domain;

import jdk.vm.ci.meta.Local;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Entity
@ToString
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(name = "name")
    private String name;


    @Column(name = "createDate")
    private LocalDateTime createDate;
}

package com.example.srpingprjbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SrpingPrjBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrpingPrjBatchApplication.class, args);
    }

}

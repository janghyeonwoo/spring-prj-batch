package com.example.srpingprjbatch.config.task;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class NoActionWriter<T> implements ItemWriter<T> {
    @Override
    public void write(List<? extends T> items) throws Exception {

    }
}

package com.example.srpingprjbatch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CustomListWriter <T> implements ItemWriter<List<T>> {
    private final JdbcBatchItemWriter<T> itemWriter;

    @Override
    public void write(List<? extends List<T>> items) throws Exception {
        List<T> tList = new ArrayList<>();
        for(List<T> i: items){
            tList.addAll(i);
        }

        itemWriter.afterPropertiesSet();
        itemWriter.write(tList);
    }
}

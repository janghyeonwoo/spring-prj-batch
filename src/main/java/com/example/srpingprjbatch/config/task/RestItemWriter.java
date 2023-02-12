package com.example.srpingprjbatch.config.task;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RestItemWriter<T> implements ItemWriter<List<T>> {

    private final JdbcBatchItemWriter<T> jdbcBatchItemWriter;

    @Override
    public void write(List<? extends List<T>> items) throws Exception {
        List<T> itemList = new ArrayList<>();
        for(List<T> item : items){
            itemList.addAll(item);
        }
        jdbcBatchItemWriter.afterPropertiesSet();
        jdbcBatchItemWriter.write(itemList);
    }
}

package com.example.srpingprjbatch.job;

import com.example.srpingprjbatch.domain.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class ClassifierJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final String JOB_NAME = "CLASSIFIER_JOB";
    private final String STEP_NAME = "CLASSIFIER_JOB_STEP";
    private final int fetchSize = 10;
    private final DataSource dataSource;

    @Bean
    public Job createJob() throws Exception {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(createStep())
                .build();
    }

    @Bean
    public Step createStep() throws Exception {
        return stepBuilderFactory
                .get(STEP_NAME)
                .<OrderDto, OrderDto>chunk(fetchSize)
                .reader(customReader2())
                .writer(customItemWriter())
                .build();
    }


    @Bean
    public ItemReader<OrderDto> customReader() {
        String sql = "SELECT * FROM my_order";
        return new JdbcCursorItemReaderBuilder<OrderDto>()
                .fetchSize(fetchSize)
                .dataSource(dataSource)
                .beanRowMapper(OrderDto.class)
                .saveState(false)
                .sql(sql)
                .build();
    }


    @Bean
    public ItemReader<OrderDto> customReader2() throws Exception {
        return new JdbcPagingItemReaderBuilder<OrderDto>()
                .fetchSize(fetchSize)
                .pageSize(fetchSize)
                .dataSource(dataSource)
                .saveState(false)
                .rowMapper(new BeanPropertyRowMapper<>(OrderDto.class))
                .queryProvider(createQueryProvider())
                .build();
    }


    @Bean
    public PagingQueryProvider createQueryProvider() throws Exception {
        String sql = "SELECT * FROM my_order";
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("SELECT *");
        queryProvider.setFromClause("FROM my_order");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("ord_idx", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }


    @Bean
    public ClassifierCompositeItemWriter<OrderDto> compositeItemWriter() {
        return new ClassifierCompositeItemWriterBuilder<OrderDto>()
                .classifier(item -> {
                    final CompositeItemWriter<OrderDto> compositeItemWriter = new CompositeItemWriter<>();
                    List<ItemWriter<? super OrderDto>> dele = new ArrayList<>();
                    dele.add(customItemWriter());
                    dele.add(customItemUpdateWriter());
                    compositeItemWriter.setDelegates(dele);
                    return compositeItemWriter;
                }).build();
    }


    @Bean
    public JdbcBatchItemWriter<OrderDto> customItemWriter() {
        String sql = "INSERT INTO order_history(his_name) VALUES(:userName)";
        return new JdbcBatchItemWriterBuilder<OrderDto>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .assertUpdates(false)
                .build();
    }


    @Bean
    public JdbcBatchItemWriter<OrderDto> customItemUpdateWriter() {
        String sql = "UPDATE my_order SET status = 7 WHERE ord_idx = :ordIdx";
        return new JdbcBatchItemWriterBuilder<OrderDto>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .assertUpdates(false)
                .build();
    }


}


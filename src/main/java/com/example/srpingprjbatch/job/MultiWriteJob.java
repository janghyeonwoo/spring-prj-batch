package com.example.srpingprjbatch.job;

import com.example.srpingprjbatch.config.listener.CustomJobListener;
import com.example.srpingprjbatch.config.listener.CustomStepListener;
import com.example.srpingprjbatch.domain.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class MultiWriteJob {

    private final String JOB_NAME = "MULTI_WRITE_JOB";
    private final String STEP_NAME = "MULTI_WRITE_STEP";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Value("${chunckSize:10}")
    private int chunckSize;


    @Bean(name = JOB_NAME)
    public Job excuteMultiWriteJob() throws Exception {
        return jobBuilderFactory.get(JOB_NAME)
                .start(excuteMultiWriteStep())
                .incrementer(new RunIdIncrementer())
                .listener(new CustomJobListener())
                .build();
    }

    @JobScope
    @Bean(name = STEP_NAME)
    public Step excuteMultiWriteStep() throws Exception {
        return stepBuilderFactory.get(STEP_NAME)
                .<OrderDto, OrderDto>chunk(10)
                .reader(jdbcPagingOrderReader())
                .writer(jdbcBatchItemWriter())
                .listener(new CustomStepListener())
                .build();
    }


    @Bean
    public JdbcPagingItemReader<OrderDto> jdbcPagingOrderReader() throws Exception {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("userName", "pooney");

        return new JdbcPagingItemReaderBuilder<OrderDto>()
                .pageSize(chunckSize)
                .fetchSize(chunckSize)
                .queryProvider(pagingQueryProvider())
                .parameterValues(parameterValues)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(OrderDto.class))
                .name("jdbcPagingOrderReader")
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<OrderDto> jdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<OrderDto>()
                .dataSource(dataSource)
                .sql("INSET INTO my_order(user_name,price) VALUES (:user_name, :price)")
                .beanMapped()
                .build();
    }


    @Bean
    public PagingQueryProvider pagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setSelectClause("SELECT ord_idx");
        queryProvider.setFromClause("FROM my_order");
        queryProvider.setWhereClause("WHERE user_name = :userName");
        queryProvider.setDataSource(dataSource);
        Map<String, Order> sortMap = new HashMap<>();
        sortMap.put("ord_idx", Order.ASCENDING);
        queryProvider.setSortKeys(sortMap);

        return queryProvider.getObject();

    }

}

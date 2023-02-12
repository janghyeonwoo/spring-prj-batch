package com.example.srpingprjbatch.job;

import com.example.srpingprjbatch.config.HttpUtil;
import com.example.srpingprjbatch.config.task.NoActionWriter;
import com.example.srpingprjbatch.config.task.RestItemReader;
import com.example.srpingprjbatch.config.task.RestItemWriter;
import com.example.srpingprjbatch.domain.dto.SyncClub;
import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import lombok.RequiredArgsConstructor;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.print.attribute.standard.JobName;
import javax.sql.DataSource;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class RestApiJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    private final String JOB_NAME = "REST_API_JOB";
    private final String STEP_NAME = "REST_API_JOB_STEP";
    private final HttpUtil httpUtil;

    @Bean(name = JOB_NAME)
    public Job apiJob(){
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(apiStep())
                .build();

    }


    @Bean(name = STEP_NAME)
    public Step apiStep(){
        return stepBuilderFactory.get(STEP_NAME)
                .<List<SyncClub>,List<SyncClub>>chunk(1)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public ItemReader<List<SyncClub>> itemReader(){
        return new RestItemReader(httpUtil);
    }

    @Bean
    public RestItemWriter<SyncClub> itemWriter(){
        String sql = "INSERT INTO community.sync_club(club_no, addr) VALUES (:golfSeq,'AAA') " +
                     "ON DUPLICATE KEY UPDATE addr = 'bbb'";

        JdbcBatchItemWriter<SyncClub> jdbcBatchItemWriter = new JdbcBatchItemWriterBuilder<SyncClub>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
        return new RestItemWriter<>(jdbcBatchItemWriter);
    }

}

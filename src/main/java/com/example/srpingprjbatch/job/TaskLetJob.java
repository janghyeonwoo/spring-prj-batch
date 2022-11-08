package com.example.srpingprjbatch.job;

import com.example.srpingprjbatch.config.dto.RequestDateJobParameter;
import com.example.srpingprjbatch.domain.dto.GameDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TaskLetJob {
    private final String JOB_NAME = "TEST_JOB2";
    private final String STEP_NAME = "TEST_STEP2";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @JobScope
    @Bean
    public RequestDateJobParameter requestDateJobParameter(){
        return new RequestDateJobParameter();
    };

    @Value("${chunkSize:1000}")
    private int chunkSize;


    @Bean(name = JOB_NAME)
    public Job testJob(){
        return jobBuilderFactory.get(JOB_NAME)
                .start(testStep())
                .build();

    }

    @JobScope
    @Bean(name = STEP_NAME)
    public Step testStep(){
        return stepBuilderFactory.get(STEP_NAME)
                .tasklet((contribution, chunkContext) -> {
                    log.info("--taskLet start");
                    return RepeatStatus.FINISHED;
                }).build();
    }

}

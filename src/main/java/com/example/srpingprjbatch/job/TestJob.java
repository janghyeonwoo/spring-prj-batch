package com.example.srpingprjbatch.job;

import com.example.srpingprjbatch.config.dto.UniqueRunIdIncrementer;
import com.example.srpingprjbatch.domain.Game;
import com.example.srpingprjbatch.config.dto.RequestDateJobParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TestJob {
    private final String JOB_NAME = "TODATE_TEST_JOB";
    private final String STEP_NAME = "TODATE_TEST_STEP1";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final RequestDateJobParameter requestDateJobParameter;
    private final EntityManagerFactory entityManagerFactory;

    @Value("${chunkSize:1000}")
    private int chunckSIZE;


    @Bean(name = JOB_NAME + "jobParameters")
    @JobScope
    public RequestDateJobParameter requestDateJobParameter(){
        return new RequestDateJobParameter();
    }


    @Bean(name = JOB_NAME)
    public Job testJob() throws Exception{
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new UniqueRunIdIncrementer())
                .start(testStep())
                .build();
    }

    @Bean
    @JobScope
    public Step testStep() throws Exception{
        return stepBuilderFactory.get(STEP_NAME)
                .<Game,Game>chunk(chunckSIZE)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Game> reader()throws ParseException {

        log.info("jobParameters :: {}", requestDateJobParameter.getLocalDateTime());
        Map<String,Object> parametersMap = new HashMap<>();
        parametersMap.put("currentDate", requestDateJobParameter.getLocalDateTime());
        return new JpaPagingItemReaderBuilder<Game>()
                .pageSize(10)
                .parameterValues(parametersMap)
                .queryString("SELECT p FROM Game p WHERE p.createDate >= :currentDate")
                .entityManagerFactory(entityManagerFactory)
                .name("Jpa___pageing__reader")
                .build();
    }

    @Bean
    @JobScope
    public ItemProcessor<Game,Game> processor(){
        return new ItemProcessor<Game, Game>() {
            @Override
            public Game process(Game item) throws Exception {
                log.info("ItemProcessor info :: {}", item);
                return null;
            }
        };
    }

    @Bean
    @JobScope
    public JpaItemWriter<Game> writer(){
        return new JpaItemWriterBuilder<Game>().entityManagerFactory(entityManagerFactory).build();
    }
}

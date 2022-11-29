package com.example.srpingprjbatch.job;

import com.example.srpingprjbatch.config.dto.UniqueRunIdIncrementer;
import com.example.srpingprjbatch.config.listener.CustomJobListener;
import com.example.srpingprjbatch.config.listener.CustomStepListener;
import com.example.srpingprjbatch.domain.Order;
import com.example.srpingprjbatch.domain.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * JdbcCursor를 사용하는 이유는 방대한 데이터의 경우 connection을 계속 물고 있기 대문에 자원활용에는 안좋다 하지만 예를 들면
 * JdbcPaging를 사용하는 경우 두개테이블을 조인하여 하였을대 SpringBatch에서 자동으로 페이징처리를 진행한다. 이떄 orderBy 기준으로 paging을 처리하기 위하여 지금까지 조회한 ord_idx를
 * 내부 적으로 가지고 있어 다음 조회할때 and ord_idx > 100 limit 10 로 만들어 쿼리를 날리게 된다. 하지만 join을 한 상태이기 때문에 ord_idx에 대하여 ambiguous라는 에러 메시지가 발생한다.
 * 이문제를 해결하기위해 JdbcCursor를 사용하는 것도 하나의 예이다.
 */
@RequiredArgsConstructor
@Configuration
public class CursorJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final String JOB_NAME = "ORDER_JOB";
    private final String STEP_NAME = "ORDER_STEP";


    @Value("${chunckSize:100}")
    private Integer chunkSize;


    @Bean(name = JOB_NAME)
    public Job orderJob(){
        return jobBuilderFactory.get("orderJob")
                .start(orderStep())
                .incrementer(new UniqueRunIdIncrementer())
                .listener(new CustomJobListener())
                .build();
    }


    @JobScope
    @Bean(name = STEP_NAME)
    public Step orderStep(){
        return stepBuilderFactory.get("orderStep")
                .<OrderDto,OrderDto>chunk(chunkSize)
                .reader(orderJdbcCursorItemReader())
                .writer(orderItemWriter())
                .listener(new CustomStepListener())
                .build();
    }

    @StepScope
    @Bean
    public JdbcCursorItemReader<OrderDto> orderJdbcCursorItemReader(){
        /**
         *    1. Parameter index out of range (1 > number of parameters, which is 0). ->  NamedParameterUtils.substituteNamedParameters를 설정안한 경우 parameter는 넘겼지만 사용을 못하니 에러 발생
         *    2. 처음 Order Entity로 resultSet했는데 이경우 setter도 없고 기본생성자도 없어서 아무러 결과값을 받지 못하였다. 그래서 Setter를 가진 OrderDto로 조회하닌 결과 값을 정상적으로 ResultSet하여 가져올수 있어 ItemWriter에서 정상적으로 업데이트가
         *        이루어짐;
         */

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("ordIdx", 1);
        String sql = "select o.ord_idx from my_order o where o.ord_idx > :ordIdx";
        return new JdbcCursorItemReaderBuilder<OrderDto>()
                .name("orderCursorItemReader")
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .sql(NamedParameterUtils.substituteNamedParameters(sql, new MapSqlParameterSource(parameterValues)))
                .rowMapper(new BeanPropertyRowMapper<>(OrderDto.class))
                .saveState(Boolean.FALSE) //중간을 저장히자 않고 다시 시작하면 처음부터 다시 롤백하고 시작하겠다는 뜻.
                .preparedStatementSetter(new ArgumentPreparedStatementSetter(new Object[]{1}))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Order,Order> orderOrderItemProcessor(){
        return (item) -> {
            System.out.println("ordIdx :: "+ item.getOrdIdx());
            return item;
        };
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<OrderDto> orderItemWriter(){
        /**
         * 중복 삭제요청의 경우 assertUpdates:false
         */
        return new JdbcBatchItemWriterBuilder<OrderDto>()
                .dataSource(dataSource)
                .sql("UPDATE my_order SET price = price + 1 WHERE ord_idx = :ordIdx")
                .assertUpdates(false)
                .beanMapped()
                .build();
    }

}

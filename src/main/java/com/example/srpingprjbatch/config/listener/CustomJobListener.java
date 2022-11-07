package com.example.srpingprjbatch.config.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import javax.batch.api.listener.JobListener;

@Slf4j
public class CustomJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("beforeJob : {}", jobExecution);

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("afterJob : {}", jobExecution);
        if(jobExecution.getStatus() == BatchStatus.FAILED){
            String jobName = jobExecution.getJobInstance().getJobName();
            log.info("{}을 실패했습니다.", jobName);
        }
    }
}

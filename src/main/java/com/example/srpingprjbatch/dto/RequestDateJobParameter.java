package com.example.srpingprjbatch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@NoArgsConstructor
public class RequestDateJobParameter {
    //해당 JOB이 성공했지만 재실행 해야할 경우 버전을 변경한다.
    @Value("#{jobParameters[version]}")
    private String version;
    private LocalDateTime localDateTime;

    @Value("#{jobParameters[requestDate]}")
    public void setLocalDateTime(String requestDate) {
            this.localDateTime = StringUtils.hasText(requestDate) ?
                    LocalDateTime.parse(requestDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    : LocalDateTime.now();
    }

}

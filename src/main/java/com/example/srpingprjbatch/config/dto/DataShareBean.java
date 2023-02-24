package com.example.srpingprjbatch.config.dto;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Slf4j
@Component
public class DataShareBean <T> {


    private Map<String, T> shareDataMap;

    public DataShareBean () {
    	// thread safe한 concurrentMap 이용
        this.shareDataMap = new ConcurrentHashMap<>();
    }

    public void putData(String key, T data) {
        if (shareDataMap ==  null) {
            log.error("Map is not initialize");
            return;
        }

        shareDataMap.put(key, data);
    }

    public T getData (String key) {

        if (shareDataMap == null) {
            return null;
        }

        return shareDataMap.get(key);
    }

    public int getSize () {
        if (this.shareDataMap == null) {
            log.error("Map is not initialize");
            return 0;
        }

        return shareDataMap.size();
    }

}

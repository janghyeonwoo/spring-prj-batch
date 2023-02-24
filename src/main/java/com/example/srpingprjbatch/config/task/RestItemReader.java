package com.example.srpingprjbatch.config.task;

import com.example.srpingprjbatch.config.HttpUtil;
import com.example.srpingprjbatch.domain.dto.Response;
import com.example.srpingprjbatch.domain.dto.SyncClub;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.POST;

@RequiredArgsConstructor
public class RestItemReader implements ItemReader<List<SyncClub>> {
    private final HttpUtil httpUtil;
    private boolean first = true;
    @Override
    public List<SyncClub> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(!first) return null;
        String url = "https://test.icexp.co.kr/api/kakao/cbReceive/golfList";

        /**
         * JSON Array를 받으려고 제네릭을 사용하였는데 결과를 잘받는다고 생각을 했지만 잘받지 못하였다.. List<SyncClub> 타입으로 받을 거라 생각햇지만 그게 아닌 Linked Hash map로 받고 있었던것이다.
         * 때문에 JdbcItemWriter에서 ':' 값으로 namedParameter인지 판별을 하고 namedParameter인면 pojo에서 값을 꺼내어 sql을 만든다 하지만 Pojo가 아닌 Linked Hash map이였기 때문에 type에러가 계속 난 것이 었다.
         * 이것을 ㅎ해결 하기  위하여 String  값으로 바꾸고 Response의 result를 제네릭에서 List<SyncClub>으로 타입을 강제하여 하니 성공적으로 Witer가 작동하였다.
         */


        Response response = httpUtil.callHttpBody(new SyncClub(), Response.class, POST, url, MediaType.APPLICATION_JSON, MediaType.ALL);

        first = false;
        return response.getResult().size() > 0 ? response.getResult() : null;
    }
}

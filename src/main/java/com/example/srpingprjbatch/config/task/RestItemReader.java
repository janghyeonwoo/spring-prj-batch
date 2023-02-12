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
        String url = "";
        String response = httpUtil.callHttpBody(new SyncClub(), String.class, POST, url, MediaType.APPLICATION_JSON, MediaType.ALL);
        Response res = new ObjectMapper().readValue(response,Response.class);
        List<SyncClub> clubList = res.getResult();
        first = false;
        return clubList.size() > 0 ? clubList : null;
    }
}

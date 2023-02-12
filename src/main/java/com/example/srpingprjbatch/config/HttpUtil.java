package com.example.srpingprjbatch.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Component
@Slf4j
public class HttpUtil {
    private WebClient webClient;

    public HttpUtil() {
        this.webClient = WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.newConnection().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)))
                .filter(
                        ExchangeFilterFunction.ofRequestProcessor(
                                clientRequest -> {
                                    log.info(">>>>>>>>>> WEBCLIENT_REQUEST <<<<<<<<<<");
                                    log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
                                    clientRequest.headers().forEach(
                                            (name, values) -> values.forEach(value -> log.info("{} : {}", name, value))
                                    );
                                    return Mono.just(clientRequest);
                                }
                        )
                )
                .filter(
                        ExchangeFilterFunction.ofResponseProcessor(
                                clientResponse -> {
                                    log.info(">>>>>>>>>> WEBCLIENT_RESPONSE <<<<<<<<<<");
                                    clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("{} : {}", name, value)));
                                    return Mono.just(clientResponse.mutate()
                                            .body(f -> f.map(dataBuffer -> {
                                                log.info("Response data: {} ", dataBuffer.toString(StandardCharsets.UTF_8));
                                                return dataBuffer;
                                            }))
                                            .build());
                                })
                )
                .build();
    }

    public <S, T> T callHttpBody(S data, Class<T> clazz, HttpMethod httpMethod, String url, MediaType contentType , MediaType acceptType)  {
        T responseData = null;
        try {
            if(HttpMethod.POST == httpMethod){
                responseData = webClient.post()
                        .uri(url)
                        .accept(acceptType == null ? MediaType.ALL : acceptType)
                        .contentType(contentType == null ? MediaType.APPLICATION_JSON : contentType)
                        .bodyValue(data)
                        .retrieve()
                        .onStatus(HttpStatus::is5xxServerError, errRes -> errRes.createException().flatMap(res -> Mono.error(RuntimeException::new)))
                        .onStatus(HttpStatus::is4xxClientError, errRes -> errRes.createException().flatMap(res -> Mono.error(RuntimeException::new)))
                        .bodyToMono(clazz)

//                .onErrorResume(thr -> Mono.error(new RuntimeException(thr)))
//                .onErrorReturn(clazz.getConstructor().newInstance())
                        .block();
            }else if (HttpMethod.GET == httpMethod){
                responseData = webClient.get()
                        .uri(url)
                        .accept(MediaType.ALL)
                        .retrieve()
                        .onStatus(HttpStatus::is5xxServerError, errRes -> errRes.createException().flatMap(res -> Mono.error(RuntimeException::new)))
                        .onStatus(HttpStatus::is4xxClientError, errRes -> errRes.createException().flatMap(res -> Mono.error(RuntimeException::new)))
                        .bodyToMono(clazz)
//                .onErrorResume(thr -> Mono.error(new RuntimeException(thr)))
//                .onErrorReturn(clazz.getConstructor().newInstance())
                        .block();
            }

        } catch (DecodingException decodingException) {
            log.info("[DecodingException] : {}: ",decodingException);
            return null;
        }
        return responseData;
    }


    public <S, T> T callHttpHeaderBody(S data, Class<T> clazz, HttpMethod httpMethod, String url, MediaType contentType , MediaType acceptType
            , Consumer<HttpHeaders> httpHeadersConsumer)  {
        T responseData = null;
        try {
            if(HttpMethod.POST == httpMethod){
                responseData = webClient.post()
                        .uri(url)
                        .headers(httpHeadersConsumer)
                        .accept(acceptType == null ? MediaType.ALL : acceptType)
                        .contentType(contentType == null ? MediaType.APPLICATION_JSON : contentType)
                        .bodyValue(data)
                        .retrieve()
                        .onStatus(HttpStatus::is5xxServerError, errRes -> errRes.createException().flatMap(res -> Mono.error(RuntimeException::new)))
                        .onStatus(HttpStatus::is4xxClientError, errRes -> errRes.createException().flatMap(res -> Mono.error(RuntimeException::new)))
                        .bodyToMono(clazz)
//                .onErrorResume(thr -> Mono.error(new RuntimeException(thr)))
//                .onErrorReturn(clazz.getConstructor().newInstance())
                        .block();
            }else if (HttpMethod.GET == httpMethod){
                responseData = webClient.get()
                        .uri(url)
                        .accept(MediaType.ALL)
                        .retrieve()
                        .onStatus(HttpStatus::is5xxServerError, errRes -> errRes.createException().flatMap(res -> Mono.error(RuntimeException::new)))
                        .onStatus(HttpStatus::is4xxClientError, errRes -> errRes.createException().flatMap(res -> Mono.error(RuntimeException::new)))
                        .bodyToMono(clazz)
//                .onErrorResume(thr -> Mono.error(new RuntimeException(thr)))
//                .onErrorReturn(clazz.getConstructor().newInstance())
                        .block();
            }

        } catch (DecodingException decodingException) {
            log.info("[DecodingException] : {}: ",decodingException);
            return null;
        }
        return responseData;
    }

    public <S, T> ResponseEntity<T> callHttpResponse(S data, Class<T> clazz, HttpMethod httpMethod, String url) {
        ResponseEntity<T> block = webClient.post()
                .uri(url)
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(data)
                .retrieve()
                .onStatus(HttpStatus::isError, errRes -> errRes.createException().flatMap(res -> Mono.error(RuntimeException::new)))
                .toEntity(clazz)
//                .onErrorResume(thr -> Mono.error(new RuntimeException(thr)))
//                .onErrorReturn(clazz.getConstructor().newInstance())
                .block();
        return block;
    }

    public <S,T> T convertValue(S data , TypeReference<T> t) {
        return new ObjectMapper().convertValue(data, t);
    }
}

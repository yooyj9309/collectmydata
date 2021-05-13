package com.banksalad.collectmydata.common.collect.executor;

import com.banksalad.collectmydata.common.collect.api.ApiResponseEntity;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TransferClientImpl implements TransferClient {

  private static final int CONNECT_TIMEOUT_SECS = 3;
  private static final int READ_TIMEOUT_SECS = 15;
  private static final int WRITE_TIMEOUT_SECS = 15;

  private static final int MAX_IN_MEMORY_SIZE_BYTES = 1024 * 1024 * 3;    // 3mb

  private static final WebClient webClient;

  static {
    ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE_BYTES))
        .build();

    HttpClient httpClient = HttpClient.create()
        .responseTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECS))
        .doOnConnected(conn -> conn
            .addHandler(new ReadTimeoutHandler(READ_TIMEOUT_SECS))
            .addHandler(new WriteTimeoutHandler(WRITE_TIMEOUT_SECS)));

    webClient = WebClient.builder()
        .exchangeStrategies(exchangeStrategies)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }

  @Override
  public ApiResponseEntity execute(String baseUrl, String uri, String httpMethod,
      Map<String, String> headers, String body) {

    try {
      return executeReactive(baseUrl, uri, httpMethod, headers, body).toFuture().get();

    } catch (InterruptedException | ExecutionException e) {
      throw new CollectRuntimeException("Fail to transfer request", e);
    }
  }


  private Mono<ApiResponseEntity> executeReactive(String baseUrl, String uri, String httpMethod,
      Map<String, String> headers, String body) {

    return webClient.method(HttpMethod.valueOf(httpMethod))
        .uri(baseUrl + uri)
        .accept(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.setAll(headers))
        .bodyValue(body)
        .exchangeToMono(response -> response.bodyToMono(String.class)
            .map(stringBody ->
                ApiResponseEntity.builder()
                    .httpStatusCode(response.rawStatusCode())
                    .headers(
                        response.headers().asHttpHeaders().entrySet().stream()
                            .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue()))))
                    .body(stringBody)
                    .build())
        );
  }
}

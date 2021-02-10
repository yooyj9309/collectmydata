package com.banksalad.collectmydata.common.collect.executor;

import com.banksalad.collectmydata.common.collect.api.ApiResponseEntity;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TransferClientImpl implements TransferClient {

  ExchangeStrategies exchangeStrategies = ExchangeStrategies
      .builder()
      .codecs(configurer -> configurer.defaultCodecs()
          .maxInMemorySize(1024 * 1024 * 50))
      .build();

  @Override
  public ApiResponseEntity execute(String baseUrl, String uri, String httpMethod,
      Map<String, String> headers, String body) {

    try {
      return executeReactive(baseUrl, uri, httpMethod, headers, body)
          .toFuture().get();
    } catch (InterruptedException | ExecutionException e) {
      throw new CollectRuntimeException("Fail to transfer request", e);
    }
  }

  @Override
  public Mono<ApiResponseEntity> executeReactive(String baseUrl, String uri, String httpMethod,
      Map<String, String> headers, String body) {

    return WebClient.builder().baseUrl(baseUrl).build()
        .method(HttpMethod.valueOf(httpMethod))
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.setAll(headers))
        .bodyValue(body)
        .exchangeToMono(response -> response.bodyToMono(String.class)
            .map(stringBody ->
                ApiResponseEntity.builder()
                    .httpStatusCode(response.rawStatusCode())
                    .headers(
                        response.headers().asHttpHeaders().entrySet().stream()
                            .collect(
                                Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue()))))
                    .body(stringBody)
                    .build())
        );
  }
}

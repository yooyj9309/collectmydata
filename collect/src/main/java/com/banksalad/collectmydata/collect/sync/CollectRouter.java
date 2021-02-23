package com.banksalad.collectmydata.collect.sync;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Slf4j
@Configuration
public class CollectRouter {

  @Bean
  public RouterFunction<ServerResponse> routes(CollectHandler collectHandler) {
    return route(RequestPredicates.POST("/v1/collectmydata/finance/bank/sync")
        .and(contentType(MediaType.APPLICATION_JSON)), collectHandler::syncFinanceBank);
  }

}

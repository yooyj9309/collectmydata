package com.banksalad.collectmydata.collect.sync;

import com.banksalad.collectmydata.collect.sync.dto.SyncFinanceBankRequest;
import com.banksalad.collectmydata.common.dto.SyncFinanceBankResponse;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CollectHandler {

  private final CollectSyncService collectSyncService;

  public CollectHandler(CollectSyncService collectSyncService) {
    this.collectSyncService = collectSyncService;
  }

  public Mono<ServerResponse> syncFinanceBank(ServerRequest serverRequest) {

    Mono<SyncFinanceBankResponse> syncCompletedMessageMono =
        collectSyncService.syncFinanceBank(serverRequest.bodyToMono(SyncFinanceBankRequest.class));

    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(syncCompletedMessageMono, SyncCompletedMessage.class);

  }
}

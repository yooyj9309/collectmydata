package com.banksalad.collectmydata.collect.sync;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectHandler {

  private final CollectSyncService collectSyncService;

//  public Mono<ServerResponse> syncFinanceBank(ServerRequest serverRequest) {
//
//    Mono<SyncFinanceBankResponse> syncCompletedMessageMono =
//        collectSyncService.syncFinanceBank(serverRequest.bodyToMono(SyncFinanceBankRequest.class));
//
//    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(syncCompletedMessageMono, SyncCompletedMessage.class);
//
//  }
}

package com.banksalad.collectmydata.collect.sync;

import com.banksalad.collectmydata.collect.common.service.CollectMessageService;
import com.banksalad.collectmydata.collect.common.service.RedisPubSubService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectSyncServiceImpl implements CollectSyncService {

  private final CollectMessageService collectMessageService;
  private final RedisPubSubService redisPubSubService;

//  @Override
//  public Mono<SyncFinanceBankResponse> syncFinanceBank(Mono<SyncFinanceBankRequest> restSyncFinanceBankRequest) {
//    return
//        restSyncFinanceBankRequest
//            .flatMap(syncFinanceBankRequest -> {
//
//              long banksaladUserId = syncFinanceBankRequest.getBanksaladUserId();
//              String syncRequestId = UUID.randomUUID().toString();
//
//              Mono<SyncFinanceBankResponse> syncFinanceBankResponse =
//                  redisPubSubService.subscribeSyncResponse(banksaladUserId, syncRequestId, SyncFinanceBankResponse.class);
//
//              produceSyncRequest(
//                  SyncRequestedMessage.builder()
//                      .banksaladUserId(banksaladUserId)
//                      .organizationId("woori_bank")   // TODO : set proper organizationId
//                      .syncRequestId(syncRequestId)
//                      .syncRequestType(SyncRequestType.ONDEMAND)
//                      .build());
//
//              return syncFinanceBankResponse;
//            });
//  }
//
//  @Override
//  public void syncFinanceCard(SyncFinanceCardRequest request) {
//
//  }
//
//  @Override
//  public void syncFinanceInvest(SyncFinanceInvestRequest request) {
//
//  }
//
//  @Override
//  public void syncFinanceInsu(SyncFinanceInsuRequest request) {
//
//  }
//
//  @Override
//  public void syncFinanceEfin(SyncFinanceEfinRequest request) {
//
//  }
//
//  @Override
//  public void syncFinanceCapital(SyncFinanceCapitalRequest request) {
//
//  }
//
//  // TODO : connect produce and listen
//  private void produceSyncRequest(SyncRequestedMessage syncRequestedMessage) {
//    collectMessageService.produceBankSyncRequested(syncRequestedMessage)
//        .addCallback(
//            CollectListenableFutureCallbackFactory
//                .create(MessageTopic.bankSyncCompleted, syncRequestedMessage.getSyncRequestId()));
//  }
//
//  static class CollectListenableFutureCallbackFactory {
//
//    public static ListenableFutureCallback<SendResult<String, String>> create(String topic, String syncRequestId) {
//      return new ListenableFutureCallback() {
//        @Override
//        public void onSuccess(Object result) {
//          log.info("Produce topic: {}, syncRequestId: {}", topic, syncRequestId);
//        }
//
//        @Override
//        public void onFailure(Throwable t) {
//          log.error("Fail to produce topic: {}, syncRequestId:{}, exception={}", topic, syncRequestId, t.getMessage());
//        }
//      };
//    }
//  }
}

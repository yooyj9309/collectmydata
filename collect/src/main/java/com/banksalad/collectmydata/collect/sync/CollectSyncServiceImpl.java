package com.banksalad.collectmydata.collect.sync;

import com.banksalad.collectmydata.collect.common.service.CollectMessageService;
import com.banksalad.collectmydata.collect.common.service.RedisPubSubService;
import com.banksalad.collectmydata.collect.sync.dto.SyncFinanceBankRequest;
import com.banksalad.collectmydata.common.dto.SyncFinanceBankResponse;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;

import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.github.banksalad.idl.apis.v1.collectmydata.FinanceCapitalProto.SyncFinanceCapitalRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceCardProto.SyncFinanceCardRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceEfinProto.SyncFinanceEfinRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceInsuProto.SyncFinanceInsuRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.FinanceInvestProto.SyncFinanceInvestRequest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
public class CollectSyncServiceImpl implements CollectSyncService {

  private final CollectMessageService collectMessageService;
  private final RedisPubSubService redisPubSubService;

  public CollectSyncServiceImpl(CollectMessageService collectMessageService, RedisPubSubService redisPubSubService) {
    this.collectMessageService = collectMessageService;
    this.redisPubSubService = redisPubSubService;
  }

  @Override
  public Mono<SyncFinanceBankResponse> syncFinanceBank(Mono<SyncFinanceBankRequest> restSyncFinanceBankRequest) {
    return
        restSyncFinanceBankRequest
            .flatMap(syncFinanceBankRequest -> {

              long banksaladUserId = syncFinanceBankRequest.getBanksaladUserId();
              String syncRequestId = UUID.randomUUID().toString();

              Mono<SyncFinanceBankResponse> syncFinanceBankResponse =
                  redisPubSubService.subscribeSyncResponse(banksaladUserId, syncRequestId, SyncFinanceBankResponse.class);

              produceSyncRequest(
                  SyncRequestedMessage.builder()
                      .banksaladUserId(banksaladUserId)
                      .organizationId("woori_bank")   // TODO : set proper organizationId
                      .syncRequestId(syncRequestId)
                      .syncRequestType(SyncRequestType.ONDEMAND)
                      .build());

              return syncFinanceBankResponse;
            });
  }

  @Override
  public void syncFinanceCard(SyncFinanceCardRequest request) {

  }

  @Override
  public void syncFinanceInvest(SyncFinanceInvestRequest request) {

  }

  @Override
  public void syncFinanceInsu(SyncFinanceInsuRequest request) {

  }

  @Override
  public void syncFinanceEfin(SyncFinanceEfinRequest request) {

  }

  @Override
  public void syncFinanceCapital(SyncFinanceCapitalRequest request) {

  }

  // TODO : connect produce and listen
  private void produceSyncRequest(SyncRequestedMessage syncRequestedMessage) {
    collectMessageService.produceBankSyncRequested(syncRequestedMessage)
        .addCallback(
            CollectListenableFutureCallbackFactory
                .create(MessageTopic.bankSyncCompleted, syncRequestedMessage.getSyncRequestId()));
  }

  static class CollectListenableFutureCallbackFactory {

    public static ListenableFutureCallback<SendResult<String, String>> create(String topic, String syncRequestId) {
      return new ListenableFutureCallback() {
        @Override
        public void onSuccess(Object result) {
          log.info("Produce topic: {}, syncRequestId: {}", topic, syncRequestId);
        }

        @Override
        public void onFailure(Throwable t) {
          log.error("Fail to produce topic: {}, syncRequestId:{}, exception={}", topic, syncRequestId, t.getMessage());
        }
      };
    }
  }
}

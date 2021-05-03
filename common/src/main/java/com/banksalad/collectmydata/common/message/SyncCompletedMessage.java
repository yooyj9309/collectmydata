package com.banksalad.collectmydata.common.message;

import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.CollectmydatabankSyncItem;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.CollectmydatacardSyncItem;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.CollectmydatainvestSyncItem;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.NotifyCollectmydatabankSyncedRequest;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.NotifyCollectmydatacardSyncedRequest;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.NotifyCollectmydatainvestSyncedRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SyncCompletedMessage {

  private long banksaladUserId;
  private String organizationId;
  private String syncRequestId;
  private SyncRequestType syncRequestType;

  public NotifyCollectmydatabankSyncedRequest toNotifyBankRequest() {

    return NotifyCollectmydatabankSyncedRequest.newBuilder()
        .setBanksaladUserId(String.valueOf(banksaladUserId))
        .setCollectmydataSyncRequestUuid(syncRequestId)
        .setCollectmydatabankSyncItemEnum(CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_COMPLETED.name())
        .build();
  }

  public NotifyCollectmydatainvestSyncedRequest toNotifyInvestRequest() {

    return NotifyCollectmydatainvestSyncedRequest.newBuilder()
        .setBanksaladUserId(String.valueOf(banksaladUserId))
        .setCollectmydataSyncRequestUuid(syncRequestId)
        .setCollectmydatainvestSyncItemEnum(CollectmydatainvestSyncItem.COLLECTMYDATAINVEST_SYNC_ITEM_COMPLETED.name())
        .build();
  }

  public NotifyCollectmydatacardSyncedRequest toNotifyCardRequest() {

    return NotifyCollectmydatacardSyncedRequest.newBuilder()
        .setBanksaladUserId(String.valueOf(banksaladUserId))
        .setCollectmydataSyncRequestUuid(syncRequestId)
        .setCollectmydatacardSyncItemEnum(CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_COMPLETED.name())
        .build();
  }
}


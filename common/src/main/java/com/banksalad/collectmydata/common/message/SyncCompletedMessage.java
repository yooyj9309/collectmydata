package com.banksalad.collectmydata.common.message;

import com.banksalad.collectmydata.common.enums.SyncRequestType;

import com.github.banksalad.idl.apis.v1.finance.FinanceProto.CollectmydatabankSyncItem;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.NotifyCollectmydatabankSyncedRequest;
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

  public NotifyCollectmydatabankSyncedRequest toNotifyRequest() {

    return NotifyCollectmydatabankSyncedRequest.newBuilder()
        .setBanksaladUserId(String.valueOf(banksaladUserId))
        .setCollectmydataSyncRequestUuid(syncRequestId)
        .setCollectmydatabankSyncItemEnum(CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_COMPLETED.name())
        .build();
  }
}


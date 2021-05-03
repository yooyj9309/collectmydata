package com.banksalad.collectmydata.common.message;

import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.CollectmydatainvestSyncItem;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.InvestAccountTransactionSyncCondition;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.NotifyCollectmydatainvestSyncedRequest;
import lombok.Builder;
import lombok.Getter;

import static com.banksalad.collectmydata.common.enums.FinanceSyncItem.COLLECTMYDATAINVEST_SYNC_ITEM_ACCOUNT_TRANSACTION;

@Getter
@Builder
public class PublishmentRequestedInvestMessage implements PublishmentRequestedMessage {

  private final FinanceIndustry financeIndustry;
  private final FinanceSyncItem financeSyncItem;

  private final long banksaladUserId;
  private final String organizationId;
  private final String syncRequestId;

  private final String accountNum;
  private final boolean hasNextPage;

  public NotifyCollectmydatainvestSyncedRequest toNotifyRequest() {
    if (financeSyncItem == COLLECTMYDATAINVEST_SYNC_ITEM_ACCOUNT_TRANSACTION) {
      return NotifyCollectmydatainvestSyncedRequest.newBuilder()
          .setBanksaladUserId(String.valueOf(banksaladUserId))
          .setCollectmydataSyncRequestUuid(syncRequestId)
          .setCollectmydatainvestSyncItemEnum(convertFinanceSyncItemToProtoEnum(financeSyncItem).name())
          .setTransactionSyncCondition(InvestAccountTransactionSyncCondition.newBuilder()
              .setAccountNumber(accountNum)
              .setHasNextPage(hasNextPage)
              .build())
          .build();
    }

    return NotifyCollectmydatainvestSyncedRequest.newBuilder()
        .setBanksaladUserId(String.valueOf(banksaladUserId))
        .setCollectmydataSyncRequestUuid(syncRequestId)
        .setCollectmydatainvestSyncItemEnum(convertFinanceSyncItemToProtoEnum(financeSyncItem).name())
        .build();
  }

  public CollectmydatainvestSyncItem convertFinanceSyncItemToProtoEnum(FinanceSyncItem financeSyncItem) {
    switch (financeSyncItem) {
      case COLLECTMYDATAINVEST_SYNC_ITEM_ACCOUNT_SUMMARY:
        return CollectmydatainvestSyncItem.COLLECTMYDATAINVEST_SYNC_ITEM_ACCOUNT_SUMMARY;

      case COLLECTMYDATAINVEST_SYNC_ITEM_ACCOUNT_BASIC:
        return CollectmydatainvestSyncItem.COLLECTMYDATAINVEST_SYNC_ITEM_ACCOUNT_BASIC;

      case COLLECTMYDATAINVEST_SYNC_ITEM_ACCOUNT_TRANSACTION:
        return CollectmydatainvestSyncItem.COLLECTMYDATAINVEST_SYNC_ITEM_ACCOUNT_TRANSACTION;

      case COLLECTMYDATAINVEST_SYNC_ITEM_ACCOUNT_PRODUCT:
        return CollectmydatainvestSyncItem.COLLECTMYDATAINVEST_SYNC_ITEM_ACCOUNT_PRODUCT;

      case COLLECTMYDATAINVEST_SYNC_ITEM_COMPLETED:
        return CollectmydatainvestSyncItem.COLLECTMYDATAINVEST_SYNC_ITEM_COMPLETED;

      default:
        return CollectmydatainvestSyncItem.COLLECTMYDATAINVEST_SYNC_ITEM_UNKNOWN;
    }
  }
}

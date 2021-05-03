package com.banksalad.collectmydata.common.message;

import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.grpc.converter.ProtoTypeConverter;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.BankAccountTransactionSyncCondition;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.CardTransactionSyncCondition;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.CollectmydatacardSyncItem;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.NotifyCollectmydatacardSyncedRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PublishmentRequestedCardMessage implements PublishmentRequestedMessage {

  private final FinanceIndustry financeIndustry;
  private final FinanceSyncItem financeSyncItem;

  private final long banksaladUserId;
  private final String organizationId;
  private final String syncRequestId;

  private final String cardId;
  private final boolean hasNextPage;

  public NotifyCollectmydatacardSyncedRequest toNotifyRequest() {
    switch (financeSyncItem) {
      case COLLECTMYDATACARD_SYNC_ITEM_CARD_BILL_BASIC:
      case COLLECTMYDATACARD_SYNC_ITEM_CARD_APPROVAL_DOMESTIC_TRANSACTION:
      case COLLECTMYDATACARD_SYNC_ITEM_CARD_APPROVAL_OVERSEAS_TRANSACTION:
        return NotifyCollectmydatacardSyncedRequest.newBuilder()
            .setBanksaladUserId(String.valueOf(banksaladUserId))
            .setCollectmydataSyncRequestUuid(syncRequestId)
            .setCollectmydatacardSyncItemEnum(convertFinanceSyncItemToProtoEnum(financeSyncItem).name())
            .setTransactionSyncCondition(CardTransactionSyncCondition.newBuilder()
                .setMydataCardId(cardId)
                .setHasNextPage(hasNextPage)
                .build())
            .build();

      default:
        return NotifyCollectmydatacardSyncedRequest.newBuilder()
            .setBanksaladUserId(String.valueOf(banksaladUserId))
            .setCollectmydataSyncRequestUuid(syncRequestId)
            .setCollectmydatacardSyncItemEnum(convertFinanceSyncItemToProtoEnum(financeSyncItem).name())
            .build();
    }
  }

  public CollectmydatacardSyncItem convertFinanceSyncItemToProtoEnum(FinanceSyncItem financeSyncItem) {
    switch (financeSyncItem) {
      case COLLECTMYDATACARD_SYNC_ITEM_CARD_SUMMARY:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_CARD_SUMMARY;

      case COLLECTMYDATACARD_SYNC_ITEM_CARD_BASIC:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_CARD_BASIC;

      case COLLECTMYDATACARD_SYNC_ITEM_CARD_POINT:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_CARD_POINT;

      case COLLECTMYDATACARD_SYNC_ITEM_CARD_BILL_BASIC:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_CARD_BILL_BASIC;

      case COLLECTMYDATACARD_SYNC_ITEM_CARD_BILL_DETAIL:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_CARD_BILL_DETAIL;

      case COLLECTMYDATACARD_SYNC_ITEM_CARD_PAYMENT:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_CARD_PAYMENT;

      case COLLECTMYDATACARD_SYNC_ITEM_CARD_APPROVAL_DOMESTIC_TRANSACTION:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_CARD_APPROVAL_DOMESTIC_TRANSACTION;

      case COLLECTMYDATACARD_SYNC_ITEM_CARD_APPROVAL_OVERSEAS_TRANSACTION:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_CARD_APPROVAL_OVERSEAS_TRANSACTION;

      case COLLECTMYDATACARD_SYNC_ITEM_LOAN_SUMMARY:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_LOAN_SUMMARY;

      case COLLECTMYDATACARD_SYNC_ITEM_LOAN_REVOLVING:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_LOAN_REVOLVING;

      case COLLECTMYDATACARD_SYNC_ITEM_LOAN_SHORT_TERM:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_LOAN_SHORT_TERM;

      case COLLECTMYDATACARD_SYNC_ITEM_LOAN_LONG_TERM:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_LOAN_LONG_TERM;

      default:
        return CollectmydatacardSyncItem.COLLECTMYDATACARD_SYNC_ITEM_UNKNOWN;
    }
  }
}

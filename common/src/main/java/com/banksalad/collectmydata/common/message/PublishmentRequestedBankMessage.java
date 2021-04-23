package com.banksalad.collectmydata.common.message;

import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.grpc.converter.ProtoTypeConverter;

import com.github.banksalad.idl.apis.v1.finance.FinanceProto.CollectmydatabankSyncItem;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.NotifyCollectmydatabankSyncedRequest;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.TransactionSyncCondition;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PublishmentRequestedBankMessage implements PublishmentRequestedMessage {

  private final FinanceIndustry financeIndustry;
  private final FinanceSyncItem financeSyncItem;

  private final long banksaladUserId;
  private final String organizationId;
  private final String syncRequestId;

  private final String accountNum;
  private final String seqno;
  private final boolean hasNextPage;

  public NotifyCollectmydatabankSyncedRequest toNotifyRequest() {

    switch (financeSyncItem) {
      case COLLECTMYDATA_BANK_SYNC_ITEM_DEPOSIT_ACCOUNT_TRANSACTION:
      case COLLECTMYDATA_BANK_SYNC_ITEM_INVEST_ACCOUNT_TRANSACTION:
      case COLLECTMYDATA_BANK_SYNC_ITEM_LOAN_ACCOUNT_TRANSACTION:
        return NotifyCollectmydatabankSyncedRequest.newBuilder()
            .setBanksaladUserId(String.valueOf(banksaladUserId))
            .setCollectmydataSyncRequestUuid(syncRequestId)
            .setCollectmydatabankSyncItemEnum(convertFinanceSyncItemToProtoEnum(financeSyncItem).name())
            .setTransactionSyncCondition(TransactionSyncCondition.newBuilder()
                .setAccountNumber(accountNum)
                .setSequenceNumber(ProtoTypeConverter.toStringValue(seqno))
                .setHasNextPage(hasNextPage)
                .build())
            .build();
        
      default:
        return NotifyCollectmydatabankSyncedRequest.newBuilder()
            .setBanksaladUserId(String.valueOf(banksaladUserId))
            .setCollectmydataSyncRequestUuid(syncRequestId)
            .setCollectmydatabankSyncItemEnum(convertFinanceSyncItemToProtoEnum(financeSyncItem).name())
            .build();
    }
  }

  public CollectmydatabankSyncItem convertFinanceSyncItemToProtoEnum(FinanceSyncItem financeSyncItem) {
    switch (financeSyncItem) {
      case COLLECTMYDATA_BANK_SYNC_ITEM_ACCOUNT_SUMMARY:
        return CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_ACCOUNT_SUMMARY;

      case COLLECTMYDATA_BANK_SYNC_ITEM_DEPOSIT_ACCOUNT_BASIC:
        return CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_DEPOSIT_ACCOUNT_BASIC;

      case COLLECTMYDATA_BANK_SYNC_ITEM_DEPOSIT_ACCOUNT_DETAIL:
        return CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_DEPOSIT_ACCOUNT_DETAIL;

      case COLLECTMYDATA_BANK_SYNC_ITEM_DEPOSIT_ACCOUNT_TRANSACTION:
        return CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_DEPOSIT_ACCOUNT_TRANSACTION;

      case COLLECTMYDATA_BANK_SYNC_ITEM_INVEST_ACCOUNT_BASIC:
        return CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_INVEST_ACCOUNT_BASIC;

      case COLLECTMYDATA_BANK_SYNC_ITEM_INVEST_ACCOUNT_DETAIL:
        return CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_INVEST_ACCOUNT_DETAIL;

      case COLLECTMYDATA_BANK_SYNC_ITEM_INVEST_ACCOUNT_TRANSACTION:
        return CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_INVEST_ACCOUNT_TRANSACTION;

      case COLLECTMYDATA_BANK_SYNC_ITEM_LOAN_ACCOUNT_BASIC:
        return CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_LOAN_ACCOUNT_BASIC;

      case COLLECTMYDATA_BANK_SYNC_ITEM_LOAN_ACCOUNT_DETAIL:
        return CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_LOAN_ACCOUNT_DETAIL;

      case COLLECTMYDATA_BANK_SYNC_ITEM_LOAN_ACCOUNT_TRANSACTION:
        return CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_LOAN_ACCOUNT_TRANSACTION;

      case COLLECTMYDATA_BANK_SYNC_ITEM_COMPLETED:
        return CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_COMPLETED;

      default:
        return CollectmydatabankSyncItem.COLLECTMYDATABANK_SYNC_ITEM_UNKNOWN;
    }
  }
}

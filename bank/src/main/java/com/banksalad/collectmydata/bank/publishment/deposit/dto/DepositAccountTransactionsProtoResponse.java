package com.banksalad.collectmydata.bank.publishment.deposit.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankDepositAccountTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountTransactionsResponse;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DepositAccountTransactionsProtoResponse {

  List<DepositAccountTransactionResponse> depositAccountTransactionResponses;

  public ListBankDepositAccountTransactionsResponse toListBankDepositAccountTransactionsResponseProto() {
    List<BankDepositAccountTransaction> bankDepositAccountTransactions = depositAccountTransactionResponses.stream()
        .map(depositAccountTransactionResponse -> BankDepositAccountTransaction.newBuilder()
            .setAccountNum(depositAccountTransactionResponse.getAccountNum())
            .setSeqno(StringValue.newBuilder().setValue(depositAccountTransactionResponse.getSeqno()).build())
            .setCurrencyCode(depositAccountTransactionResponse.getCurrencyCode())
            .setTransDtime(depositAccountTransactionResponse.getTransDtime())
            .setTransNo(StringValue.newBuilder().setValue(depositAccountTransactionResponse.getTransNo()).build())
            .setTransType(depositAccountTransactionResponse.getTransType())
            .setTransClass(depositAccountTransactionResponse.getTransClass())
            .setTransAmt3F(depositAccountTransactionResponse.getTransAmt().longValue())
            .setBalanceAmt3F(depositAccountTransactionResponse.getBalanceAmt().longValue())
            .setPaidInCnt(Int64Value.newBuilder().setValue(depositAccountTransactionResponse.getPaidInCnt()).build())
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(depositAccountTransactionResponse.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(depositAccountTransactionResponse.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return ListBankDepositAccountTransactionsResponse.newBuilder()
        .addAllDepositAccountTransactions(bankDepositAccountTransactions)
        .build();
  }
}

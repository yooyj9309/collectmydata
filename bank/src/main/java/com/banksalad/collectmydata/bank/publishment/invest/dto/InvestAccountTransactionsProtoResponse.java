package com.banksalad.collectmydata.bank.publishment.invest.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankInvestAccountTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountTransactionsResponse;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestAccountTransactionsProtoResponse {

  List<InvestAccountTransactionResponse> investAccountTransactionResponses;

  public ListBankInvestAccountTransactionsResponse toListBankInvestAccountTransactionsResponse() {
    List<BankInvestAccountTransaction> bankInvestAccountTransactions = investAccountTransactionResponses.stream()
        .map(investAccountTransactionResponse -> BankInvestAccountTransaction.newBuilder()
            .setAccountNum(investAccountTransactionResponse.getAccountNum())
            .setSeqno(StringValue.newBuilder().setValue(investAccountTransactionResponse.getSeqno()).build())
            .setCurrencyCode(investAccountTransactionResponse.getCurrencyCode())
            .setTransDtime(investAccountTransactionResponse.getTransDtime())
            .setTransNo(StringValue.newBuilder().setValue(investAccountTransactionResponse.getTransNo()).build())
            .setTransType(investAccountTransactionResponse.getTransType())
            .setBaseAmt3F(
                Int64Value.newBuilder().setValue(investAccountTransactionResponse.getBaseAmt().longValue()).build())
            .setTransFundNum3F(
                Int64Value.newBuilder().setValue(investAccountTransactionResponse.getTransFundNum().longValue())
                    .build())
            .setTransAmt3F(investAccountTransactionResponse.getTransAmt().longValue())
            .setBalanceAmt3F(investAccountTransactionResponse.getBalanceAmt().longValue())
            .setCreatedAtMs(
                DateUtil.utcLocalDateTimeToEpochMilliSecond(investAccountTransactionResponse.getCreatedAt()))
            .setUpdatedAtMs(
                DateUtil.utcLocalDateTimeToEpochMilliSecond(investAccountTransactionResponse.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return ListBankInvestAccountTransactionsResponse.newBuilder()
        .addAllInvestAccountTransactions(bankInvestAccountTransactions)
        .build();
  }
}

package com.banksalad.collectmydata.bank.publishment.summary.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankAccountSummary;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankAccountSummariesResponse;
import com.google.protobuf.BoolValue;
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
public class AccountSummariesProtoResponse {

  List<AccountSummaryResponse> accountSummaryResponses;

  public ListBankAccountSummariesResponse toListBankAccountSummariesResponseProto() {
    List<BankAccountSummary> bankAccountSummaries = accountSummaryResponses.stream()
        .map(accountSummaryResponse -> BankAccountSummary.newBuilder()
            .setAccountNum(accountSummaryResponse.getAccountNum())
            .setSeqno(StringValue.newBuilder().setValue(accountSummaryResponse.getSeqno()).build())
            .setIsConsent(accountSummaryResponse.isConsent())
            .setIsForeignDeposit(BoolValue.newBuilder().setValue(accountSummaryResponse.isForeignDeposit()).build())
            .setProdName(accountSummaryResponse.getProdName())
            .setAccountType(accountSummaryResponse.getAccountType())
            .setAccountStatus(accountSummaryResponse.getAccountStatus())
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(accountSummaryResponse.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(accountSummaryResponse.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return ListBankAccountSummariesResponse.newBuilder()
        .addAllAccountSummaries(bankAccountSummaries)
        .build();
  }
}

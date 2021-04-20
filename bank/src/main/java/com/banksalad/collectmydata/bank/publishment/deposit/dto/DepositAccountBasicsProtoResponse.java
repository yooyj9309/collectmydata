package com.banksalad.collectmydata.bank.publishment.deposit.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankDepositAccountBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountBasicsResponse;
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
public class DepositAccountBasicsProtoResponse {

  List<DepositAccountBasicResponse> depositAccountBasicResponses;

  public ListBankDepositAccountBasicsResponse toListBankDepositAccountBasicsResponseProto() {
    List<BankDepositAccountBasic> bankDepositAccountBasics = depositAccountBasicResponses.stream()
        .map(depositAccountBasicResponse -> BankDepositAccountBasic.newBuilder()
            .setAccountNum(depositAccountBasicResponse.getAccountNum())
            .setSeqno(StringValue.newBuilder().setValue(depositAccountBasicResponse.getSeqno()).build())
            .setCurrencyCode(depositAccountBasicResponse.getCurrencyCode())
            .setSavingMethod(depositAccountBasicResponse.getSavingMethod())
            .setHolderName(depositAccountBasicResponse.getHolderName())
            .setIssueDate(depositAccountBasicResponse.getIssueDate())
            .setExpDate(StringValue.newBuilder().setValue(depositAccountBasicResponse.getExpDate()).build())
            .setCommitAmt3F(
                Int64Value.newBuilder().setValue(depositAccountBasicResponse.getCommitAmt().longValue()).build())
            .setMonthlyPaidInAmt3F(
                Int64Value.newBuilder().setValue(depositAccountBasicResponse.getMonthlyPaidInAmt().longValue())
                    .build())
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(depositAccountBasicResponse.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(depositAccountBasicResponse.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return ListBankDepositAccountBasicsResponse.newBuilder()
        .addAllDepositAccountBasics(bankDepositAccountBasics)
        .build();
  }
}

package com.banksalad.collectmydata.bank.publishment.deposit.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankDepositAccountDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountDetailsResponse;
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
public class DepositAccountDetailsProtoResponse {

  List<DepositAccountDetailResponse> depositAccountDetailResponses;

  public ListBankDepositAccountDetailsResponse toListBankDepositAccountDetailsResponseProto() {
    List<BankDepositAccountDetail> bankDepositAccountDetails = depositAccountDetailResponses.stream()
        .map(depositAccountDetailResponse -> BankDepositAccountDetail.newBuilder()
            .setAccountNum(depositAccountDetailResponse.getAccountNum())
            .setSeqno(StringValue.newBuilder().setValue(depositAccountDetailResponse.getSeqno()).build())
            .setCurrencyCode(depositAccountDetailResponse.getCurrencyCode())
            .setBalanceAmt3F(depositAccountDetailResponse.getBalanceAmt().longValue())
            .setWithdrawableAmt3F(depositAccountDetailResponse.getWithdrawableAmt().longValue())
            .setOfferedRate5F(depositAccountDetailResponse.getOfferedRate().longValue())
            .setLastPaidInCnt(Int64Value.newBuilder().setValue(depositAccountDetailResponse.getLastPaidInCnt()).build())
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(depositAccountDetailResponse.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(depositAccountDetailResponse.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return ListBankDepositAccountDetailsResponse.newBuilder()
        .addAllDepositAccountDetails(bankDepositAccountDetails)
        .build();
  }
}

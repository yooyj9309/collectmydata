package com.banksalad.collectmydata.bank.publishment.invest.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankInvestAccountDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountDetailsResponse;
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
public class InvestAccountDetailsProtoResponse {

  List<InvestAccountDetailResponse> investAccountDetailResponses;

  public ListBankInvestAccountDetailsResponse toListBankInvestAccountDetailsResponse() {
    List<BankInvestAccountDetail> bankInvestAccountDetails = investAccountDetailResponses.stream()
        .map(investAccountDetailResponse -> BankInvestAccountDetail.newBuilder()
            .setAccountNum(investAccountDetailResponse.getAccountNum())
            .setSeqno(StringValue.newBuilder().setValue(investAccountDetailResponse.getSeqno()).build())
            .setCurrencyCode(investAccountDetailResponse.getCurrencyCode())
            .setBalanceAmt3F(investAccountDetailResponse.getBalanceAmt().longValue())
            .setEvalAmt3F(investAccountDetailResponse.getEvalAmt().longValue())
            .setInvPrincipal3F(investAccountDetailResponse.getInvPrincipal().longValue())
            .setFundNum3F(
                Int64Value.newBuilder().setValue(investAccountDetailResponse.getInvPrincipal().longValue()).build())
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(investAccountDetailResponse.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(investAccountDetailResponse.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return ListBankInvestAccountDetailsResponse.newBuilder()
        .addAllInvestAccountDetails(bankInvestAccountDetails)
        .build();
  }
}

package com.banksalad.collectmydata.bank.publishment.invest.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankInvestAccountBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountBasicsResponse;
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
public class InvestAccountBasicsProtoResponse {

  List<InvestAccountBasicResponse> investAccountBasicResponses;

  public ListBankInvestAccountBasicsResponse toListBankInvestAccountBasicsResponse() {
    List<BankInvestAccountBasic> bankInvestAccountBasics = investAccountBasicResponses.stream()
        .map(investAccountBasicResponse -> BankInvestAccountBasic.newBuilder()
            .setAccountNum(investAccountBasicResponse.getAccountNum())
            .setSeqno(StringValue.newBuilder().setValue(investAccountBasicResponse.getSeqno()).build())
            .setStandardFundCode(investAccountBasicResponse.getStandardFundCode())
            .setPaidInType(investAccountBasicResponse.getPaidInType())
            .setIssueDate(investAccountBasicResponse.getIssueDate())
            .setExpDate(StringValue.newBuilder().setValue(investAccountBasicResponse.getExpDate()).build())
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(investAccountBasicResponse.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(investAccountBasicResponse.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return ListBankInvestAccountBasicsResponse.newBuilder()
        .addAllInvestAccountBasics(bankInvestAccountBasics)
        .build();
  }
}

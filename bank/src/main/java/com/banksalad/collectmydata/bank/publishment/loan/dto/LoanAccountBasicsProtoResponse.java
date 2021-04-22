package com.banksalad.collectmydata.bank.publishment.loan.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankLoanAccountBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankLoanAccountBasicsResponse;
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
public class LoanAccountBasicsProtoResponse {

  List<LoanAccountBasicResponse> loanAccountBasicResponses;

  public ListBankLoanAccountBasicsResponse toListBankLoanAccountBasicsResponse() {
    List<BankLoanAccountBasic> bankLoanAccountBasics = loanAccountBasicResponses.stream()
        .map(loanAccountBasicResponse -> BankLoanAccountBasic.newBuilder()
            .setAccountNum(loanAccountBasicResponse.getAccountNum())
            .setSeqno(StringValue.newBuilder().setValue(loanAccountBasicResponse.getSeqno()).build())
            .setHolderName(loanAccountBasicResponse.getHolderName())
            .setExpDate(loanAccountBasicResponse.getExpDate())
            .setLastOfferedRate3F(loanAccountBasicResponse.getLastOfferedRate().longValue())
            .setRepayDate(StringValue.newBuilder().setValue(loanAccountBasicResponse.getRepayDate()).build())
            .setRepayMethod(loanAccountBasicResponse.getRepayMethod())
            .setRepayOrgCode(StringValue.newBuilder().setValue(loanAccountBasicResponse.getRepayOrgCode()).build())
            .setRepayAccountNum(
                StringValue.newBuilder().setValue(loanAccountBasicResponse.getRepayAccountNum()).build())
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(loanAccountBasicResponse.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(loanAccountBasicResponse.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return ListBankLoanAccountBasicsResponse.newBuilder()
        .addAllLoanAccountBasics(bankLoanAccountBasics)
        .build();
  }
}

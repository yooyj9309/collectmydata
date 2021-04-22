package com.banksalad.collectmydata.bank.publishment.loan.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankLoanAccountDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankLoanAccountDetailsResponse;
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
public class LoanAccountDetailsProtoResponse {

  List<LoanAccountDetailResponse> loanAccountDetailResponses;

  public ListBankLoanAccountDetailsResponse toListBankLoanAccountDetailsResponse() {
    List<BankLoanAccountDetail> bankLoanAccountDetails = loanAccountDetailResponses.stream()
        .map(loanAccountDetailResponse -> BankLoanAccountDetail.newBuilder()
            .setAccountNum(loanAccountDetailResponse.getAccountNum())
            .setSeqno(StringValue.newBuilder().setValue(loanAccountDetailResponse.getSeqno()).build())
            .setBalanceAmt3F(loanAccountDetailResponse.getBalanceAmt().longValue())
            .setLoanPrincipal3F(loanAccountDetailResponse.getLoanPrincipal().longValue())
            .setNextRepayDate(loanAccountDetailResponse.getNextRepayDate())
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(loanAccountDetailResponse.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(loanAccountDetailResponse.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return ListBankLoanAccountDetailsResponse.newBuilder()
        .addAllLoanAccountDetails(bankLoanAccountDetails)
        .build();
  }
}

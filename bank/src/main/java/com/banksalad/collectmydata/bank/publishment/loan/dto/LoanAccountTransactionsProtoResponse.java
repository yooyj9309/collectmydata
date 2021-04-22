package com.banksalad.collectmydata.bank.publishment.loan.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankLoanAccountTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankLoanAccountTransactionInterest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankLoanAccountTransactionsResponse;
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
public class LoanAccountTransactionsProtoResponse {

  List<LoanAccountTransactionResponse> loanAccountTransactionResponses;

  public ListBankLoanAccountTransactionsResponse toListLoanAccountTransactionsResponse() {
    List<BankLoanAccountTransaction> bankLoanAccountTransactions = loanAccountTransactionResponses.stream()
        .map(loanAccountTransactionResponse -> BankLoanAccountTransaction.newBuilder()
            .setAccountNum(loanAccountTransactionResponse.getAccountNum())
            .setSeqno(StringValue.newBuilder().setValue(loanAccountTransactionResponse.getSeqno()).build())
            .setTransDtime(loanAccountTransactionResponse.getTransDtime())
            .setTransNo(StringValue.newBuilder().setValue(loanAccountTransactionResponse.getTransNo()).build())
            .setTransType(loanAccountTransactionResponse.getTransType())
            .setTransAmt3F(loanAccountTransactionResponse.getTransAmt().longValue())
            .setBalanceAmt3F(loanAccountTransactionResponse.getBalanceAmt().longValue())
            .setPrincipalAmt3F(loanAccountTransactionResponse.getPrincipalAmt().longValue())
            .setIntAmt3F(loanAccountTransactionResponse.getIntAmt().longValue())
            .addAllLoanAccountTransactionInterests(toBankLoanAccountTransactionInterests(
                loanAccountTransactionResponse.getLoanAccountTransactionInterests()))
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(loanAccountTransactionResponse.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(loanAccountTransactionResponse.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return ListBankLoanAccountTransactionsResponse.newBuilder()
        .addAllLoanAccountTransactions(bankLoanAccountTransactions)
        .build();
  }

  private List<BankLoanAccountTransactionInterest> toBankLoanAccountTransactionInterests(
      List<LoanAccountTransactionInterest> loanAccountTransactionInterests) {
    return loanAccountTransactionInterests.stream()
        .map(loanAccountTransactionInterest -> BankLoanAccountTransactionInterest.newBuilder()
            .setIntStartDate(loanAccountTransactionInterest.getIntStartDate())
            .setIntEndDate(loanAccountTransactionInterest.getIntEndDate())
            .setIntRate3F(loanAccountTransactionInterest.getIntRate().longValue())
            .setIntType(loanAccountTransactionInterest.getIntType())
            .build())
        .collect(Collectors.toList());
  }
}

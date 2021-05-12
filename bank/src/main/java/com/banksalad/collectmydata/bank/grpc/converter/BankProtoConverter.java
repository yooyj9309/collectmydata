package com.banksalad.collectmydata.bank.grpc.converter;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountTransactionResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountTransactionResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountTransactionInterest;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountTransactionResponse;
import com.banksalad.collectmydata.bank.publishment.summary.dto.AccountSummaryResponse;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankAccountSummary;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankDepositAccountBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankDepositAccountDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankDepositAccountTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankInvestAccountBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankInvestAccountDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankInvestAccountTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankLoanAccountBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankLoanAccountDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankLoanAccountTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankLoanAccountTransactionInterest;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;

import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.common.grpc.converter.ProtoTypeConverter.toInt64ValueMultiply1000;
import static com.banksalad.collectmydata.common.grpc.converter.ProtoTypeConverter.toInt64ValueMultiply100000;

@Component
public class BankProtoConverter {

  public BankAccountSummary toBankAccountSummary(AccountSummaryResponse summaryResponse) {
    BankAccountSummary.Builder builder = BankAccountSummary.newBuilder();

    if (summaryResponse.getSeqno() != null) {
      builder.setSeqno(StringValue.newBuilder().setValue(summaryResponse.getSeqno()).build());
    }

    if (summaryResponse.getForeignDeposit() != null) {
      builder.setIsForeignDeposit(BoolValue.newBuilder().setValue(summaryResponse.getForeignDeposit()).build());
    }

    return builder.setAccountNum(summaryResponse.getAccountNum())
        .setIsConsent(summaryResponse.isConsent())
        .setProdName(summaryResponse.getProdName())
        .setAccountType(summaryResponse.getAccountType())
        .setAccountStatus(summaryResponse.getAccountStatus())
        .setCreatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(summaryResponse.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(summaryResponse.getUpdatedAt()))
        .build();
  }

  public BankDepositAccountBasic toBankDepositAccountBasic(DepositAccountBasicResponse basicResponse) {
    BankDepositAccountBasic.Builder builder = BankDepositAccountBasic.newBuilder();

    if (basicResponse.getSeqno() != null) {
      builder.setSeqno(StringValue.newBuilder().setValue(basicResponse.getSeqno()).build());
    }

    if (basicResponse.getExpDate() != null) {
      builder.setExpDate(StringValue.newBuilder().setValue(basicResponse.getExpDate()).build());
    }

    if (basicResponse.getCommitAmt() != null) {
      builder.setCommitAmt3F(toInt64ValueMultiply1000(basicResponse.getCommitAmt()));
    }

    if (basicResponse.getMonthlyPaidInAmt() != null) {
      builder.setMonthlyPaidInAmt3F(toInt64ValueMultiply1000(basicResponse.getMonthlyPaidInAmt()));
    }

    return builder.setAccountNum(basicResponse.getAccountNum())
        .setCurrencyCode(basicResponse.getCurrencyCode())
        .setSavingMethod(basicResponse.getSavingMethod())
        .setHolderName(basicResponse.getHolderName())
        .setIssueDate(basicResponse.getIssueDate())
        .setCreatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(basicResponse.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(basicResponse.getUpdatedAt()))
        .build();
  }

  public BankDepositAccountDetail toBankDepositAccountDetail(DepositAccountDetailResponse detailResponse) {
    BankDepositAccountDetail.Builder builder = BankDepositAccountDetail.newBuilder();

    if (detailResponse.getSeqno() != null) {
      builder.setSeqno(StringValue.newBuilder().setValue(detailResponse.getSeqno()).build());
    }

    if (detailResponse.getLastPaidInCnt() != null) {
      builder.setLastPaidInCnt(Int32Value.newBuilder().setValue(detailResponse.getLastPaidInCnt()).build());
    }

    return builder.setAccountNum(detailResponse.getAccountNum())
        .setCurrencyCode(detailResponse.getCurrencyCode())
        .setBalanceAmt3F(toInt64ValueMultiply1000(detailResponse.getBalanceAmt()).getValue())
        .setWithdrawableAmt3F(toInt64ValueMultiply1000(detailResponse.getWithdrawableAmt()).getValue())
        .setOfferedRate5F(toInt64ValueMultiply100000(detailResponse.getOfferedRate()).getValue())
        .setCreatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(detailResponse.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(detailResponse.getUpdatedAt()))
        .build();
  }

  public BankDepositAccountTransaction toBankDepositAccountTransaction(
      DepositAccountTransactionResponse transactionResponse) {
    BankDepositAccountTransaction.Builder builder = BankDepositAccountTransaction.newBuilder();

    if (transactionResponse.getSeqno() != null) {
      builder.setSeqno(StringValue.newBuilder().setValue(transactionResponse.getSeqno()).build());
    }

    if (transactionResponse.getTransNo() != null) {
      builder.setTransNo(StringValue.newBuilder().setValue(transactionResponse.getTransNo()).build());
    }

    if (transactionResponse.getPaidInCnt() != null) {
      builder.setPaidInCnt(Int32Value.newBuilder().setValue(transactionResponse.getPaidInCnt()).build());
    }

    return builder.setAccountNum(transactionResponse.getAccountNum())
        .setCurrencyCode(transactionResponse.getCurrencyCode())
        .setTransDtime(transactionResponse.getTransDtime())
        .setTransType(transactionResponse.getTransType())
        .setTransClass(transactionResponse.getTransClass())
        .setTransAmt3F(toInt64ValueMultiply1000(transactionResponse.getTransAmt()).getValue())
        .setBalanceAmt3F(toInt64ValueMultiply1000(transactionResponse.getBalanceAmt()).getValue())
        .setCreatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(transactionResponse.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(transactionResponse.getUpdatedAt()))
        .build();
  }

  public BankInvestAccountBasic toBankInvestAccountBasic(InvestAccountBasicResponse basicResponse) {
    BankInvestAccountBasic.Builder builder = BankInvestAccountBasic.newBuilder();

    if (basicResponse.getSeqno() != null) {
      builder.setSeqno(StringValue.newBuilder().setValue(basicResponse.getSeqno()).build());
    }

    if (basicResponse.getExpDate() != null) {
      builder.setExpDate(StringValue.newBuilder().setValue(basicResponse.getExpDate()).build());
    }

    return builder.setAccountNum(basicResponse.getAccountNum())
        .setStandardFundCode(basicResponse.getStandardFundCode())
        .setPaidInType(basicResponse.getPaidInType())
        .setIssueDate(basicResponse.getIssueDate())
        .setCreatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(basicResponse.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(basicResponse.getUpdatedAt()))
        .build();
  }

  public BankInvestAccountDetail toBankInvestAccountDetail(InvestAccountDetailResponse detailResponse) {
    BankInvestAccountDetail.Builder builder = BankInvestAccountDetail.newBuilder();

    if (detailResponse.getSeqno() != null) {
      builder.setSeqno(StringValue.newBuilder().setValue(detailResponse.getSeqno()).build());
    }

    if (detailResponse.getFundNum() != null) {
      builder.setFundNum3F(toInt64ValueMultiply1000(detailResponse.getFundNum()));
    }

    return builder.setAccountNum(detailResponse.getAccountNum())
        .setCurrencyCode(detailResponse.getCurrencyCode())
        .setBalanceAmt3F(toInt64ValueMultiply1000(detailResponse.getBalanceAmt()).getValue())
        .setEvalAmt3F(toInt64ValueMultiply1000(detailResponse.getEvalAmt()).getValue())
        .setInvPrincipal3F(toInt64ValueMultiply1000(detailResponse.getInvPrincipal()).getValue())
        .setCreatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(detailResponse.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(detailResponse.getUpdatedAt()))
        .build();
  }

  public BankInvestAccountTransaction toBankInvestAccountTransaction(InvestAccountTransactionResponse transactionResponse) {
    BankInvestAccountTransaction.Builder builder = BankInvestAccountTransaction.newBuilder();

    if (transactionResponse.getSeqno() != null) {
      builder.setSeqno(StringValue.newBuilder().setValue(transactionResponse.getSeqno()).build());
    }

    if (transactionResponse.getTransNo() != null) {
      builder.setTransNo(StringValue.newBuilder().setValue(transactionResponse.getTransNo()).build());
    }

    if (transactionResponse.getBaseAmt() != null) {
      builder.setBaseAmt3F(toInt64ValueMultiply1000(transactionResponse.getBaseAmt()));
    }

    if (transactionResponse.getTransFundNum() != null) {
      builder.setTransFundNum3F(toInt64ValueMultiply1000(transactionResponse.getTransFundNum()));
    }

    return builder.setAccountNum(transactionResponse.getAccountNum())
        .setCurrencyCode(transactionResponse.getCurrencyCode())
        .setTransDtime(transactionResponse.getTransDtime())
        .setTransType(transactionResponse.getTransType())
        .setTransAmt3F(toInt64ValueMultiply1000(transactionResponse.getTransAmt()).getValue())
        .setBalanceAmt3F(toInt64ValueMultiply1000(transactionResponse.getBalanceAmt()).getValue())
        .setCreatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(transactionResponse.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(transactionResponse.getUpdatedAt()))
        .build();
  }

  public BankLoanAccountBasic toBankLoanAccountBasic(LoanAccountBasicResponse basicResponse) {
    BankLoanAccountBasic.Builder builder = BankLoanAccountBasic.newBuilder();

    if (basicResponse.getSeqno() != null) {
      builder.setSeqno(StringValue.newBuilder().setValue(basicResponse.getSeqno()).build());
    }

    if (basicResponse.getRepayDate() != null) {
      builder.setRepayDate(StringValue.newBuilder().setValue(basicResponse.getRepayDate()).build());
    }

    if (basicResponse.getRepayOrgCode() != null) {
      builder.setRepayOrgCode(StringValue.newBuilder().setValue(basicResponse.getRepayOrgCode()).build());
    }

    if (basicResponse.getRepayAccountNum() != null) {
      builder.setRepayAccountNum(StringValue.newBuilder().setValue(basicResponse.getRepayAccountNum()).build());
    }

    return builder.setAccountNum(basicResponse.getAccountNum())
        .setHolderName(basicResponse.getHolderName())
        .setExpDate(basicResponse.getExpDate())
        .setLastOfferedRate3F(toInt64ValueMultiply1000(basicResponse.getLastOfferedRate()).getValue())
        .setRepayMethod(basicResponse.getRepayMethod())
        .setCreatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(basicResponse.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(basicResponse.getUpdatedAt()))
        .build();
  }

  public BankLoanAccountDetail toBankLoanAccountDetail(LoanAccountDetailResponse detailResponse) {
    BankLoanAccountDetail.Builder builder = BankLoanAccountDetail.newBuilder();

    if (detailResponse.getSeqno() != null) {
      builder.setSeqno(StringValue.newBuilder().setValue(detailResponse.getSeqno()).build());
    }

    return builder.setAccountNum(detailResponse.getAccountNum())
        .setBalanceAmt3F(toInt64ValueMultiply1000(detailResponse.getBalanceAmt()).getValue())
        .setLoanPrincipal3F(toInt64ValueMultiply1000(detailResponse.getLoanPrincipal()).getValue())
        .setNextRepayDate(detailResponse.getNextRepayDate())
        .setCreatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(detailResponse.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(detailResponse.getUpdatedAt()))
        .build();
  }

  public BankLoanAccountTransaction toBankLoanAccountTransaction(LoanAccountTransactionResponse transactionResponse) {
    BankLoanAccountTransaction.Builder builder = BankLoanAccountTransaction.newBuilder();

    if (transactionResponse.getSeqno() != null) {
      builder.setSeqno(StringValue.newBuilder().setValue(transactionResponse.getSeqno()).build());
    }

    if (transactionResponse.getTransNo() != null) {
      builder.setTransNo(StringValue.newBuilder().setValue(transactionResponse.getTransNo()).build());
    }

    return builder.setAccountNum(transactionResponse.getAccountNum())
        .setTransDtime(transactionResponse.getTransDtime())
        .setTransType(transactionResponse.getTransType())
        .setTransAmt3F(toInt64ValueMultiply1000(transactionResponse.getTransAmt()).getValue())
        .setBalanceAmt3F(toInt64ValueMultiply1000(transactionResponse.getBalanceAmt()).getValue())
        .setPrincipalAmt3F(toInt64ValueMultiply1000(transactionResponse.getPrincipalAmt()).getValue())
        .setIntAmt3F(toInt64ValueMultiply1000(transactionResponse.getIntAmt()).getValue())
        .addAllLoanAccountTransactionInterests(
            toBankLoanAccountTransactionInterests(transactionResponse.getLoanAccountTransactionInterests()))
        .setCreatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(transactionResponse.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(transactionResponse.getUpdatedAt()))
        .build();
  }

  private List<BankLoanAccountTransactionInterest> toBankLoanAccountTransactionInterests(
      List<LoanAccountTransactionInterest> loanAccountTransactionInterests) {
    return loanAccountTransactionInterests.stream()
        .map(loanAccountTransactionInterest -> BankLoanAccountTransactionInterest.newBuilder()
            .setIntStartDate(loanAccountTransactionInterest.getIntStartDate())
            .setIntEndDate(loanAccountTransactionInterest.getIntEndDate())
            .setIntRate3F(toInt64ValueMultiply1000(loanAccountTransactionInterest.getIntRate()).getValue())
            .setIntType(loanAccountTransactionInterest.getIntType())
            .build())
        .collect(Collectors.toList());
  }
}

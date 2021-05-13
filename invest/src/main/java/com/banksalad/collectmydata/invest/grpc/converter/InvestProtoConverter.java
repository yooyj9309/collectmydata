package com.banksalad.collectmydata.invest.grpc.converter;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountProductResponse;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountTransactionResponse;
import com.banksalad.collectmydata.invest.publishment.summary.dto.AccountSummaryResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.InvestAccountBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.InvestAccountProduct;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.InvestAccountSummary;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.InvestAccountTransaction;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;

@Component
public class InvestProtoConverter {

  public InvestAccountSummary toInvestAccountSummary(AccountSummaryResponse summary) {
    return InvestAccountSummary.newBuilder()
        .setAccountNum(summary.getAccountNum())
        .setIsConsent(summary.getConsent())
        .setAccountName(summary.getAccountName())
        .setAccountType(summary.getAccountType())
        .setAccountStatus(summary.getAccountStatus())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(summary.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(summary.getUpdatedAt()))
        .build();
  }

  public InvestAccountBasic toInvestAccountBasic(AccountBasicResponse basic) {
    return InvestAccountBasic.newBuilder()
        .setAccountNum(basic.getAccountNum())
        .setIssueDate(basic.getIssueDate())
        .setIsTaxBenefits(basic.getTaxBenefits())
        .setWithholdingsAmt3F(NumberUtil.multiply1000(basic.getWithholdingsAmt()))
        .setCreditLoanAmt3F(NumberUtil.multiply1000(basic.getCreditLoanAmt()))
        .setMortgageAmt3F(NumberUtil.multiply1000(basic.getMortgageAmt()))
        .setCurrencyCode(basic.getCurrencyCode())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(basic.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(basic.getUpdatedAt()))
        .build();
  }

  public InvestAccountTransaction toInvestAccountTransaction(AccountTransactionResponse transaction) {
    InvestAccountTransaction.Builder builder = InvestAccountTransaction.newBuilder();

    builder.setAccountNum(transaction.getAccountNum())
        .setProdCode(transaction.getProdCode())
        .setTransDtime(transaction.getTransDtime())
        .setProdName(transaction.getProdName())
        .setTransType(transaction.getTransType());

    if (!transaction.getTransTypeDetail().isBlank()) {
      builder.setTransTypeDetail(StringValue.newBuilder().setValue(transaction.getTransTypeDetail()).build());
    }

    builder.setTransNum(transaction.getTransNum())
        .setBaseAmt4F(NumberUtil.multiply10000(transaction.getBalanceAmt()))
        .setTransAmt3F(NumberUtil.multiply1000(transaction.getTransAmt()))
        .setSettleAmt3F(NumberUtil.multiply1000(transaction.getSettleAmt()))
        .setBalanceAmt3F(NumberUtil.multiply1000(transaction.getBalanceAmt()))
        .setCurrencyCode(transaction.getCurrencyCode())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(transaction.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(transaction.getUpdatedAt()));

    return builder.build();
  }

  public InvestAccountProduct toInvestAccountProduct(AccountProductResponse product) {
    InvestAccountProduct.Builder builder = InvestAccountProduct.newBuilder();

    builder.setAccountNum(product.getAccountNum())
        .setProdCode(product.getProdCode())
        .setProdTypeDetail(product.getProdTypeDetail())
        .setProdName(product.getProdName());

    if (product.getPurchaseAmt() != null) {
      builder.setPurchaseAmt3F(Int64Value.newBuilder()
          .setValue(NumberUtil.multiply1000(product.getPurchaseAmt()))
          .build());
    }

    if (product.getHoldingNum() != null) {
      builder.setHoldingNum(Int64Value.newBuilder()
          .setValue(product.getHoldingNum())
          .build());
    }

    if (product.getAvailForSaleNum() != null) {
      builder.setAvailForSaleNum(Int64Value.newBuilder()
          .setValue(product.getAvailForSaleNum())
          .build());
    }

    if (product.getEvalAmt() != null) {
      builder.setEvalAmt3F(Int64Value.newBuilder()
          .setValue(NumberUtil.multiply1000(product.getEvalAmt()))
          .build());
    }

    if (!product.getIssueDate().isBlank()) {
      builder.setIssueDate(StringValue.newBuilder().setValue(product.getIssueDate()).build());
    }

    if (product.getPaidInAmt() != null) {
      builder.setPaidInAmt3F(Int64Value.newBuilder()
          .setValue(NumberUtil.multiply1000(product.getPaidInAmt()))
          .build());
    }

    if (product.getWithdrawalAmt() != null) {
      builder.setWithdrawalAmt3F(Int64Value.newBuilder()
          .setValue(NumberUtil.multiply1000(product.getWithdrawalAmt()))
          .build());
    }

    if (!product.getLastPaidInDate().isBlank()) {
      builder.setLastPaidInDate(StringValue.newBuilder().setValue(product.getLastPaidInDate()).build());
    }

    if (product.getRcvAmt() != null) {
      builder.setRcvAmt3F(Int64Value.newBuilder()
          .setValue(NumberUtil.multiply1000(product.getRcvAmt()))
          .build());
    }

    builder.setCurrencyCode(product.getCurrencyCode())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(product.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(product.getUpdatedAt()));

    return builder.build();
  }
}

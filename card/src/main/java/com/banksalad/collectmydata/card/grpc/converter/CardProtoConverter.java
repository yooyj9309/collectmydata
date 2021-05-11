package com.banksalad.collectmydata.card.grpc.converter;

import com.banksalad.collectmydata.card.publishment.bill.dto.BillBasicPublishment;
import com.banksalad.collectmydata.card.publishment.bill.dto.BillDetailPublishment;
import com.banksalad.collectmydata.card.publishment.transaction.dto.ApprovalDomesticPublishment;
import com.banksalad.collectmydata.card.publishment.transaction.dto.ApprovalOverseasPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanLongTermPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.PaymentPublishment;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardApprovalDomestic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardApprovalOversea;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardBillBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardBillDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardBillDetail.Builder;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardLoanLongTerm;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardPayment;

import static com.banksalad.collectmydata.common.grpc.converter.ProtoTypeConverter.toInt32Value;
import static com.banksalad.collectmydata.common.grpc.converter.ProtoTypeConverter.toInt64ValueMultiply1000;
import static com.banksalad.collectmydata.common.grpc.converter.ProtoTypeConverter.toStringValue;

public class CardProtoConverter {

  // 6.3.4
  public static CardBillBasic toCardBillBasicProto(BillBasicPublishment billBasicPublishment) {

    CardBillBasic.Builder builder = CardBillBasic.newBuilder();

    if (billBasicPublishment.getSeqno() != null) {
      builder.setSeqno(toStringValue(billBasicPublishment.getSeqno()));
    }

    return builder
        .setChargeAmt3F(toInt64ValueMultiply1000(billBasicPublishment.getChargeAmt()).getValue())
        .setChargeDay(String.valueOf(billBasicPublishment.getChargeDay()))
        .setChargeMonth(String.valueOf(billBasicPublishment.getChargeMonth()))
        .setPaidOutDate(billBasicPublishment.getPaidOutDate())
        .setCardType(billBasicPublishment.getCardType())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(billBasicPublishment.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(billBasicPublishment.getUpdatedAt()))
        .build();
  }

  // 6.3.5
  public static CardBillDetail toCardBillDetailProto(BillDetailPublishment billDetailPublishment) {

    Builder builder = CardBillDetail.newBuilder();

    if (billDetailPublishment.getSeqno() != null) {
      builder.setSeqno(toStringValue(billDetailPublishment.getSeqno()));
    }

    if (billDetailPublishment.getTotalInstallCnt() != null) {
      builder.setTotalInstallCnt(toInt32Value(billDetailPublishment.getTotalInstallCnt()));
    }

    if (billDetailPublishment.getCurInstallCnt() != null) {
      builder.setCurInstallCnt(toInt32Value(billDetailPublishment.getCurInstallCnt()));
    }

    if (billDetailPublishment.getBalanceAmt() != null) {
      builder.setBalanceAmt3F(toInt64ValueMultiply1000(billDetailPublishment.getBalanceAmt()));
    }

    return builder
        .setChargeMonth(billDetailPublishment.getChargeMonth().toString())
        .setCardId(billDetailPublishment.getCardId())
        .setPaidDtime(billDetailPublishment.getPaidDtime())
        .setCurrencyCode(billDetailPublishment.getCurrencyCode())
        .setMerchantName(billDetailPublishment.getMerchantName())
        .setCreditFeeAmt3F(
            toInt64ValueMultiply1000(billDetailPublishment.getCreditFeeAmt()).getValue())
        .setProdType(billDetailPublishment.getProdType())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(billDetailPublishment.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(billDetailPublishment.getUpdatedAt())).build();
  }

  // 6.3.6
  public static CardPayment toCardPaymentProto(PaymentPublishment paymentPublishment) {

    CardPayment.Builder builder = CardPayment.newBuilder();

    if (paymentPublishment.getSeqno() != null) {
      builder.setSeqno(toStringValue(paymentPublishment.getSeqno()));
    }

    return builder
        .setPayDueDate(paymentPublishment.getPayDueDate())
        .setPayAmt3F(toInt64ValueMultiply1000(paymentPublishment.getPayAmt()).getValue())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(paymentPublishment.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(paymentPublishment.getUpdatedAt())).build();
  }

  // 6.3.7
  public static CardApprovalDomestic toCardApprovalDomesticProto(
      ApprovalDomesticPublishment approvalDomesticPublishment) {
    CardApprovalDomestic.Builder builder = CardApprovalDomestic.newBuilder();

    if (approvalDomesticPublishment.getCancelDtime() != null) {
      builder.setCancelDtime(toStringValue(approvalDomesticPublishment.getCancelDtime()));
    }

    if (approvalDomesticPublishment.getTotalInstallCnt() != null) {
      builder.setTotalInstallCnt(toInt32Value(approvalDomesticPublishment.getTotalInstallCnt()));
    }

    return builder.setCardId(approvalDomesticPublishment.getCardId())
        .setApprovedNum(approvalDomesticPublishment.getApprovedNum())
        .setStatus(approvalDomesticPublishment.getStatus())
        .setPayType(approvalDomesticPublishment.getPayType())
        .setApprovedDtime(approvalDomesticPublishment.getApprovedDtime())
        .setMerchantName(approvalDomesticPublishment.getMerchantName())
        .setApprovedAmt3F(toInt64ValueMultiply1000(approvalDomesticPublishment.getApprovedAmt()).getValue())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(approvalDomesticPublishment.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(approvalDomesticPublishment.getCreatedAt()))
        .build();
  }

  // 6.3.8
  public static CardApprovalOversea toCardApprovalOverseaProto(
      ApprovalOverseasPublishment approvalOverseasPublishment) {
    CardApprovalOversea.Builder builder = CardApprovalOversea.newBuilder();

    if (approvalOverseasPublishment.getCancelDtime() != null) {
      builder.setCancelDtime(toStringValue(approvalOverseasPublishment.getCancelDtime()));
    }

    if (approvalOverseasPublishment.getKrwAmt() != null) {
      builder.setKrwAmt3F(toInt64ValueMultiply1000(approvalOverseasPublishment.getKrwAmt()));
    }

    return builder.setCardId(approvalOverseasPublishment.getCardId())
        .setApprovedNum(approvalOverseasPublishment.getApprovedNum())
        .setStatus(approvalOverseasPublishment.getStatus())
        .setPayType(approvalOverseasPublishment.getPayType())
        .setApprovedDtime(approvalOverseasPublishment.getApprovedNum())
        .setMerchantName(approvalOverseasPublishment.getMerchantName())
        .setApprovedAmt3F(toInt64ValueMultiply1000(approvalOverseasPublishment.getApprovedAmt()).getValue())
        .setCountryCode(approvalOverseasPublishment.getCountryCode())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(approvalOverseasPublishment.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(approvalOverseasPublishment.getUpdatedAt()))
        .build();
  }

  // 6.3.12
  public static CardLoanLongTerm toCardLoanLongTermProto(LoanLongTermPublishment loanLongTermPublishment) {

    CardLoanLongTerm.Builder builder = CardLoanLongTerm.newBuilder();

    if (loanLongTermPublishment.getLoanCnt() != null) {
      builder.setLoanCnt(toInt32Value(loanLongTermPublishment.getLoanCnt()));
    }

    return builder
        .setLoanLongTermNo(loanLongTermPublishment.getLoanLongTermNo())
        .setLoanDtime(loanLongTermPublishment.getLoanDtime())
        .setLoanType(loanLongTermPublishment.getLoanType())
        .setLoanName(loanLongTermPublishment.getLoanName())
        .setLoanAmt3F(toInt64ValueMultiply1000(loanLongTermPublishment.getLoanAmt()).getValue())
        .setIntRate3F(toInt64ValueMultiply1000(loanLongTermPublishment.getIntRate()).getValue())
        .setExpDate(loanLongTermPublishment.getExpDate())
        .setBalanceAmt3F(toInt64ValueMultiply1000(loanLongTermPublishment.getBalanceAmt()).getValue())
        .setRepayMethod(loanLongTermPublishment.getRepayMethod())
        .setIntAmt3F(toInt64ValueMultiply1000(loanLongTermPublishment.getIntAmt()).getValue())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(loanLongTermPublishment.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(loanLongTermPublishment.getUpdatedAt()))
        .build();
  }
}

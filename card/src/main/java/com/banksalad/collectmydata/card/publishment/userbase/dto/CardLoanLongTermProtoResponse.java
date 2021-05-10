package com.banksalad.collectmydata.card.publishment.userbase.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardLoanLongTerm;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardLoanLongTermsResponse;
import com.google.protobuf.Int32Value;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.common.grpc.converter.ProtoTypeConverter.toInt64ValueMultiply1000;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CardLoanLongTermProtoResponse {

  List<LoanLongTermPublishment> loanLongTermPublishments;

  public ListCardLoanLongTermsResponse toListCardLoanLongTermsResponseProto() {

    List<CardLoanLongTerm> cardLoanLongTerms = loanLongTermPublishments.stream().map(loanLongTermPublishment -> CardLoanLongTerm.newBuilder()
        .setLoanLongTermNo(loanLongTermPublishment.getLoanLongTermNo())
        .setLoanDtime(loanLongTermPublishment.getLoanDtime())
        .setLoanCnt(Int32Value.of(loanLongTermPublishment.getLoanCnt()))
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
        .build()).collect(Collectors.toList());

    return ListCardLoanLongTermsResponse.newBuilder()
        .addAllCardLoanLongTerms(cardLoanLongTerms).build();
  }
}

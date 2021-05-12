package com.banksalad.collectmydata.card.publishment.userbase.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardLoanShortTerm;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardLoanShortTermsResponse;
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
public class CardLoanShortTermProtoResponse {

  List<LoanShortTermPublishment> loanShortTermPublishments;

  public ListCardLoanShortTermsResponse toListCardLoanShortTermsResponseProto() {

    List<CardLoanShortTerm> cardLoanShortTermsProtos = loanShortTermPublishments.stream()
        .map(loanShortTermPublishment -> CardLoanShortTerm.newBuilder()
            .setLoanShortTermNo(loanShortTermPublishment.getLoanShortTermNo())
            .setLoanDtime(loanShortTermPublishment.getLoanDtime())
            .setLoanAmt3F(toInt64ValueMultiply1000(loanShortTermPublishment.getLoanAmt()).getValue())
            .setPayDueDate(loanShortTermPublishment.getPayDueDate())
            .setIntRate3F(toInt64ValueMultiply1000(loanShortTermPublishment.getIntRate()).getValue())
            .setCreatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(loanShortTermPublishment.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(loanShortTermPublishment.getUpdatedAt()))
            .build()).collect(Collectors.toList());

    return ListCardLoanShortTermsResponse.newBuilder()
        .addAllCardLoanShortTerms(cardLoanShortTermsProtos).build();
  }
}

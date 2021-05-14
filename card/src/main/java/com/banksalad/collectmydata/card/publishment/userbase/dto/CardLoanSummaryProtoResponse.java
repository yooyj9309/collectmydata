package com.banksalad.collectmydata.card.publishment.userbase.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardLoanSummary;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardLoanSummariesResponse;
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
public class CardLoanSummaryProtoResponse {

  List<LoanSummaryPublishment> loanSummaryPublishments;

  public ListCardLoanSummariesResponse toListCardLoanSummariesResponseProto() {

    List<CardLoanSummary> cardLoanSummaries = loanSummaryPublishments.stream().map(loanSummaryPublishment -> CardLoanSummary.newBuilder()
        .setIsLoanRevolving(loanSummaryPublishment.isLoanRevolving())
        .setIsLoanShortTerm(loanSummaryPublishment.isLoanShortTerm())
        .setIsLoanLongTerm(loanSummaryPublishment.isLoanLongTerm())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(loanSummaryPublishment.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(loanSummaryPublishment.getUpdatedAt()))
        .build()).collect(Collectors.toList());

    return ListCardLoanSummariesResponse.newBuilder()
        .addAllCardLoanSummaries(cardLoanSummaries).build();
  }
}

package com.banksalad.collectmydata.card.publishment.userbase.dto;

import com.banksalad.collectmydata.card.grpc.converter.CardProtoConverter;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardLoanLongTerm;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardLoanLongTermsResponse;
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
public class CardLoanLongTermProtoResponse {

  List<LoanLongTermPublishment> loanLongTermPublishments;

  public ListCardLoanLongTermsResponse toListCardLoanLongTermsResponseProto() {

    List<CardLoanLongTerm> cardLoanLongTerms = loanLongTermPublishments.stream()
        .map(CardProtoConverter::toCardLoanLongTermProto).collect(Collectors.toList());

    return ListCardLoanLongTermsResponse.newBuilder()
        .addAllCardLoanLongTerms(cardLoanLongTerms).build();
  }
}

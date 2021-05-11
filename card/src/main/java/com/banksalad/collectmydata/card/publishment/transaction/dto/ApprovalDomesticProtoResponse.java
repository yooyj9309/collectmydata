package com.banksalad.collectmydata.card.publishment.transaction.dto;

import com.banksalad.collectmydata.card.grpc.converter.CardProtoConverter;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardApprovalDomestic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardApprovalDomesticsResponse;
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
public class ApprovalDomesticProtoResponse {

  List<ApprovalDomesticPublishment> approvalDomesticPublishments;

  public ListCardApprovalDomesticsResponse toListCardApprovalDomesticsResponseProto() {

    List<CardApprovalDomestic> cardApprovalDomesticProtos = approvalDomesticPublishments.stream()
        .map(CardProtoConverter::toCardApprovalDomesticProto).collect(Collectors.toList());

    return ListCardApprovalDomesticsResponse.newBuilder()
        .addAllCardApprovalDomestics(cardApprovalDomesticProtos).build();
  }
}

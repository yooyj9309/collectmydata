package com.banksalad.collectmydata.card.publishment.transaction.dto;

import com.banksalad.collectmydata.card.grpc.converter.CardProtoConverter;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardApprovalOversea;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardApprovalOverseasResponse;
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
public class CardApprovalOverseaProtoResponse {

  List<ApprovalOverseasPublishment> approvalOverseasPublishments;

  public ListCardApprovalOverseasResponse toListCardApprovalOverseasResponseProto() {

    List<CardApprovalOversea> cardApprovalOverseasProtos = approvalOverseasPublishments.stream()
        .map(CardProtoConverter::toCardApprovalOverseaProto)
        .collect(Collectors.toList());

    return ListCardApprovalOverseasResponse.newBuilder()
        .addAllCardApprovalOverseas(cardApprovalOverseasProtos).build();
  }
}

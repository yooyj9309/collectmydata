package com.banksalad.collectmydata.card.publishment.bill.dto;

import com.banksalad.collectmydata.card.grpc.converter.CardProtoConverter;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardBillDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardBillDetailsResponse;
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
public class CardBillDetailProtoResponse {

  List<BillDetailPublishment> billDetailPublishments;

  public ListCardBillDetailsResponse toListCardBillDetailsResponseProto() {

    List<CardBillDetail> cardBillDetailsProtos = billDetailPublishments.stream()
        .map(CardProtoConverter::toCardBillDetailProto).collect(
            Collectors.toList());

    return ListCardBillDetailsResponse.newBuilder()
        .addAllCardBillDetails(cardBillDetailsProtos).build();
  }
}

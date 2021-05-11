package com.banksalad.collectmydata.card.publishment.bill.dto;

import com.banksalad.collectmydata.card.grpc.converter.CardProtoConverter;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardBillBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardBillBasicsResponse;
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
public class CardBillBasicProtoResponse {

  List<BillBasicPublishment> billBasicPublishments;

  public ListCardBillBasicsResponse toListCardBillBasicsResponseProto() {

    List<CardBillBasic> cardBillBasicsProtos = billBasicPublishments.stream()
        .map(CardProtoConverter::toCardBillBasicProto
        ).collect(Collectors.toList());

    return ListCardBillBasicsResponse.newBuilder()
        .addAllCardBillBasics(cardBillBasicsProtos).build();
  }
}

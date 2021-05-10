package com.banksalad.collectmydata.card.publishment.bill.dto;

import com.banksalad.collectmydata.common.grpc.converter.ProtoTypeConverter;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardBillBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardBillBasicsResponse;
import com.google.protobuf.StringValue;
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

    List<CardBillBasic> cardBillBasicsProtos = billBasicPublishments.stream().map(billBasicPublishment -> CardBillBasic.newBuilder()
        .setSeqno(billBasicPublishment.getSeqno() != null ? StringValue.of(billBasicPublishment.getSeqno()) : null)
        .setChargeAmt3F(ProtoTypeConverter.toInt64ValueMultiply1000(billBasicPublishment.getChargeAmt()).getValue())
        .setChargeDay(String.valueOf(billBasicPublishment.getChargeDay()))
        .setChargeMonth(String.valueOf(billBasicPublishment.getChargeMonth()))
        .setPaidOutDate(billBasicPublishment.getPaidOutDate())
        .setCardType(billBasicPublishment.getCardType())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(billBasicPublishment.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(billBasicPublishment.getUpdatedAt()))
        .build()).collect(Collectors.toList());

    return ListCardBillBasicsResponse.newBuilder()
        .addAllCardBillBasics(cardBillBasicsProtos).build();
  }
}

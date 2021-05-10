package com.banksalad.collectmydata.card.publishment.userbase.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardRevolving;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardRevolvingsResponse;
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
public class CardRevolvingProtoResponse {

  List<RevolvingPublishment> revolvingPublishments;

  public ListCardRevolvingsResponse toListCardRevolvingResponse() {

    List<CardRevolving> cardRevolvings = revolvingPublishments.stream().map(revolvingPublishment -> CardRevolving.newBuilder()
        .setRevolvingNo(revolvingPublishment.getRevolvingNo())
        .setRevolvingMonth(String.valueOf(revolvingPublishment.getRevolvingMonth()))
        .setReqDate(revolvingPublishment.getReqDate())
        .setMinPayRate3F(toInt64ValueMultiply1000(revolvingPublishment.getMinPayRate()).getValue())
        .setMinPayAmt3F(toInt64ValueMultiply1000(revolvingPublishment.getMinPayAmt()).getValue())
        .setAgreedPayRate3F(toInt64ValueMultiply1000(revolvingPublishment.getAgreedPayRate()).getValue())
        .setRemainedAmt3F(toInt64ValueMultiply1000(revolvingPublishment.getRemainedAmt()).getValue())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(revolvingPublishment.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(revolvingPublishment.getUpdatedAt())).build())
        .collect(Collectors.toList());

    return ListCardRevolvingsResponse.newBuilder()
        .addAllCardRevolvings(cardRevolvings).build();
  }
}

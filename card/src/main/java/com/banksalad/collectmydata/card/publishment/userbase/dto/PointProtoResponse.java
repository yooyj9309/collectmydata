package com.banksalad.collectmydata.card.publishment.userbase.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardPoint;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardPointsResponse;
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
public class PointProtoResponse {

  private List<PointPublishment> pointPublishments;

  public ListCardPointsResponse toListCardPointResponseProto() {

    List<CardPoint> cardPointsProto = pointPublishments.stream().map(pointPublishment -> CardPoint.newBuilder()
        .setPointName(pointPublishment.getPointName())
        .setRemainPointAmt(pointPublishment.getRemainPointAmt())
        .setExpiringPointAmt(pointPublishment.getExpiringPointAmt())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(pointPublishment.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(pointPublishment.getUpdatedAt())).build())
        .collect(Collectors.toList());

    return ListCardPointsResponse.newBuilder()
        .addAllCardPoints(cardPointsProto).build();
  }
}

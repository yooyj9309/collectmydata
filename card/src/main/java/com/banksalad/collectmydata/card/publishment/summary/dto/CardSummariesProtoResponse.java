package com.banksalad.collectmydata.card.publishment.summary.dto;

import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardSummariesResponse;
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
public class CardSummariesProtoResponse {

  List<CardSummary> cardSummaries;

  public ListCardSummariesResponse toListCardSummariesResponseProto() {
    List<CollectmydatacardProto.CardSummary> cardSummariesProto = cardSummaries.stream()
        .map(cardSummary -> CollectmydatacardProto.CardSummary.newBuilder()
            .setCardId(cardSummary.getCardId())
            .setCardNum(cardSummary.getCardNum())
            .setIsConsent(cardSummary.isConsent())
            .setCardName(cardSummary.getCardName())
            .setCardMember(cardSummary.getCardMember())
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(cardSummary.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(cardSummary.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return CollectmydatacardProto.ListCardSummariesResponse.newBuilder()
        .addAllCardSummaries(cardSummariesProto)
        .build();
  }
}

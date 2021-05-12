package com.banksalad.collectmydata.card.publishment.summary.dto;

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

  List<CardSummaryPublishment> cardSummaryPublishments;

  public ListCardSummariesResponse toListCardSummariesResponseProto() {
    List<CollectmydatacardProto.CardSummary> cardSummariesProto = cardSummaryPublishments.stream()
        .map(cardSummaryPublishment -> CollectmydatacardProto.CardSummary.newBuilder()
            .setCardId(cardSummaryPublishment.getCardId())
            .setCardNum(cardSummaryPublishment.getCardNum())
            .setIsConsent(cardSummaryPublishment.isConsent())
            .setCardName(cardSummaryPublishment.getCardName())
            .setCardMember(cardSummaryPublishment.getCardMember())
            .setCreatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(cardSummaryPublishment.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.kstLocalDateTimeToEpochMilliSecond(cardSummaryPublishment.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return CollectmydatacardProto.ListCardSummariesResponse.newBuilder()
        .addAllCardSummaries(cardSummariesProto)
        .build();
  }
}

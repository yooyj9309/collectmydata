package com.banksalad.collectmydata.card.publishment.accountinfo.dto;

import com.banksalad.collectmydata.card.card.dto.CardBasic;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardBasicsResponse;
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
public class CardBasicsProtoResponse {

  private List<CardBasicPublishment> cardBasicPublishments;

  public ListCardBasicsResponse toListCardBasicResponsesProto() {
    List<CollectmydatacardProto.CardBasic> cardBasicProtos = cardBasicPublishments.stream()
        .map(cardBasicPublishment -> CollectmydatacardProto.CardBasic.newBuilder()
            .setCardId(cardBasicPublishment.getCardId())
            .setCardType(cardBasicPublishment.getCardType())
            .setIsTransPayable(cardBasicPublishment.isTransPayable())
            .setIsCashCard(cardBasicPublishment.isCashCard())
            .setLinkedBankCode(cardBasicPublishment.getLinkedBankCode())
            .setCardBrand(cardBasicPublishment.getCardBrand())
            .setAnnualFee3F(NumberUtil.multiply1000(cardBasicPublishment.getAnnualFee()))
            .setIssueDate(cardBasicPublishment.getIssueDate())
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(cardBasicPublishment.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(cardBasicPublishment.getUpdatedAt()))
            .build())
        .collect(Collectors.toList());

    return ListCardBasicsResponse.newBuilder()
        .addAllCardBasics(cardBasicProtos).build();
  }

}

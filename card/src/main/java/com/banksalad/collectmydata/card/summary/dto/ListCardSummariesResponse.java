package com.banksalad.collectmydata.card.summary.dto;

import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ListCardSummariesResponse implements SummaryResponse {

  private String rspCode;

  private String rspMsg;

  private long searchTimestamp;

  private int cardCnt;

  @JsonProperty("card_list")
  private List<CardSummary> cardSummaries;
}

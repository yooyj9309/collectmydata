package com.banksalad.collectmydata.card.publishment.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CardSummaryPublishment {

  private String cardId;

  private String cardNum;

  @JsonProperty("is_consent")
  private boolean consent;

  private String cardName;

  private Integer cardMember;

  private long searchTimestamp;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private LocalDateTime approvalDomesticTransactionSyncedAt;

  private LocalDateTime approvalOverseasTransactionSyncedAt;
}

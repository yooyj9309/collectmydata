package com.banksalad.collectmydata.card.publishment.userbase.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RevolvingPublishment {

  private Short revolvingNo;

  // revolvingList에서 반복되는 값이지만 편의를 위해 추가한다.
  private int revolvingMonth;

  private String reqDate;

  private BigDecimal minPayRate;

  private BigDecimal minPayAmt;

  private BigDecimal agreedPayRate;

  private BigDecimal remainedAmt;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

}

package com.banksalad.collectmydata.card.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CardBasic {

  private String cardType;

  @JsonProperty("is_trans_payable")
  private boolean transPayable;

  @JsonProperty("is_cash_card")
  private boolean cashCard;

  private String linkedBankCode;

  private String cardBrand;

  private BigDecimal annualFee;

  private String issueDate;

}

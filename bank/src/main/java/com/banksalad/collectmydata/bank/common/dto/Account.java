package com.banksalad.collectmydata.bank.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Account {

  private String accountNum;
  private String isConsent;
  private String seqno;
  @JsonProperty("is_foreign_deposit")
  private boolean foreignDeposit;
  private String prodName;
  private String accountType;
  private String accountStatus;

  // TODO jayden-lee basicSearchTimestamp, detailSearchTimestamp, transactionFromDate 프로퍼티 추가
}

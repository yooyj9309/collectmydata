package com.banksalad.collectmydata.capital.account.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Account {

  private String accountNum;
  private Boolean isConsent;
  private int seqno;
  private String prodName;
  private String accountType;
  private String accountStatus;

  // TODO 6.7.2 6.7.3 response도 아래에 이어서 관리하는게 맞지않나 고려, (dto -> entity로 변환될 구조이니)
}

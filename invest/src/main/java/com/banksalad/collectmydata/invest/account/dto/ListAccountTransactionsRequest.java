package com.banksalad.collectmydata.invest.account.dto;

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
public class ListAccountTransactionsRequest {

  private String orgCode;
  private String accountNum;
  private String fromDate;
  private String toDate;
  private String nextPage;
  private String limit; // TODO jaeseong: 명세에 맞게 long으로 변경 (테스트배드 연동을 위해 String으로 임시 수정)
}

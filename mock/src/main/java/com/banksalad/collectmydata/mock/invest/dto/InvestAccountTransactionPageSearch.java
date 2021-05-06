package com.banksalad.collectmydata.mock.invest.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InvestAccountTransactionPageSearch {

  private Long banksaladUserId;
  private String orgCode;
  private String accountNum;
  private LocalDateTime fromDate;
  private LocalDateTime toDate;
  private int pageNumber;
  private int pageSize;
}

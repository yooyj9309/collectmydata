package com.banksalad.collectmydata.mock.invest.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class InvestAccountProductListSearch {

  private long banksaladUserId;
  private String organizationId;
  private LocalDateTime updatedAt;
  private String accountNum;
}

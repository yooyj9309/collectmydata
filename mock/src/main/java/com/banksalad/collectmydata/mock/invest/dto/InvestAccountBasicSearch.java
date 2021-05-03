package com.banksalad.collectmydata.mock.invest.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InvestAccountBasicSearch {

  private long banksaladUserId;
  private String organizationId;
  private String accountNum;
}

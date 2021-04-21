package com.banksalad.collectmydata.bank.publishment.invest.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestAccountBasicResponse {

  private String accountNum;

  private String seqno;

  private String standardFundCode;

  private String paidInType;

  private String issueDate;

  private String expDate;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}

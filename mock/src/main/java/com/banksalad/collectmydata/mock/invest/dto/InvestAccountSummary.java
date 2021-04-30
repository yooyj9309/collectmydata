package com.banksalad.collectmydata.mock.invest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
@AllArgsConstructor
public class InvestAccountSummary {

  private BigInteger accountNum;
  private boolean isConsent;
  private String accountName;
  private int accountType;
  private int accountStatus;
}

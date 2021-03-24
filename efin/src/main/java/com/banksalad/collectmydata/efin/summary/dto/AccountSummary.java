package com.banksalad.collectmydata.efin.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccountSummary {

  private String subKey;

  private String accountId;

  @JsonProperty("is_consent")
  private boolean consent;

  private String accountStatus;

  @JsonProperty("is_pay_reg")
  private boolean payReg;

  private int payCnt;

  @JsonProperty("pay_list")
  private List<AccountSummaryPay> accountSummaryPays;


}

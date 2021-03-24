package com.banksalad.collectmydata.efin.account.dto;

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
public class AccountTransaction {

  private String transType;
  private String fobName;
  private String transNum;
  private String transDtime;
  private BigDecimal transAmt;
  private String transOrgCode;
  private String transId;
  private Integer totalInstallCnt;
  private String merchantName;
  private String transTitle;
  private String transCategory;
  private String payMethod;
}

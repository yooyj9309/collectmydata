package com.banksalad.collectmydata.capital.account.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccountBasic {

  private String accountNum;
  private String seqno;
  private String holderName;
  private String issueDate;
  private String expDate;
  private BigDecimal lastOfferedRate;
  private String repayDate;
  private String repayMethod;
  private String repayOrgCode;
  private String repayAccountNum;
}

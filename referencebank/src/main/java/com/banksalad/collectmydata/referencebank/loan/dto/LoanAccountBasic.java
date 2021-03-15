package com.banksalad.collectmydata.referencebank.loan.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanAccountBasic {

  private String rspCode;

  private String rspMsg;

  private long searchTimestamp;

  private String holderName;

  private String issueDate;

  private String expDate;

  private BigDecimal lastOfferedRate;

  private String repayDate;

  private String repayMethod;

  private String repayOrgCode;

  private String repayAccountNum;

}

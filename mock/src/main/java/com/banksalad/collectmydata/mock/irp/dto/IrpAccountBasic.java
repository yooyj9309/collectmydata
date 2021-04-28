package com.banksalad.collectmydata.mock.irp.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class IrpAccountBasic {

  private BigDecimal accumAmt;
  private BigDecimal evalAmt;
  private BigDecimal employerAmt;
  private BigDecimal employeeAmt;
  private String issueDate;
  private String firstDepositDate;
}

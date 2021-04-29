package com.banksalad.collectmydata.mock.irp.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class IrpAccountDetail {

  private String irpName;
  private String irptype;
  private BigDecimal evalAmt;
  private BigDecimal invPrincipal;
  private Integer fundNum;
  private String openDate;
  private String expDate;
  private BigDecimal intRate;
}

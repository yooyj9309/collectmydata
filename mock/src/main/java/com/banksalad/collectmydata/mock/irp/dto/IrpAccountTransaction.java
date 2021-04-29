package com.banksalad.collectmydata.mock.irp.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class IrpAccountTransaction {

  private String transDtime;
  private String transType;
  private BigDecimal transAmt;
}

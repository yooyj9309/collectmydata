package com.banksalad.collectmydata.mock.irp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IrpAccountSummary {

  private String prodName;
  private String accountNum;
  private String seqno;
  private boolean isConsent;
  private String accountStatus;
}

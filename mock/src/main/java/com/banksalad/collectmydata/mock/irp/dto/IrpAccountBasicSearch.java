package com.banksalad.collectmydata.mock.irp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IrpAccountBasicSearch {

  private long banksaladUserId;
  private String organizationId;
  private String accountNum;
  private String seqno;
}

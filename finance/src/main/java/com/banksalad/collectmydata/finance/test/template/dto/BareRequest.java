package com.banksalad.collectmydata.finance.test.template.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BareRequest {

  private Long searchTimestamp;

  private String nextPage;

  private Integer chargeMonth;

  /* 6.3.5 조회 시 필요 */
  private String seqno;
}

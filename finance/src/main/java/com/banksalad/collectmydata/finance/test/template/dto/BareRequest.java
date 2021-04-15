package com.banksalad.collectmydata.finance.test.template.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BareRequest {

  private Long searchTimestamp;

  private String nextPage;
}

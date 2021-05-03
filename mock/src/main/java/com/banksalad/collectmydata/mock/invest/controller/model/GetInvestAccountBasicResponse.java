package com.banksalad.collectmydata.mock.invest.controller.model;

import com.banksalad.collectmydata.mock.invest.dto.InvestAccountBasic;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetInvestAccountBasicResponse {

  @JsonUnwrapped
  private InvestAccountBasic investAccountBasic;
}

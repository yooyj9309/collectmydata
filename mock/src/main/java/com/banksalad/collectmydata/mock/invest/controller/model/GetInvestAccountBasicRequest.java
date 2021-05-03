package com.banksalad.collectmydata.mock.invest.controller.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class GetInvestAccountBasicRequest {

  @NotNull
  @Size(max = 20)
  private String accountNum;
}

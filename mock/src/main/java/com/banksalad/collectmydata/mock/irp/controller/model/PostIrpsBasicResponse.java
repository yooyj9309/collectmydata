package com.banksalad.collectmydata.mock.irp.controller.model;

import com.banksalad.collectmydata.mock.irp.dto.IrpAccountBasic;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostIrpsBasicResponse {

  @JsonUnwrapped
  private IrpAccountBasic irpAccountBasic;
}

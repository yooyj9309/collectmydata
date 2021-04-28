package com.banksalad.collectmydata.mock.irp.controller.model;

import com.banksalad.collectmydata.mock.irp.dto.IrpAccountSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetIrpsResponse {

  private int irpCnt;
  private List<IrpAccountSummary> irpList;
}

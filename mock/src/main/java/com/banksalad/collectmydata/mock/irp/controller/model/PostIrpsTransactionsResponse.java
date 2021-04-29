package com.banksalad.collectmydata.mock.irp.controller.model;

import com.banksalad.collectmydata.mock.irp.dto.IrpAccountTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostIrpsTransactionsResponse {

  private String nextPage;
  private int transCnt;
  private List<IrpAccountTransaction> transList;
}

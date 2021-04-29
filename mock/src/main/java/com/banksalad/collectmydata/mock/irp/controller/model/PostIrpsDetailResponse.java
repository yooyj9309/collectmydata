package com.banksalad.collectmydata.mock.irp.controller.model;

import com.banksalad.collectmydata.mock.irp.dto.IrpAccountDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostIrpsDetailResponse {

  private String nextPage;
  private int irpCnt;
  private List<IrpAccountDetail> irpList;
}

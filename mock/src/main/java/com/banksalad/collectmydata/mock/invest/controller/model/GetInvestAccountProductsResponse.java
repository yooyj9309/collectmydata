package com.banksalad.collectmydata.mock.invest.controller.model;

import com.banksalad.collectmydata.mock.invest.dto.InvestAccountProduct;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetInvestAccountProductsResponse {

  private int prodCnt;
  private List<InvestAccountProduct> investAccountProducts;
}

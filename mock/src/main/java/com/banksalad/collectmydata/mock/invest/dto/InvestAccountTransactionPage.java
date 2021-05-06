package com.banksalad.collectmydata.mock.invest.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class InvestAccountTransactionPage {

  private int totalElements;
  private boolean isFirst;
  private boolean isLast;
  private int totalPages;
  private int pageSize;
  private int pageNumber;
  private int numberOfElements;
  private List<InvestAccountTransaction> investAccountTransaction;
}

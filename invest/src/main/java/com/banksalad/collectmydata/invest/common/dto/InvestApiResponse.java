package com.banksalad.collectmydata.invest.common.dto;

import com.banksalad.collectmydata.invest.account.dto.AccountBasic;
import com.banksalad.collectmydata.invest.account.dto.AccountProduct;
import com.banksalad.collectmydata.invest.account.dto.AccountTransaction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestApiResponse {

  private List<AccountBasic> accountBasics;
  private List<AccountTransaction> accountTransactions;
  private List<AccountProduct> accountProducts;
}

package com.banksalad.collectmydata.invest.collect;

import org.springframework.http.HttpMethod;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Pagination;

public class Apis {

  // 6.4.1 계좌 목록 조회 API
  public static Api finance_invest_accounts =
      Api.builder()
          .id("IV01")
          .name("6.4.1 계좌 목록 조회")
          .endpoint("/accounts?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  // 6.4.2 계좌 기본정보 조회 API
  public static Api finance_invest_account_basic =
      Api.builder()
          .id("IV02")
          .name("6.4.2 계좌 기본정보 조회")
          .endpoint("/accounts/basic")
          .method(HttpMethod.POST.name())
          .build();

  // 6.4.3 계좌 거래내역 조회 API
  public static Api finance_invest_account_transactions =
      Api.builder()
          .id("IV03")
          .name("6.4.3 계좌 거래내역 조회")
          .endpoint("/accounts/transactions")
          .method(HttpMethod.POST.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

  // 6.4.4 계좌 상품정보 조회 API
  public static Api finance_invest_account_products =
      Api.builder()
          .id("IV04")
          .name("6.4.4 계좌 상품정보 조회")
          .endpoint("/accounts/products")
          .method(HttpMethod.POST.name())
          .build();
}

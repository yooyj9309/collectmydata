package com.banksalad.collectmydata.bank.common.collect;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Pagination;

public class Apis {

  private enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE, CONNECT
  }

  public static Api finance_bank_accounts =
      Api.builder()
          .id("BA01")
          .name("계좌 목록 조회")
          .endpoint(
              "/accounts?org_code={org_code}&search_timestamp={search_timestamp}&next_page={next_page}&limit={limit}")
          .method(HttpMethod.GET.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

  //6.2.5 투자상품 계좌 기본 정보 조회
  public static Api finance_bank_invest_account_basic =
      Api.builder()
          .id("BA05")
          .name("투자 상품 계좌 기본 정보 조회")
          .endpoint("/accounts/invest/basic")
          .method(HttpMethod.POST.name())
          .build();

  //6.2.6 투자상품 계좌 추가 정보 조회
  public static Api finance_bank_invest_account_detail =
      Api.builder()
          .id("BA06")
          .name("투자 상품 계좌 추가 정보 조회")
          .endpoint("/accounts/invest/detail")
          .method(HttpMethod.POST.name())
          .build();
}

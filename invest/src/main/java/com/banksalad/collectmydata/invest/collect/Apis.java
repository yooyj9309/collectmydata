package com.banksalad.collectmydata.invest.collect;

import com.banksalad.collectmydata.common.collect.api.Api;

public class Apis {

  private enum HttpMethod {
    GET, POST
  }

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

}

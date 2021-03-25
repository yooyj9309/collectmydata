package com.banksalad.collectmydata.ginsu.collect;

import com.banksalad.collectmydata.common.collect.api.Api;

public class Apis {

  private enum HttpMethod {
    GET, POST
  }

  public static Api finance_ginsu_summaries =
      Api.builder()
          .id("GI01")
          .name("보증 보험 목록 조회")
          .endpoint(
              "/insurances?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  public static Api finance_ginsu_insurance_basic =
      Api.builder()
          .id("GI02")
          .name("보증 보험 기본 정보 조회")
          .endpoint("/insurances/basic")
          .method(HttpMethod.POST.name())
          .build();

  public static Api finance_ginsu_account_transactions =
      Api.builder()
          .id("GI03")
          .name("보증 보험 거래내역 조회")
          .endpoint("/insurances/transactions")
          .method(HttpMethod.POST.name())
          .build();
}

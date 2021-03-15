package com.banksalad.collectmydata.telecom.collect;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Pagination;

public class Apis {

  private enum HttpMethod {
    GET, POST
  }

  // 6.9.1 통신 계약 목록 조회
  public static Api finance_telecom_summaries =
      Api.builder()
          .id("TC01")
          .name("TC01-통신 계약 목록 조회")
          .endpoint("/telecoms?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  // 6.9.2 청구 정보 조회
  public static Api finance_telecom_bills =
      Api.builder()
          .id("TC02")
          .name("TC02-청구 정보 조회")
          .endpoint("/telecoms/bills?org_code={org_code}&charge_month={charge_month}")
          .method(HttpMethod.GET.name())
          .build();

  // 6.9.3 통신 거래내역 조회
  public static Api finance_telecom_transactions =
      Api.builder()
          .id("TC03")
          .name("TC03-통신 거래내역 조회")
          .endpoint("/telecoms/transactions")
          .method(HttpMethod.POST.name())
          .build();

  // 6.9.4 결제내역 조회
  public static Api finance_telecom_paid_transactions =
      Api.builder()
          .id("TC04")
          .name("TC04-결제내역 조회")
          .endpoint("/telecoms/paid-transactions")
          .method(HttpMethod.POST.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

}

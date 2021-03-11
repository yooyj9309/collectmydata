package com.banksalad.collectmydata.irp.collect;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Pagination;

public class Apis {

  // 6.1.3 개인형 IRP 계좌 목록 조회 (은행, 금투, 보험 공통)
  public static Api irp_get_accounts =
      Api.builder()
          .id("IR01")
          .name("IR01-개인형 IRP 계좌 목록 조회")
          .endpoint("/irps?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();
  // 6.1.4 개인형 IRP 계좌 기본정보 조회 (은행, 금투, 보험 공통)
  public static Api irp_get_basic =
      Api.builder()
          .id("IR02")
          .name("IR02-개인형 IRP 계좌 기본정보 조회")
          .endpoint("/irps/basic")
          .method(HttpMethod.POST.name())
          .build();
  // 6.1.5 개인형 IRP 계좌 추가정보 조회 (은행, 금투, 보험 공통)
  public static Api irp_get_detail =
      Api.builder()
          .id("IR03")
          .name("IR03-개인형 IRP 계좌 추가정보 조회")
          .endpoint("/irps/detail")
          .method(HttpMethod.POST.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();
  // 6.1.6 개인형 IRP 계좌 거래내역 조회 (은행, 금투, 보험 공통)
  public static Api irp_get_transactions =
      Api.builder()
          .id("IR04")
          .name("IR04-개인형 IRP 계좌 거래내역 조회")
          .endpoint("/irps/transactions")
          .method(HttpMethod.POST.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

  private enum HttpMethod {
    GET, POST
  }
}

package com.banksalad.collectmydata.efin.collect;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Pagination;

public class Apis {

  public enum HttpMethod {
    GET, POST
  }

  public static Api finance_efin_summaries =
      Api.builder()
          .id("EF01")
          .name("EF01-전자지급수단 목록 조회")
          .endpoint(
              "/accounts?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  public static Api finance_efin_balances =
      Api.builder()
          .id("EF02")
          .name("EF02-전자지급수단 잔액정보 조회")
          .endpoint("/accounts/balance")
          .method(HttpMethod.POST.name())
          .build();

  public static Api finance_efin_charge =
      Api.builder()
          .id("EF03")
          .name("EF03-전자지급수단 자동충전정보 조회")
          .endpoint("/accounts/charge")
          .method(HttpMethod.POST.name())
          .build();

  public static Api finance_efin_prepaid_transactions =
      Api.builder()
          .id("EF04")
          .name("EF04-선불 거래내역 조회")
          .endpoint("/accounts/prepaid-transactions")
          .method(HttpMethod.POST.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

  public static Api finance_efin_transactions =
      Api.builder()
          .id("EF05")
          .name("EF05-결제내역 조회")
          .endpoint("/accounts/transactions")
          .method(HttpMethod.POST.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();


}

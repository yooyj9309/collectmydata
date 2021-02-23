package com.banksalad.collectmydata.referencebank.collect;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Pagination;

public class Apis {

  private enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE, CONNECT
  }

  public static Api finance_bank_accounts =
      Api.builder()
          .id("BA01")
          .name("계좌목록조회")
          .endpoint(
              "/accounts?org_code={org_code}&search_timestamp={search_timestamp}&next_page={next_page}&limit={limit}")
          .method(HttpMethod.GET.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

  public static Api finance_bank_accounts_deposit_basic =
      Api.builder()
          .id("BA02")
          .name("수신계좌기본정보조회")
          .endpoint("/accounts/deposit/basic")
          .method(HttpMethod.POST.name())
          .build();

  public static Api finance_bank_accounts_deposit_detail =
      Api.builder()
          .id("BA03")
          .name("수신계좌추가정보조회")
          .endpoint("/accounts/deposit/detail")
          .method(HttpMethod.POST.name())
          .build();


  public static Api finance_bank_accounts_deposit_transactions =
      Api.builder()
          .id("BA04")
          .name("수신계좌거래정보조회")
          .endpoint("/accounts/deposit/transactions")
          .method(HttpMethod.POST.name())
          .build();

  public static Api finance_bank_accounts_invest_basic =
      Api.builder()
          .id("BA11")
          .name("투자상품계좌기본정조회")
          .endpoint("/accounts/invest/basic")
          .method(HttpMethod.POST.name())
          .build();

  public static Api finance_bank_accounts_invest_detail =
      Api.builder()
          .id("BA12")
          .name("투자상품계좌추가정조회")
          .endpoint("/accounts/deposit/detail")
          .method(HttpMethod.POST.name())
          .build();

  public static Api finance_bank_accounts_invest_transactions =
      Api.builder()
          .id("BA13")
          .name("투자상품계좌거래내역조회")
          .endpoint("/accounts/deposit/transactions")
          .method(HttpMethod.POST.name())
          .build();

  public static Api finance_bank_accounts_loan_basic =
      Api.builder()
          .id("BA21")
          .name("대출상품계좌기본정본조회")
          .endpoint("/accounts/loan/basic")
          .method(HttpMethod.POST.name())
          .build();

  public static Api finance_bank_accounts_loan_detail =
      Api.builder()
          .id("BA22")
          .name("대출상품계좌추가정보조회")
          .endpoint("/accounts/loan/detail")
          .method(HttpMethod.POST.name())
          .build();

  public static Api finance_bank_accounts_loan_transactions =
      Api.builder()
          .id("BA23")
          .name("대출상품계좌거래내역조회")
          .endpoint("/accounts/loan/transactions")
          .method(HttpMethod.POST.name())
          .build();
}

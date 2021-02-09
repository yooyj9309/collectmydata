package com.banksalad.collectmydata.capital.common.collect;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Pagination;

public class Apis {

  private enum HttpMethod {
    GET, POST
  }

  // 6.7.1 계좌 목록 조회 API
  public static Api capital_get_accounts =
      Api.builder()
          .id("CP01")
          .name("계좌 목록 조회(6.7.1)")
          .endpoint("/loans?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  // 6.7.2 대출상품계좌 기본정보 조회
  public static Api capital_get_account_basic =
      Api.builder()
          .id("CP02")
          .name("대출상품계좌 기본정보 조회(6.7.2)")
          .endpoint("/loans/basic")
          .method(HttpMethod.POST.name())
          .build();

  // 6.7.3 대출상품계좌 추가정보 조회
  public static Api capital_get_account_detail =
      Api.builder()
          .id("CP03")
          .name("대출상품계좌 추가정보 조회(6.7.3)")
          .endpoint("/loans/detail")
          .method(HttpMethod.POST.name())
          .build();

  // 6.7.4 대출상품계좌 거래내역 조회
  public static Api capital_get_account_transactions =
      Api.builder()
          .id("CP04")
          .name("대출상품계좌 거래내역 조회(6.7.4)")
          .endpoint("/loans/transactions?org_code={org_code}&account_num={account_num}&seqno={seqno}" +
                  "&from_dtime={from_dtime}&to_dtime={to_dtime}&next_page={next_page}&limit={limit}")
          .method(HttpMethod.POST.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

  // 6.7.5 운용리스 기본정보 조회 API
  public static Api capital_get_operating_lease_basic =
      Api.builder()
          .id("CP05")
          .name("운용리스 기본정보 조회(6.7.5)")
          .endpoint("/loans/oplease/basic")
          .method(HttpMethod.POST.name())
          .build();

  // 6.7.6 운용리스 거래내역 조회 API
  public static Api capital_get_operating_lease_transactions =
      Api.builder()
          .id("CP06")
          .name("운용리스 거래정보 조회(6.7.6)")
          .endpoint("/loans/oplease/transactions")
          .method(HttpMethod.POST.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

}

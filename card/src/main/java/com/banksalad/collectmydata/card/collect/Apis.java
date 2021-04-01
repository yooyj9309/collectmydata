package com.banksalad.collectmydata.card.collect;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Pagination;

public class Apis {

  public enum HttpMethod {
    GET, POST
  }

  public static Api finance_card_summaries =
      Api.builder()
          .id("CD01")
          .name("CD01-카드 목록 조회")
          .endpoint(
              "/cards?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  public static Api finance_card_basic =
      Api.builder()
          .id("CD02")
          .name("CD02-카드 기본정보 조회")
          .endpoint(
              "/cards/{card_id}?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  public static Api finance_card_approval_domestic =
      Api.builder()
          .id("CD03")
          .name("CD03-국내 승인내역 조회")
          .endpoint(
              "/cards/{card_id}/approval-domestic?org_code={org_code}&from_date={from_date}&to_date={to_date}&next_page={next_page}&limit={limit}")
          .method(HttpMethod.GET.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

  public static Api finance_card_point =
      Api.builder()
          .id("CD11")
          .name("CD11-포인트 정보 조회")
          .endpoint("/cards/point??org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  public static Api finance_card_bills =
      Api.builder()
          .id("CD21")
          .name("CD21-청구 기본정보 조회")
          .endpoint(
              "/cards/bills?org_code={org_code}&from_month={from_month}&to_month={to_month}&next_page={next_page}&limit={limit}")
          .method(HttpMethod.GET.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

  public static Api finance_card_bills_detail =
      Api.builder()
          .id("CD22")
          .name("CD22-청구 추가정보 조회")
          .endpoint(
              "/cards/bills/detail?org_code={org_code}&seqno={seqno}&charge_month={charge_month}&next_page={next_page}&limit={limit}")
          .method(HttpMethod.GET.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

  public static Api finance_card_payment =
      Api.builder()
          .id("CD23")
          .name("CD23-결제내역 조회")
          .endpoint(
              "/cards/payment?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  public static Api finance_loan_summary =
      Api.builder()
          .id("CD31")
          .name("CD31-대출상품 목록 조회")
          .endpoint(
              "/loans?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  public static Api finance_loan_revolvings =
      Api.builder()
          .id("CD32")
          .name("CD32-리볼빙 정보 조회")
          .endpoint(
              "/loans/revolving?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  public static Api finance_loan_short_terms =
      Api.builder()
          .id("CD33")
          .name("CD33-단기대출 정보 조회")
          .endpoint(
              "/loans/short-term?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  public static Api finance_loan_long_terms =
      Api.builder()
          .id("CD34")
          .name("CD34-장기대출 정보 조회")
          .endpoint(
              "/loans/long-term?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();
}

package com.banksalad.collectmydata.insu.collect;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Pagination;

public class Apis {

  private enum HttpMethod {
    GET, POST
  }

  // 6.5.1 보험 목록 조회
  public static Api insurance_get_summaries =
      Api.builder()
          .id("IS01")
          .name("IS01-보험 목록 조회")
          .endpoint("/insurances?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  // 6.5.2 보험 기본정보 조회
  public static Api insurance_get_basic =
      Api.builder()
          .id("IS02")
          .name("IS02-보험 기본정보 조회")
          .endpoint("/insurances/basic")
          .method(HttpMethod.POST.name())
          .build();

  // 6.5.3 보험 특약정보 조회
  public static Api insurance_get_contract =
      Api.builder()
          .id("IS03")
          .name("IS03-보험 특약정보 조회")
          .endpoint("/insurances/contracts")
          .method(HttpMethod.POST.name())
          .build();

  // 6.5.4 자동차보험 정보 조회
  public static Api insurance_get_car =
      Api.builder()
          .id("IS04")
          .name("IS04-자동차보험 정보 조회")
          .endpoint("/insurances/car")
          .method(HttpMethod.POST.name())
          .build();

  // 6.5.5 보험 납입정보 조회
  public static Api insurance_get_payment =
      Api.builder()
          .id("IS05")
          .name("IS05-보험 납입정보 조회")
          .endpoint("/insurances/payment")
          .method(HttpMethod.POST.name())
          .build();

  // 6.5.6 보험 거래내역 조회
  public static Api insurance_get_transactions =
      Api.builder()
          .id("IS06")
          .name("IS06-보험 거래내역 조회")
          .endpoint("/insurances/transactions")
          .method(HttpMethod.POST.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

  // 6.5.7 자동차보험 거래내역 조회
  public static Api insurance_get_car_transactions =
      Api.builder()
          .id("IS07")
          .name("IS07-자동차보험 거래내역 조회")
          .endpoint("/insurances/car/transactions")
          .method(HttpMethod.POST.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

  // 6.5.8 대출상품 목록 조회
  public static Api insurance_get_loan_summaries =
      Api.builder()
          .id("IS11")
          .name("IS11-대출상품 목록 조회")
          .endpoint("/loans?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  // 6.5.9 대출상품 기본정보 조회
  public static Api insurance_get_loan_basic =
      Api.builder()
          .id("IS12")
          .name("IS12-대출상품 기본정보 조회")
          .endpoint("/loans/basic")
          .method(HttpMethod.POST.name())
          .build();

  // 6.5.10 대출상품 추가정보 조회
  public static Api insurance_get_loan_detail =
      Api.builder()
          .id("IS13")
          .name("IS13-대출상품 추가정보 조회")
          .endpoint("/loans/detail")
          .method(HttpMethod.POST.name())
          .build();

  // 6.5.11 대출상품 거래내역 조회
  public static Api insurance_get_loan_transactions =
      Api.builder()
          .id("IS14")
          .name("IS14-대출상품 거래내역 조회")
          .endpoint("/loans/transactions")
          .method(HttpMethod.POST.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();
}

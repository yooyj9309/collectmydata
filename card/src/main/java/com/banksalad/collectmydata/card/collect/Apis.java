package com.banksalad.collectmydata.card.collect;

import com.banksalad.collectmydata.common.collect.api.Api;

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
          .id("CD01")
          .name("CD02-카드 기본정보 조회")
          .endpoint(
              "/cards/{card_id}?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();


}

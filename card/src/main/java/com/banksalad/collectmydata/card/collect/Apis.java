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


}

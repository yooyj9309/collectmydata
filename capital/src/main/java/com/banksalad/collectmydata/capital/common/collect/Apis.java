package com.banksalad.collectmydata.capital.common.collect;

import com.banksalad.collectmydata.common.collect.api.Api;

public class Apis {

  private enum HttpMethod {
    GET, POST
  }

  public static Api capital_get_accounts =
      Api.builder()
          .id("CP01")
          .name("계좌 목록 조회(6.7.1)")
          .endpoint("/loans?org_code={org_code}&search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();
}

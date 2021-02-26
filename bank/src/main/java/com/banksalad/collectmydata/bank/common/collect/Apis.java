package com.banksalad.collectmydata.bank.common.collect;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Pagination;

public class Apis {

  private enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE, CONNECT
  }

  public static Api finance_bank_accounts =
      Api.builder()
          .id("BA01")
          .name("계좌 목록 조회")
          .endpoint(
              "/accounts?org_code={org_code}&search_timestamp={search_timestamp}&next_page={next_page}&limit={limit}")
          .method(HttpMethod.GET.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();
}

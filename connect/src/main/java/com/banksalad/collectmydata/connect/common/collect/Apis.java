package com.banksalad.collectmydata.connect.common.collect;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.api.Pagination;

public class Apis {
  private enum HttpMethod {
    GET, POST
  }

  public static Api support_get_access_token =
      Api.builder()
          .id("SU01") // 7.x 는 이름이 없습니다.
          .name("접근토큰 발급(7.1.2)")
          .endpoint("/oauth/2.0/token")
          .method(HttpMethod.POST.name())
          .build();

  public static Api support_get_organization_info =
      Api.builder()
          .id("SU02")
          .name("기관정보 조회(7.1.2)")
          .endpoint("/mgmts/orgs?search_timestamp={search_timestamp}")
          .method(HttpMethod.GET.name())
          .build();

  public static Api support_get_organization_service_info =
      Api.builder()
          .id("SU03")
          .name("서비스정보 조회(7.1.3)")
          .endpoint("/mgmts/services")
          .method(HttpMethod.POST.name())
          .build();

  public static Api oauth_issue_token =
      Api.builder()
          .id("AU02")
          .name("접근토큰 발급 요청(5.1.2)")
          .endpoint("/oauth/2.0/token")
          .method(HttpMethod.POST.name())
          .build();

  public static Api oauth_refresh_token =
      Api.builder()
          .id("AU02")
          .name("접근토큰 갱신(5.1.3)")
          .endpoint("/oauth/2.0/token")
          .method(HttpMethod.GET.name())
          .build();

  public static Api oauth_revoke_token =
      Api.builder()
          .id("AU03")
          .name("접근토큰 폐기(5.1.4)")
          .endpoint("/oauth/2.0/revoke")
          .method(HttpMethod.GET.name())
          .build();
}

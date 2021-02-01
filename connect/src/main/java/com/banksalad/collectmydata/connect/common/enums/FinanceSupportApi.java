package com.banksalad.collectmydata.connect.common.enums;

public enum FinanceSupportApi {
  ORGANIZATION_TOEKN("/oauth/2.0/token", "7.1.1 접근토큰 발급"),
  ORGANIZATION_INFO("/mgmts/orgs", "7.1.2 기관정보 조회"),
  ORGANIZATION_SERVICE_INFO("/mgmts/services", "7.1.3 서비스정보 조회");

  private String apiPath;
  private String description;

  FinanceSupportApi(String apiPath, String description) {
    this.apiPath = apiPath;
    this.description = description;
  }
}

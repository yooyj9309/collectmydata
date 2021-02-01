package com.banksalad.collectmydata.connect.support.service;

import com.banksalad.collectmydata.connect.common.enums.FinanceSupportApi;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceResponse;

import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

public class SupportServiceImpl implements SupportService {

  @Value("${organization.finance-portal-domain}")
  private String financePortalDomain;

  public void syncAllOrganizationInfo() {
    // 1. 뱅크샐러드 토큰 갱신
    // 2. timestamp 조회
    // 3.1. 7.1.2 api 조회 및 upsert
    // 3.2. 7.1.3 service 조회 및 upsert

    // 조회 timestamp 업데이트
    String accessToken = getAccessToken();
    syncOrganizationInfo(accessToken, false);
    syncOrganizationServiceInfo(accessToken, false);
  }

  @Override
  public void syncOrganizationInfo() {
    syncOrganizationInfo(null, true);
  }

  @Override
  public void syncOrganizationServiceInfo() {
    syncOrganizationServiceInfo(null, true);
  }


  public void syncOrganizationInfo(String accessToken, Boolean requireRefreshToken) {
    if (requireRefreshToken) {
      accessToken = getAccessToken();
    }
    Long timestamp = getTimeStamp(FinanceSupportApi.ORGANIZATION_INFO); // 7.1.2 timestamp 조회 fixme
    // request 생성 fixme
    // 7.1.2 기관 정보 조회 및 적재
    FinanceOrganizationResponse financeOrganizationResponse = null;
  }

  public void syncOrganizationServiceInfo(String accessToken, Boolean requireRefreshToken) {
    if (requireRefreshToken) {
      accessToken = getAccessToken();
    }

    Long timestamp = getTimeStamp(FinanceSupportApi.ORGANIZATION_SERVICE_INFO); // 7.1.3 timestamp 조회 fixme
    // request 생성 fixme
    // 7.1.3 기관 서비스 정보 조회 및 적재
    FinanceOrganizationServiceResponse financeOrganizationServiceResponse = null;

  }

  private String getAccessToken() {
    return ""; // fixme
  }

  private Long getTimeStamp(FinanceSupportApi financeSupportApi) {
    //DB 조회
    return Optional.of(1L).orElse(0L); // fixme
  }

}

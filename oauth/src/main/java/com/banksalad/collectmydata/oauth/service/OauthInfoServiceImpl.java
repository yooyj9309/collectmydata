package com.banksalad.collectmydata.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.oauth.common.enums.OauthErrorType;
import com.banksalad.collectmydata.oauth.common.exception.OauthException;
import com.banksalad.collectmydata.oauth.dto.Organization;

@Service
public class OauthInfoServiceImpl implements OauthInfoService {

  public static final String MYDATA_OAUTH_PROXY_HOST = ""; //TODO 새로생길 oauth 도메인
  public static final String APPROVE_URL = "/v1/mydata-auth/authorize";

  @Value("${organization.mydata-client-id}")
  private String CLIENT_ID;

  @Override
  public String getRedirectUrl(MydataSector sector, String state, Organization organization) {

    switch (sector) {
      case FINANCE:
        return getFinanceRedirectUrL(state, organization);
      // 아래는 아직 없는 데이
      case HEALTHCARE:
      case PUBLIC:
      default:
        // TODO
        throw new OauthException(OauthErrorType.INVALID_SECTOR, organization.getOrganizationId());
    }
  }

  private String getFinanceRedirectUrL(String state, Organization organization) {
    return UriComponentsBuilder
        .fromHttpUrl(organization.getOrganizationHost())
        .queryParam("org_code", organization.getOrganizationCode())
        .queryParam("response_type", "code")
        .queryParam("client_id", CLIENT_ID)
        .queryParam("redirect_uri", MYDATA_OAUTH_PROXY_HOST + APPROVE_URL)
        .queryParam("state", state)
        .build()
        .encode()
        .toUriString();
  }

  private String getHealthcareRedirectUrL() {
    // TODO
    return "";
  }

  private String getPublicRedirectUrL() {
    // TODO
    return "";
  }
}

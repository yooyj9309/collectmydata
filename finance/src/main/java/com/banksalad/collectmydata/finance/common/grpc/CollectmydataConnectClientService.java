package com.banksalad.collectmydata.finance.common.grpc;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationIdRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectmydataConnectClientService {

  private final ConnectmydataBlockingStub connectmydataBlockingStub;

  @Cacheable(value = "organizationCache", key = "#organizationId")
  public Organization getOrganization(String organizationId) {
    GetOrganizationByOrganizationIdRequest request = GetOrganizationByOrganizationIdRequest.newBuilder()
        .setOrganizationId(organizationId)
        .build();

    GetOrganizationResponse response = connectmydataBlockingStub.getOrganizationByOrganizationId(request);

    return Organization.builder()
        .sector(String.valueOf(MydataSector.getSector(response.getSector())))
        .industry(String.valueOf(Industry.getIndustry(response.getIndustry())))
        .organizationId(response.getOrganizationId())
        .organizationCode(response.getOrganizationCode())
        .hostUrl(response.getDomain())
        .build();
  }

  public OauthToken getAccessToken(long banksaladUserId, String organizationId) {
    GetAccessTokenRequest request = GetAccessTokenRequest.newBuilder()
        .setBanksaladUserId(String.valueOf(banksaladUserId))
        .setOrganizationId(organizationId)
        .build();

    GetAccessTokenResponse response = connectmydataBlockingStub.getAccessToken(request);

    return OauthToken.builder()
        .accessToken(response.getAccessToken())
        .consentId(response.getConsentId())
        .scopes(response.getScopeList())
        .build();
  }
}

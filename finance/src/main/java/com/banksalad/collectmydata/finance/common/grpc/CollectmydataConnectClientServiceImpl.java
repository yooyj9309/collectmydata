package com.banksalad.collectmydata.finance.common.grpc;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectGrpc.CollectmydataconnectBlockingStub;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetAccessTokenResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationGuidRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationIdRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectmydataConnectClientServiceImpl implements CollectmydataConnectClientService {

  private final CollectmydataconnectBlockingStub collectmydataconnectBlockingStub;

  @Cacheable(value = "organizationCache", key = "#organizationId")
  public Organization getOrganization(String organizationId) {

    GetOrganizationResponse response = collectmydataconnectBlockingStub.getOrganizationByOrganizationId(
        GetOrganizationByOrganizationIdRequest.newBuilder()
            .setOrganizationId(organizationId)
            .build());

    return Organization.builder()
        .sector(String.valueOf(MydataSector.getSector(response.getSector())))
        .industry(String.valueOf(Industry.getIndustry(response.getIndustry())))
        .organizationId(response.getOrganizationId())
        .organizationCode(response.getOrganizationCode())
        .hostUrl(response.getDomain())
        .build();
  }

  @Cacheable(value = "organizationByOrganizationGuidCache", key = "#organizationGuid")
  public Organization getOrganizationByOrganizationGuid(String organizationGuid) {

    GetOrganizationResponse response = collectmydataconnectBlockingStub.getOrganizationByOrganizationGuid(
        GetOrganizationByOrganizationGuidRequest.newBuilder().setOrganizationGuid(organizationGuid)
            .build());

    return Organization.builder()
        .sector(String.valueOf(MydataSector.getSector(response.getSector())))
        .industry(String.valueOf(Industry.getIndustry(response.getIndustry())))
        .organizationId(response.getOrganizationId())
        .organizationCode(response.getOrganizationCode())
        .hostUrl(response.getDomain())
        .build();
  }

  public OauthToken getAccessToken(long banksaladUserId, String organizationId) {

    GetAccessTokenResponse response = collectmydataconnectBlockingStub.getAccessToken(GetAccessTokenRequest.newBuilder()
        .setBanksaladUserId(String.valueOf(banksaladUserId))
        .setOrganizationId(organizationId)
        .build());

    return OauthToken.builder()
        .accessToken(response.getAccessToken())
        .consentId(response.getConsentId())
        .scopes(response.getScopeList())
        .build();
  }
}

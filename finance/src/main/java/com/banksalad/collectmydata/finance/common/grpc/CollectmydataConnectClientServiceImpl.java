package com.banksalad.collectmydata.finance.common.grpc;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationIdRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationObjectidRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectmydataConnectClientServiceImpl implements CollectmydataConnectClientService {

  private final ConnectmydataBlockingStub connectmydataBlockingStub;

  @Cacheable(value = "organizationCache", key = "#organizationId")
  public Organization getOrganization(String organizationId) {

    GetOrganizationResponse response = connectmydataBlockingStub.getOrganizationByOrganizationId(
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

  @Cacheable(value = "organizationByOrganizationObjectidCache", key = "#organizationObjectid")
  public Organization getOrganizationByOrganizationObjectid(String organizationObjectid) {

    GetOrganizationResponse response = connectmydataBlockingStub.getOrganizationByOrganizationObjectid(
        GetOrganizationByOrganizationObjectidRequest.newBuilder().setOrganizationObjectid(organizationObjectid)
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

    GetAccessTokenResponse response = connectmydataBlockingStub.getAccessToken(GetAccessTokenRequest.newBuilder()
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

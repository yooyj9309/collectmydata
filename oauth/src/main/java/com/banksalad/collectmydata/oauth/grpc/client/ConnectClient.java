package com.banksalad.collectmydata.oauth.grpc.client;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.oauth.common.enums.OauthErrorType;
import com.banksalad.collectmydata.oauth.common.exception.OauthException;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectGrpc.CollectmydataconnectBlockingStub;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationGuidRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.IssueTokenResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectClient {

  private final CollectmydataconnectBlockingStub collectmydataconnectBlockingStub;

  @Cacheable(value = "organizationCache", key = "#organizationGuId")
  public GetOrganizationResponse getOrganization(String organizationGuId) {
    GetOrganizationByOrganizationGuidRequest request = GetOrganizationByOrganizationGuidRequest.newBuilder()
        .setOrganizationGuid(organizationGuId)
        .build();

    try {
      return collectmydataconnectBlockingStub.getOrganizationByOrganizationGuid(request);
    } catch (Exception e) {
      throw new OauthException(OauthErrorType.FAILED_CONNECT_ORGANIZATION_RPC, organizationGuId);
    }
  }

  public IssueTokenResponse issueToken(Long banksaladUserId, String organizationId, String authorizationCode) {
    IssueTokenRequest issueTokenRequest = IssueTokenRequest.newBuilder()
        .setBanksaladUserId(banksaladUserId.toString())
        .setOrganizationId(organizationId)
        .setAuthorizationCode(authorizationCode)
        .build();

    try {
      return collectmydataconnectBlockingStub.issueToken(issueTokenRequest);
    } catch (Exception e) {
      throw new OauthException(OauthErrorType.FAILED_CONNECT_ISSUETOKEN_RPC, organizationId);
    }
  }
}

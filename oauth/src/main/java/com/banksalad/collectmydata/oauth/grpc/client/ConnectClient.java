package com.banksalad.collectmydata.oauth.grpc.client;

import com.banksalad.collectmydata.oauth.common.enums.OauthErrorType;
import com.banksalad.collectmydata.oauth.common.exception.OauthException;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectClient {

  private final ConnectmydataBlockingStub connectmydataBlockingStub;

  public GetOrganizationResponse getOrganization(String organizationObjectId) {
    GetOrganizationRequest request = GetOrganizationRequest.newBuilder()
        .setOrganizationObjectid(organizationObjectId)
        .build();

    try {
      return connectmydataBlockingStub.getOrganization(request);
    } catch (Exception e) {
      throw new OauthException(OauthErrorType.FAILED_CONNECT_ORGANIZATION_RPC, organizationObjectId);
    }
  }

  public IssueTokenResponse issueToken(Long banksaladUserId, String organizationId, String authorizationCode) {
    IssueTokenRequest issueTokenRequest = IssueTokenRequest.newBuilder()
        .setBanksaladUserId(banksaladUserId.toString())
        .setOrganizationId(organizationId)
        .setAuthorizationCode(authorizationCode)
        .build();

    try {
      return connectmydataBlockingStub.issueToken(issueTokenRequest);
    } catch (Exception e) {
      throw new OauthException(OauthErrorType.FAILED_CONNECT_ISSUETOKEN_RPC, organizationId);
    }
  }
}

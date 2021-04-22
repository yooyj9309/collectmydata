package com.banksalad.collectmydata.bank.grpc.client;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationIdRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationObjectidRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectClientService {

  private final ConnectmydataBlockingStub connectmydataBlockingStub;

  public GetOrganizationResponse getOrganizationByOrganizationObjectid(String organizationObjectid) {
    GetOrganizationByOrganizationObjectidRequest request = GetOrganizationByOrganizationObjectidRequest.newBuilder()
        .setOrganizationObjectid(organizationObjectid)
        .build();

    return connectmydataBlockingStub.getOrganizationByOrganizationObjectid(request);
  }

  public GetOrganizationResponse getOrganizationByOrganizationId(String organizationId) {
    GetOrganizationByOrganizationIdRequest request = GetOrganizationByOrganizationIdRequest.newBuilder()
        .setOrganizationId(organizationId)
        .build();

    return connectmydataBlockingStub.getOrganizationByOrganizationId(request);
  }

  public GetAccessTokenResponse getAccessToken(String banksaladUserId, String organizationId) {
    GetAccessTokenRequest request = GetAccessTokenRequest.newBuilder()
        .setBanksaladUserId(banksaladUserId)
        .setOrganizationId(organizationId)
        .build();

    return connectmydataBlockingStub.getAccessToken(request);
  }
}

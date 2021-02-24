package com.banksalad.collectmydata.capital.grpc.client;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationIdRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectmydataConnectClientService {

  private final ConnectmydataBlockingStub connectmydataBlockingStub;

  public GetOrganizationResponse getOrganization(String organizationId) {
    GetOrganizationByOrganizationIdRequest request = GetOrganizationByOrganizationIdRequest.newBuilder()
        .setOrganizationId(organizationId)
        .build();

    return connectmydataBlockingStub.getOrganizationByOrganizationId(request);
  }
}

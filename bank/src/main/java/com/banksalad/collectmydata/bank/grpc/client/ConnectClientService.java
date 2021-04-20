package com.banksalad.collectmydata.bank.grpc.client;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationObjectidRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectClientService {

  private final ConnectmydataBlockingStub connectmydataBlockingStub;

  public GetOrganizationResponse getOrganizationResponse(String organizationObjectid) {
    GetOrganizationByOrganizationObjectidRequest request = GetOrganizationByOrganizationObjectidRequest.newBuilder()
        .setOrganizationObjectid(organizationObjectid)
        .build();

    return connectmydataBlockingStub.getOrganizationByOrganizationObjectid(request);
  }
}

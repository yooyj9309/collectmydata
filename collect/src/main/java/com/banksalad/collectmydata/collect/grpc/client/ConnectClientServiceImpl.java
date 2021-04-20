package com.banksalad.collectmydata.collect.grpc.client;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationObjectidRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;

//@Service
@RequiredArgsConstructor
public class ConnectClientServiceImpl implements ConnectClientService {

  private final ConnectmydataBlockingStub connectmydataBlockingStub;

  public GetOrganizationResponse getOrganizationByOrganizationObjectid(String organizationObjectId) {

    try {
      return connectmydataBlockingStub.getOrganizationByOrganizationObjectid(
          GetOrganizationByOrganizationObjectidRequest.newBuilder().setOrganizationObjectid(organizationObjectId)
              .build());

    } catch (Exception e) {
      throw new CollectRuntimeException("Fail to get organization", e);
    }
  }
}

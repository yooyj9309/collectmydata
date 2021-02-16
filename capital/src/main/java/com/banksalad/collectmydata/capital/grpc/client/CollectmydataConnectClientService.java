package com.banksalad.collectmydata.capital.grpc.client;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectmydataConnectClientService {

  private final ConnectmydataBlockingStub connectmydataBlockingStub;

  // TODO idl 설계 자체는 organizationObjectId를 받는것으로 헀으나.. 이부분 조회 idl을 두개로 나눠야하지않을까 하는 생각이 드네요.
  public GetOrganizationResponse getOrganizationByOrganizationObjectid(String organizationObjectid) {
    GetOrganizationRequest request = GetOrganizationRequest.newBuilder()
        .setOrganizationObjectid(organizationObjectid)
        .build();

    return connectmydataBlockingStub.getOrganization(request);
  }
}

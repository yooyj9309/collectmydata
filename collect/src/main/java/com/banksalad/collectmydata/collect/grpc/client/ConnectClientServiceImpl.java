package com.banksalad.collectmydata.collect.grpc.client;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectGrpc.CollectmydataconnectBlockingStub;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationGuidRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectClientServiceImpl implements ConnectClientService {

  private final CollectmydataconnectBlockingStub collectmydataconnectBlockingStub;

  @Cacheable(value = "getOrganizationByOrganizationGuidCache", key = "#organizationGuId")
  public GetOrganizationResponse getOrganizationByOrganizationGuid(String organizationGuId) {

    try {
      return collectmydataconnectBlockingStub.getOrganizationByOrganizationGuid(
          GetOrganizationByOrganizationGuidRequest.newBuilder().setOrganizationGuid(organizationGuId)
              .build());

    } catch (Exception e) {
      throw new CollectRuntimeException("Fail to get organization", e);
    }
  }
}

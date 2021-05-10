package com.banksalad.collectmydata.collect.grpc.client;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationResponse;

public interface ConnectClientService {

  GetOrganizationResponse getOrganizationByOrganizationGuid(String organizationGuId);
}

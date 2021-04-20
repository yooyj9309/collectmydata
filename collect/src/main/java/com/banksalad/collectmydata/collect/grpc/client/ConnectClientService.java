package com.banksalad.collectmydata.collect.grpc.client;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;


public interface ConnectClientService {

  GetOrganizationResponse getOrganizationByOrganizationObjectid(String organizationObjectId);
}

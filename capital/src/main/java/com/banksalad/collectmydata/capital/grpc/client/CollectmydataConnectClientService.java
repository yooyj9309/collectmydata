package com.banksalad.collectmydata.capital.grpc.client;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationIdRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectmydataConnectClientService {

  private final ConnectmydataBlockingStub connectmydataBlockingStub;

  public Organization getOrganization(String organizationId) {
    GetOrganizationByOrganizationIdRequest request = GetOrganizationByOrganizationIdRequest.newBuilder()
        .setOrganizationId(organizationId)
        .build();

    GetOrganizationResponse response = connectmydataBlockingStub.getOrganizationByOrganizationId(request);
    return Organization.builder()
        .sector(MydataSector.getSector(response.getSector()))
        .industry(Industry.getIndustry(response.getIndustry()))
        .organizationId(response.getOrganizationId())
        .organizationCode(response.getOrganizationCode())
        .domain(response.getDomain())
        .build();
  }
}

package com.banksalad.collectmydata.insu.common.grpc;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc.ConnectmydataBlockingStub;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationIdRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectmydataConnectClientService {

  private final ConnectmydataBlockingStub connectmydataBlockingStub;

  // TODO
  public Organization getOrganization(String organizationId) {
    GetOrganizationByOrganizationIdRequest request = GetOrganizationByOrganizationIdRequest.newBuilder()
        .setOrganizationId(organizationId)
        .build();

    GetOrganizationResponse response = connectmydataBlockingStub.getOrganizationByOrganizationId(request);

    return Organization.builder()
        .sector(String.valueOf(MydataSector.getSector(response.getSector())))
        .industry(String.valueOf(Industry.getIndustry(response.getIndustry())))
        .organizationId(response.getOrganizationId())
        .organizationObjectId(null)
        .organizationCode(response.getOrganizationCode())
        .hostUrl(null)
        .build();
  }
}

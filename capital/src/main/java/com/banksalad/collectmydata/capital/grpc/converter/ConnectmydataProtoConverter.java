package com.banksalad.collectmydata.capital.grpc.converter;

import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;

public class ConnectmydataProtoConverter {

  public static Organization toOrganizationDto(GetOrganizationResponse response) {
    return Organization.builder()
        .sector(MydataSector.getSector(response.getSector()))
        .industry(Industry.getIndustry(response.getIndustry()))
        .organizationId(response.getOrganizationId())
        .organizationCode(response.getOrganizationCode())
        .domain(response.getDomain())
        .build();
  }
}

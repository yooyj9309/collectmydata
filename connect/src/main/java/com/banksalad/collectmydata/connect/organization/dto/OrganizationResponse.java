package com.banksalad.collectmydata.connect.organization.dto;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrganizationResponse {

  private final Organization organization;

  public GetOrganizationResponse toGetOrganizationProto() {
    return GetOrganizationResponse.newBuilder()
        .setSector(organization.getSector())
        .setIndustry(organization.getIndustry())
        .setOrganizationId(organization.getOrganizationId())
        .setOrganizationCode(organization.getOrganizationCode())
        .setDomain(organization.getDomain())
        .build();
  }
}

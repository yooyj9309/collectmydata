package com.banksalad.collectmydata.connect.publishment.organization.dto;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.FinanceOrganization;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.ListConnectedFinanceOrganizationsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.ListFinanceOrganizationsResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class OrganizationForFinanceProtoResponse {

  private final List<OrganizationForFinance> organizationForFinances;

  public ListFinanceOrganizationsResponse toListFinanceOrganizationsProto() {

    return ListFinanceOrganizationsResponse.newBuilder()
        .addAllFinanceOrganizations(mapToFinanceOrganizationList())
        .build();
  }

  public ListConnectedFinanceOrganizationsResponse toListConnectedFinanceOrganizationsProto() {

    return ListConnectedFinanceOrganizationsResponse.newBuilder()
        .addAllFinanceOrganizations(mapToFinanceOrganizationList())
        .build();
  }

  private List<FinanceOrganization> mapToFinanceOrganizationList(){

    return organizationForFinances.stream()
        .map(organizationForFinance -> FinanceOrganization.newBuilder()
            .setOrganizationGuid(organizationForFinance.getOrganizationGuid())
            .setOrganizationId(organizationForFinance.getOrganizationId())
            .setAuthUrl(organizationForFinance.getAuthUrl())
            .build())
        .collect(Collectors.toList());
  }
}

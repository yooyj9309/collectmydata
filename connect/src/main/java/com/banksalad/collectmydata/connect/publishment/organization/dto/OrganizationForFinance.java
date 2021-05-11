package com.banksalad.collectmydata.connect.publishment.organization.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class OrganizationForFinance {

  private final String organizationGuid;
  private final String organizationId;
  private final String authUrl;
}

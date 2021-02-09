package com.banksalad.collectmydata.connect.organization.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class Organization {

  private final String sector;
  private final String industry;
  private final String organizationId;
  private final String organizationCode;
  private final String domain;
}

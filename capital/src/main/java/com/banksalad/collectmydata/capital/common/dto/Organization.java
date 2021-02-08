package com.banksalad.collectmydata.capital.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Organization {

  private final String sector;
  private final String industry;
  private final String organizationId;
  private final String organizationCode;
  private final String domain;
}

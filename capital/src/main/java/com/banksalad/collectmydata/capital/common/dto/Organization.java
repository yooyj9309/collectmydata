package com.banksalad.collectmydata.capital.common.dto;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import lombok.Builder;
import lombok.Getter;

@Deprecated
@Getter
@Builder
public class Organization {

  private final MydataSector sector;
  private final Industry industry;
  private final String organizationId;
  private final String organizationCode;
  private final String domain;
}

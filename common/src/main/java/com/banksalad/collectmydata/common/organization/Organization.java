package com.banksalad.collectmydata.common.organization;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Deprecated
public class Organization {

  private final MydataSector sector;
  private final Industry industry;
  private final String organizationId;
  private final String organizationObjectId;
  private final String organizationCode;
  private final String hostUrl;
}

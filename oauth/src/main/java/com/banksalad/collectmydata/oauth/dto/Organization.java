package com.banksalad.collectmydata.oauth.dto;

import com.banksalad.collectmydata.oauth.common.enums.MydataSector;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Organization {

  private String organizationId;
  private String organizationCode;
  private String organizationObjectId;
  private String organizationHost;
  private String industry; // TODO 해당부분 6개? 또는 마이데이터 이외의 기관 들어오는경우 enum으로 수정
  private MydataSector mydataSector;
}

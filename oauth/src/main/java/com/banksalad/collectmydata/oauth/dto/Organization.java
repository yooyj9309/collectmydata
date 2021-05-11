package com.banksalad.collectmydata.oauth.dto;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Organization {

  private String organizationId;
  private String organizationCode;
  private String organizationGuid;
  private String organizationHost;
  private Industry industry; // TODO 해당부분 6개? 또는 마이데이터 이외의 기관 들어오는경우 enum으로 수정
  private MydataSector mydataSector;
}

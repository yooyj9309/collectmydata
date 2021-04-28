package com.banksalad.collectmydata.mock.common.api.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequestWrapper {

  private Long banksaladUserId;
  private String orgCode;
  private Long searchTimestamp;

  public ApiRequestWrapper setBanksaladUserId(Long banksaladUserId) {
    this.banksaladUserId = banksaladUserId;
    return this;
  }
}

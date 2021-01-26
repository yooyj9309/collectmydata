package com.banksalad.collectmydata.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAuthInfo {

  private String token;
  private Long banksaladUserId;
  private String os;
}

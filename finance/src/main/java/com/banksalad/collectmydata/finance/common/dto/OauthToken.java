package com.banksalad.collectmydata.finance.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OauthToken {

  private final String accessToken;
  // TODO: Do we need the following?
  private final String refreshToken;
  private final List<String> scopes;
}

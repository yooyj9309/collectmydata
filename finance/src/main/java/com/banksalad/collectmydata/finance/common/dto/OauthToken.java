package com.banksalad.collectmydata.finance.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OauthToken {

  private final String accessToken;
  private final String consentId;
  private final List<String> scopes;
}

package com.banksalad.collectmydata.connect.token.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OauthToken {

  private final String accessToken;
  private final String refreshToken;
  private final String consentId;
  private final List<String> scopes;
}

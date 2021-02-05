package com.banksalad.collectmydata.connect.token.service;

import com.banksalad.collectmydata.connect.token.dto.ExternalTokenResponse;

public interface ExternalTokenService {

  ExternalTokenResponse issueToken(String organizationCode, String authorizationCode);

  ExternalTokenResponse refreshToken(String organizationCode, String refreshToken);

  void revokeToken(String organizationCode, String accessToken);
}

package com.banksalad.collectmydata.connect.token.service;

import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.token.dto.GetTokenResponse;

public interface ExternalTokenService {

  GetTokenResponse issueToken(Organization organization, String authorizationCode);

  GetTokenResponse refreshToken(Organization organization, String refreshToken);

  void revokeToken(Organization organization, String accessToken);
}

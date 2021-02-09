package com.banksalad.collectmydata.connect.token.service;

import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.token.dto.ExternalTokenResponse;

public interface ExternalTokenService {

  ExternalTokenResponse issueToken(Organization organization, String authorizationCode);

  ExternalTokenResponse refreshToken(Organization organization, String refreshToken);

  void revokeToken(Organization organization, String accessToken);
}

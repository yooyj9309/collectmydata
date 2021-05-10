package com.banksalad.collectmydata.connect.token.service;

import com.banksalad.collectmydata.connect.token.dto.OauthToken;

public interface OauthTokenService {

  OauthToken issueToken(long banksaladUserId, String organizationId, String authorizationCode);

  OauthToken getAccessToken(long banksaladUserId, String organizationId);

  OauthToken refreshToken(long banksaladUserId, String organizationId);

  void revokeToken(long banksaladUserId, String organizationId);

  void revokeAllTokens(long banksaladUserId);
}

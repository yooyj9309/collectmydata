package com.banksalad.collectmydata.connect.token.service;

import com.banksalad.collectmydata.connect.token.dto.OauthToken;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeTokenRequest;

public interface OauthTokenService {

  OauthToken issueToken(IssueTokenRequest request);

  OauthToken getAccessToken(long banksaladUserId, String organizationId);

  OauthToken refreshToken(long banksaladUserId, String organizationId);

  void revokeToken(RevokeTokenRequest request);

  void revokeAllTokens(RevokeAllTokensRequest request);
}

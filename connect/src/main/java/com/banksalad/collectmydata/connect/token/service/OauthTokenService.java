package com.banksalad.collectmydata.connect.token.service;

import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RefreshTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeTokenRequest;

public interface OauthTokenService {

  OauthToken issueToken(IssueTokenRequest request);

  OauthToken getAccessToken(GetAccessTokenRequest request);

  OauthToken refreshToken(RefreshTokenRequest request);

  void revokeToken(RevokeTokenRequest request);

  void revokeAllTokens(RevokeAllTokensRequest request);
}

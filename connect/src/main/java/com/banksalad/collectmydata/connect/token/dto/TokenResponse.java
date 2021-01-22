package com.banksalad.collectmydata.connect.token.dto;

import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.GetAccessTokenResponse;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.IssueTokenResponse;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.RefreshTokenResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {

  private OauthToken oauthToken;

  public IssueTokenResponse toIssueTokenResponseProto() {
    return IssueTokenResponse.newBuilder()
        .setAccessToken(oauthToken.getAccessToken())
        .setRefreshToken(oauthToken.getRefreshToken())
        .build();
  }

  public GetAccessTokenResponse toGetAccessTokenResponseProto() {
    return GetAccessTokenResponse.newBuilder()
        .setAccessToken(oauthToken.getAccessToken())
        .build();
  }

  public RefreshTokenResponse toRefreshTokenResponseProto() {
    return RefreshTokenResponse.newBuilder()
        .setAccessToken(oauthToken.getAccessToken())
        .build();
  }
}

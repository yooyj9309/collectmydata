package com.banksalad.collectmydata.connect.token.dto;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetAccessTokenResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.IssueTokenResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.RefreshTokenResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OauthTokenProtoResponse {

  private final OauthToken oauthToken;

  public IssueTokenResponse toIssueTokenResponseProto() {
    return IssueTokenResponse.newBuilder()
        .setAccessToken(oauthToken.getAccessToken())
        .setRefreshToken(oauthToken.getRefreshToken())
        .build();
  }

  public GetAccessTokenResponse toGetAccessTokenResponseProto() {
    return GetAccessTokenResponse.newBuilder()
        .setAccessToken(oauthToken.getAccessToken())
        .setConsentId(oauthToken.getConsentId())
        .addAllScope(oauthToken.getScopes())
        .build();
  }

  public RefreshTokenResponse toRefreshTokenResponseProto() {
    return RefreshTokenResponse.newBuilder()
        .setAccessToken(oauthToken.getAccessToken())
        .build();
  }
}

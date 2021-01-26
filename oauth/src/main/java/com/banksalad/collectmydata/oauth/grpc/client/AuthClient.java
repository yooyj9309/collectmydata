package com.banksalad.collectmydata.oauth.grpc.client;

import com.banksalad.collectmydata.oauth.common.enums.OauthErrorType;
import com.banksalad.collectmydata.oauth.common.exception.OauthException;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.auth.AuthGrpc.AuthBlockingStub;
import com.github.banksalad.idl.apis.v1.auth.AuthProto.LegacyGetTokenRequest;
import com.github.banksalad.idl.apis.v1.auth.AuthProto.LegacyGetTokenResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthClient {

  private final AuthBlockingStub authBlockingStub;

  public LegacyGetTokenResponse getUserAuthInfoByToken(String organizationId, String token) {
    LegacyGetTokenRequest request = LegacyGetTokenRequest.newBuilder()
        .setAccessToken(token)
        .build();
    try {
      return authBlockingStub.legacyGetToken(request);
    } catch (Exception e) {
      throw new OauthException(OauthErrorType.FAILED_AUTH_TOKEN_RPC, organizationId);
    }
  }
}

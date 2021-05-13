package com.banksalad.collectmydata.connect.grpc.client;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.auth.AuthGrpc.AuthBlockingStub;
import com.github.banksalad.idl.apis.v1.auth.AuthProto.LegacyGetTokenRequest;
import com.github.banksalad.idl.apis.v1.auth.AuthProto.LegacyGetTokenResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthClient {

  private final AuthBlockingStub authBlockingStub;

  public LegacyGetTokenResponse getUserAuthInfoByToken(String accessToken) {
    return authBlockingStub.legacyGetToken(LegacyGetTokenRequest.newBuilder()
        .setAccessToken(accessToken)
        .build());
  }
}

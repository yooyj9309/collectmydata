package com.banksalad.collectmydata.oauth.grpc.client;

import com.banksalad.collectmydata.oauth.dto.UserAuthInfo;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.external.v1.auth.AuthGrpc.AuthBlockingStub;
import com.github.banksalad.idl.apis.external.v1.auth.AuthProto.LegacyGetTokenRequest;
import com.github.banksalad.idl.apis.external.v1.auth.AuthProto.LegacyGetTokenResponse;
import com.github.banksalad.idl.apis.external.v1.auth.AuthProto.LegacyTokenPayload;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AuthClient {

  private final AuthBlockingStub authBlockingStub;
  private static final String AUTH_KEY = "Banksalad-Access-Token";
  private static final String DEVICE_OS = "Banksalad-Application-Name";

  public AuthClient(AuthBlockingStub authBlockingStub) {
    this.authBlockingStub = authBlockingStub;
  }

  public UserAuthInfo getUserAuthInfoByToken(Map<String, String> headers) {
    String token = Optional.of(headers.get(AUTH_KEY)).orElseThrow(() -> new NoSuchElementException());
    String os = Optional.of(headers.get(DEVICE_OS)).orElse("UNKNOWN");
    LegacyGetTokenRequest request = LegacyGetTokenRequest.newBuilder()
        .setAccessToken(token)
        .build();

    LegacyGetTokenResponse response = authBlockingStub.legacyGetToken(request);

    if (LegacyTokenPayload.getDefaultInstance() == response.getData().getPayload()) {
      // TODO EXCEPTION 처리
    }

    int userId = response.getData().getPayload().getUserId();
    return UserAuthInfo.builder()
        .banksaladUserId(userId)
        .os(os)
        .token(token)
        .build();
  }

}

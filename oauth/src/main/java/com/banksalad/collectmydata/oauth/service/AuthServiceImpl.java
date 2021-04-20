package com.banksalad.collectmydata.oauth.service;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.oauth.common.enums.OauthErrorType;
import com.banksalad.collectmydata.oauth.common.exception.OauthException;
import com.banksalad.collectmydata.oauth.dto.UserAuthInfo;
import com.banksalad.collectmydata.oauth.grpc.client.AuthClient;
import com.github.banksalad.idl.apis.v1.auth.AuthProto.LegacyGetTokenResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private static final String LEGACY_AUTH_KEY = "Banksalad-Access-Token";
  private static final String DEVICE_OS = "banksalad-application-name";
  private static final String AUTH_KEY = "Authorization";

  private final AuthClient authClient;

  @Override
  public UserAuthInfo getUserAuthInfo(String organizationId, ServerHttpRequest httpRequest) {
    String token = Optional.ofNullable(
        Optional.ofNullable(getHeader(httpRequest, AUTH_KEY)).orElseGet(() -> getHeader(httpRequest, LEGACY_AUTH_KEY))
    ).orElseThrow(() -> new OauthException(OauthErrorType.NOT_FOUND_BANKSALAD_TOKEN, organizationId));
    String os = Optional.of(getHeader(httpRequest, DEVICE_OS)).orElse("UNKNOWN");

    LegacyGetTokenResponse response = authClient.getUserAuthInfoByToken(organizationId, token);

    return userAuthInfoAssembler(token, os, response);
  }

  private UserAuthInfo userAuthInfoAssembler(String token, String os, LegacyGetTokenResponse response) {
    int banksaladUserId = response.getData().getPayload().getUserId();
    return UserAuthInfo.builder()
        .banksaladUserId(Long.valueOf(banksaladUserId))
        .os(os)
        .token(token)
        .build();
  }

  private String getHeader(ServerHttpRequest httpRequest, String key) {
    List<String> values = httpRequest.getHeaders().get(key);

    if (values != null && values.size() != 0) {
      return values.get(0);
    }
    return null;
  }
}

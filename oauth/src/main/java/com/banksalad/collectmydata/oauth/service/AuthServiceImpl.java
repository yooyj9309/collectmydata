package com.banksalad.collectmydata.oauth.service;

import com.banksalad.collectmydata.oauth.common.enums.OauthErrorType;
import com.banksalad.collectmydata.oauth.common.exception.OauthException;
import com.banksalad.collectmydata.oauth.dto.UserAuthInfo;
import com.banksalad.collectmydata.oauth.grpc.client.AuthClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.auth.AuthProto.LegacyGetTokenResponse;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private static final String AUTH_KEY = "Banksalad-Access-Token";
  private static final String DEVICE_OS = "Banksalad-Application-Name";

  @Autowired
  private AuthClient authClient;


  @Override
  public UserAuthInfo getUserAuthInfo(String organizationId, Map<String, String> headers) {
    String token = Optional.of(headers.get(AUTH_KEY))
        .orElseThrow(() -> new OauthException(OauthErrorType.NOT_FOUND_BANKSALAD_TOKEN, organizationId));
    String os = Optional.of(headers.get(DEVICE_OS)).orElse("UNKNOWN");

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
}

package com.banksalad.collectmydata.finance.common.service;

import com.banksalad.collectmydata.finance.common.dto.OauthToken;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OauthTokenServiceImpl implements OauthTokenService {

  @Override
  public OauthToken getOauthToken(long banksaladUserId, String OrganizationId) {
    // TODO: Grpc call to connect service
    return OauthToken.builder()
        .accessToken("xxx.yyy.zzz")
        .build();
  }
}

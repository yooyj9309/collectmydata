package com.banksalad.collectmydata.connect.token.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.IssueTokenRequest;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OauthTokenService {

  private final OauthTokenRepository oauthTokenRepository;

  @Transactional
  public OauthToken issueToken(IssueTokenRequest request) {

    /**
     * TODO
     * 1. 기관 인증페이지에 토큰발행 요청
     * 2. 발행받은 토큰정보 DB 저장
     * 3. DTO 생성 후 필요한 정보만 가공하여 응답
     */
    String accessToken = "access_token_ok";
    String refreshToken = "refresh_token_ok";
    List<String> scopes = new ArrayList<>();

    return OauthToken.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .scopes(scopes)
        .build();
  }
}

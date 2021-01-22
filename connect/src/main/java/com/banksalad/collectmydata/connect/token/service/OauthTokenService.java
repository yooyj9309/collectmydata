package com.banksalad.collectmydata.connect.token.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.RefreshTokenRequest;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.RevokeTokenRequest;
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
     * 1. 기관 인증페이지에 authorization code 및 기관 정보 바탕으로 토큰발행 요청 : webClient
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

  public OauthToken getAccessToken(GetAccessTokenRequest request) {

    /**
     * TODO
     * 1. request 정보를 key 값으로 DB 조회
     *  - 토큰이 존재하지 않으면 예외처리 진행
     *  - 토큰 만료기관 확인하는 로직 추가
     * 2. DTO 생성 후 필요한 정보만 가공하여 응답
     */
    String accessToken = "access_token_ok2";
    List<String> scopes = new ArrayList<>();

    return OauthToken.builder()
        .accessToken(accessToken)
        .scopes(scopes)
        .build();
  }

  public OauthToken refreshToken(RefreshTokenRequest request) {

    /**
     * TODO
     * 1. DB를 조회하고 refresh token 만료시간 확인 후 유효하지 않다면 예외처리 진행
     * 2. 기관 인증페이지에 기관 정보 바탕으로 토큰갱신 요청 : webClient
     * 3. 갱신받은 토큰정보 DB 저장
     * 4. DTO 생성 후 필요한 정보만 가공하여 응답
     */
    String accessToken = "access_token_ok3";
    List<String> scopes = new ArrayList<>();

    return OauthToken.builder()
        .accessToken(accessToken)
        .scopes(scopes)
        .build();
  }

  public void revokeToken(RevokeTokenRequest request) {

    /**
     * TODO
     * 1. 토큰정보 DB에서 제거
     * 2. 기관 인증페이지에 기관 정보 바탕으로 토큰폐기 요청 : webClient
     */
  }

  public void revokeAllTokens(RevokeAllTokensRequest request) {
    /**
     * TODO
     * 1. 사용자의 연동되어 있는 모든 토큰정보 DB에서 제거
     * 2. 기관 인증페이지에 기관 정보 바탕으로 토큰폐기 요청 : webClient
     */
  }
}

package com.banksalad.collectmydata.oauth.common.enums;

import lombok.Getter;

@Getter
public enum OauthErrorType {

  USER_NOT_FOUND("User 조회에 실패하였습니다.", "user_not_found"),
  FAILED_TO_SAVE_USER("유저 데이터 저장에 실패했습니다.", "failed_to_save_user"),

  INVALID_SECTOR("옳바른 Sector가 아닙니다.", "invalid_sector"),

  NOT_FOUND_BANKSALAD_TOKEN("토큰정보를 찾을 수 없습니다.", "not_found_banksalad_token"),
  FAILED_CONNECT_ORGANIZATION_RPC("Connectmydata Client 에러, 기관정보 조회에 실패하였습니다.", "failed_connect_organization_rpc"),
  FAILED_CONNECT_ISSUETOKEN_RPC("Connectmydata Client 에러, 토큰 발급에 실패하였습니다.", "failed_connect_issuetoken_rpc"),

  FAILED_AUTH_TOKEN_RPC("Auth Clinet 에러, 토큰조회에 실패하였습니다.", "failed_auth_token_rpc");

  private String errorMsg;
  private String tagId;

  OauthErrorType(String errorMsg, String tagId) {
    this.errorMsg = errorMsg;
    this.tagId = tagId;
  }
}

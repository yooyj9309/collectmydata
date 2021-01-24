package com.banksalad.collectmydata.oauth.common.enums;

import lombok.Getter;

@Getter
public enum OauthErrorType {

  USER_NOT_FOUND("User 조회에 실패하였습니다.", "user_not_found"),
  FAILED_TO_SAVE_USER("유저 데이터 저장에 실패했습니다.", "failed_to_save_user"),
  INVALID_SECTOR("옳바른 Sector가 아닙니다.", "invalid_sector");

  private String errorMsg;
  private String tagId;

  OauthErrorType(String errorMsg, String tagId) {
    this.errorMsg = errorMsg;
    this.tagId = tagId;
  }
}

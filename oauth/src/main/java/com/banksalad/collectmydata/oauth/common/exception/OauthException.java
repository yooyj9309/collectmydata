package com.banksalad.collectmydata.oauth.common.exception;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.oauth.common.enums.OauthErrorType;

import lombok.Getter;

@Getter
public class OauthException extends CollectRuntimeException {

  private String organizationId;
  private String tag;

  public OauthException(OauthErrorType errorType, String organizationId) {
    super(errorType.getErrorMsg());
    this.organizationId = organizationId;
    this.tag = errorType.getTagId();
  }

  public OauthException(String message, Throwable cause) {
    super(message, cause);
  }
}

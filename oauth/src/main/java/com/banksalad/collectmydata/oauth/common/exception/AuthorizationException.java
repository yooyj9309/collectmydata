package com.banksalad.collectmydata.oauth.common.exception;


import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.oauth.common.enums.AuthorizationResultType;

import lombok.Getter;

@Getter
public class AuthorizationException extends CollectRuntimeException {

  private String organizationId;
  private AuthorizationResultType authorizationResultType;

  public AuthorizationException(String message, String organizationId) {
    super(message);
    this.organizationId = organizationId;
  }

  public AuthorizationException(AuthorizationResultType authorizationResultType, String organizationId) {
    super(authorizationResultType.getError());
    this.organizationId = organizationId;
    this.authorizationResultType = authorizationResultType;
  }

  public AuthorizationException(String message, Throwable cause) {
    super(message, cause);
  }
}

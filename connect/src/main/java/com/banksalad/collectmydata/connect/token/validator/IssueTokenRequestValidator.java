package com.banksalad.collectmydata.connect.token.validator;

import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.IssueTokenRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import lombok.Builder;

@Builder
public class IssueTokenRequestValidator {

  @Positive(message = "banksaladId should be positive number")
  @NotEmpty(message = "banksaladId should be exist")
  private String banksaladId;

  @NotEmpty(message = "organizationId should be exist")
  private String organizationId;

  @NotEmpty(message = "authorizationCode should be exist")
  private String authorizationCode;

  private IssueTokenRequestValidator(String banksaladId, String organizationId, String authorizationCode) {
    this.banksaladId = banksaladId;
    this.organizationId = organizationId;
    this.authorizationCode = authorizationCode;
  }

  public static IssueTokenRequestValidator of(IssueTokenRequest request) {
    return IssueTokenRequestValidator.builder()
        .banksaladId(request.getBanksaladUserId())
        .organizationId(request.getOrganizationObjectid()) // request.getOrganizationId()로 변경 예정(connect-mydata IDL)
        .authorizationCode(request.getAuthorizationCode())
        .build();
  }
}


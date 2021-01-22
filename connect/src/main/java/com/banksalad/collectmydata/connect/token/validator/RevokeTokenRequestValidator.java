package com.banksalad.collectmydata.connect.token.validator;

import com.github.banksalad.idl.apis.external.v1.connect.ConnectProto.RevokeTokenRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import lombok.Builder;

@Builder
public class RevokeTokenRequestValidator {

  @Positive(message = "banksaladId should be positive number")
  @NotEmpty(message = "banksaladId should be exist")
  private String banksaladId;

  @NotEmpty(message = "organizationId should be exist")
  private String organizationId;

  private RevokeTokenRequestValidator(String banksaladId, String organizationId) {
    this.banksaladId = banksaladId;
    this.organizationId = organizationId;
  }

  public static RevokeTokenRequestValidator of(RevokeTokenRequest request) {
    return RevokeTokenRequestValidator.builder()
        .banksaladId(request.getBanksaladUserId())
        .organizationId(request.getOrganizationObjectid()) // request.getOrganizationId()로 변경 예정(connect-mydata IDL)
        .build();
  }
}

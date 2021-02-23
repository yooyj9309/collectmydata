package com.banksalad.collectmydata.connect.grpc.validator;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RefreshTokenRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import lombok.Builder;

@Builder
public class RefreshTokenRequestValidator {

  @Positive(message = "banksaladId should be positive number")
  @NotEmpty(message = "banksaladId should be exist")
  private String banksaladId;

  @NotEmpty(message = "organizationId should be exist")
  private String organizationId;

  private RefreshTokenRequestValidator(String banksaladId, String organizationId) {
    this.banksaladId = banksaladId;
    this.organizationId = organizationId;
  }

  public static RefreshTokenRequestValidator of(RefreshTokenRequest request) {
    return RefreshTokenRequestValidator.builder()
        .banksaladId(request.getBanksaladUserId())
        .organizationId(request.getOrganizationId())
        .build();
  }
}

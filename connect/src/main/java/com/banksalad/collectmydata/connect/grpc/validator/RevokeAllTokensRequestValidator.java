package com.banksalad.collectmydata.connect.grpc.validator;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeAllTokensRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import lombok.Builder;

@Builder
public class RevokeAllTokensRequestValidator {

  @Positive(message = "banksaladId should be positive number")
  @NotEmpty(message = "banksaladId should be exist")
  private String banksaladId;

  private RevokeAllTokensRequestValidator(String banksaladId) {
    this.banksaladId = banksaladId;
  }

  public static RevokeAllTokensRequestValidator of(RevokeAllTokensRequest request) {
    return RevokeAllTokensRequestValidator.builder()
        .banksaladId(request.getBanksaladUserId())
        .build();
  }
}

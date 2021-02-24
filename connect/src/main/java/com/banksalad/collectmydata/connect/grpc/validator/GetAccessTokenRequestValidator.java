package com.banksalad.collectmydata.connect.grpc.validator;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import io.netty.util.internal.StringUtil;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class GetAccessTokenRequestValidator {

  @Positive(message = "banksaladUserId should be positive number")
  @NotNull(message = "banksaladUserId should be exist")
  private final Long banksaladUserId;

  @NotEmpty(message = "organizationId should be exist")
  private final String organizationId;

  public static GetAccessTokenRequestValidator of(GetAccessTokenRequest request) {
    return new GetAccessTokenRequestValidator(
        StringUtil.isNullOrEmpty(request.getBanksaladUserId()) ? null : Long.valueOf(request.getBanksaladUserId()),
        request.getOrganizationId()
    );
  }
}

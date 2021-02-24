package com.banksalad.collectmydata.connect.grpc.validator;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeAllTokensRequest;
import io.netty.util.internal.StringUtil;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class RevokeAllTokensRequestValidator {

  @Positive(message = "banksaladUserId should be positive number")
  @NotNull(message = "banksaladUserId should be exist")
  private final Long banksaladUserId;

  public static RevokeAllTokensRequestValidator of(RevokeAllTokensRequest request) {
    return new RevokeAllTokensRequestValidator(
        StringUtil.isNullOrEmpty(request.getBanksaladUserId()) ? null : Long.valueOf(request.getBanksaladUserId())
    );
  }
}

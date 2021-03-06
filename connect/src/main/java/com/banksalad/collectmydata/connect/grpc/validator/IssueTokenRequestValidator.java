package com.banksalad.collectmydata.connect.grpc.validator;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.IssueTokenRequest;
import io.netty.util.internal.StringUtil;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class IssueTokenRequestValidator {

  @Positive(message = "banksaladUserId should be positive number")
  @NotNull(message = "banksaladUserId should be exist")
  private final Long banksaladUserId;

  @NotEmpty(message = "organizationId should be exist")
  private final String organizationId;

  @NotEmpty(message = "authorizationCode should be exist")
  private final String authorizationCode;

  public static IssueTokenRequestValidator of(IssueTokenRequest request) {
    return new IssueTokenRequestValidator(
        StringUtil.isNullOrEmpty(request.getBanksaladUserId()) ? null : Long.valueOf(request.getBanksaladUserId()),
        request.getOrganizationId(),
        request.getAuthorizationCode()
    );
  }
}

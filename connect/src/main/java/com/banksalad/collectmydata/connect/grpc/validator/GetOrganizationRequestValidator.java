package com.banksalad.collectmydata.connect.grpc.validator;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationIdRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationObjectidRequest;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class GetOrganizationRequestValidator {

  @NotEmpty(message = "There is no institution id parameter.")
  private final String id;

  public static GetOrganizationRequestValidator of(GetOrganizationByOrganizationObjectidRequest request) {
    return new GetOrganizationRequestValidator(request.getOrganizationObjectid());
  }

  public static GetOrganizationRequestValidator of(GetOrganizationByOrganizationIdRequest request) {
    return new GetOrganizationRequestValidator(request.getOrganizationId());
  }
}

package com.banksalad.collectmydata.connect.grpc.validator;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationGuidRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationIdRequest;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class GetOrganizationRequestValidator {

  @NotEmpty(message = "There is no institution id parameter.")
  private final String id;

  public static GetOrganizationRequestValidator of(GetOrganizationByOrganizationGuidRequest request) {
    return new GetOrganizationRequestValidator(request.getOrganizationGuid());
  }

  public static GetOrganizationRequestValidator of(GetOrganizationByOrganizationIdRequest request) {
    return new GetOrganizationRequestValidator(request.getOrganizationId());
  }
}

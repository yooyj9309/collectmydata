package com.banksalad.collectmydata.connect.grpc.validator;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationObjectidRequest;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public class GetOrganizationRequestValidator {

  @NotEmpty(message = "objectId should be exist")
  private String organizationObjectid;

  public static GetOrganizationRequestValidator of(GetOrganizationByOrganizationObjectidRequest request) {
    return GetOrganizationRequestValidator.builder()
        .organizationObjectid(request.getOrganizationObjectid())
        .build();
  }
}

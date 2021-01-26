package com.banksalad.collectmydata.connect.organization.validator;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationRequest;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public class GetOrganizationRequestValidator {

  @NotEmpty(message = "objectId should be exist")
  private String organizationObjectid;

  private GetOrganizationRequestValidator(String organizationObjectid) {
    this.organizationObjectid = organizationObjectid;
  }

  public static GetOrganizationRequestValidator of(GetOrganizationRequest request) {
    return GetOrganizationRequestValidator.builder()
        .organizationObjectid(request.getOrganizationObjectid())
        .build();
  }
}

package com.banksalad.collectmydata.connect.organization.service;

import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationIdRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationObjectidRequest;

public interface OrganizationService {

  Organization getOrganization(GetOrganizationByOrganizationObjectidRequest request);

  Organization getOrganization(GetOrganizationByOrganizationIdRequest request);

}

package com.banksalad.collectmydata.connect.organization.service;

import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationRequest;

public interface OrganizationService {

  Organization getOrganization(GetOrganizationRequest request);
}

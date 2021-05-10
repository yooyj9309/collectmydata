package com.banksalad.collectmydata.connect.organization.service;

import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationGuidRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationIdRequest;

public interface OrganizationService {

  Organization getOrganization(GetOrganizationByOrganizationGuidRequest request);

  Organization getOrganization(GetOrganizationByOrganizationIdRequest request);
}

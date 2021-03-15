package com.banksalad.collectmydata.common.organization;

@Deprecated
public interface OrganizationService {

  Organization getOrganization(String sector, String industry, String organizationId);
}

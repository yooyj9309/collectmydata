package com.banksalad.collectmydata.finance.common.service;


import com.banksalad.collectmydata.finance.common.dto.Organization;

public interface OrganizationService {

  Organization getOrganizationByObjectId(String organizationObjectId);

  Organization getOrganizationById(String organizationId);
}

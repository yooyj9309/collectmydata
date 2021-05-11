package com.banksalad.collectmydata.connect.publishment.organization.service;

import com.banksalad.collectmydata.connect.publishment.organization.dto.OrganizationForFinance;

import java.util.List;

public interface OrganizationPublishService {

  List<OrganizationForFinance> listFinanceOrganizations();

  List<OrganizationForFinance> listConnectedFinanceOrganizations(long banksaladUserId);
}

package com.banksalad.collectmydata.finance.common.service;

import com.banksalad.collectmydata.finance.common.dto.Organization;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrganizationServiceImpl implements OrganizationService {

  @Override
  public Organization getOrganizationByObjectId(String organizationObjectId) {
    return null;
  }

  @Override
  public Organization getOrganizationById(String organizationId) {
    // TODO: Grpc call to connect service
    return Organization.builder()
        .organizationCode("020")
        .hostUrl("http://localhost:9090")
        .build();
  }
}

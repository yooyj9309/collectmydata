package com.banksalad.collectmydata.connect.organization.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationService {

  @Transactional
  public Organization getOrganization(GetOrganizationRequest request) {

    /**
     * TODO
     * 1. request로 받은 objectid를 통해 DB룰 조회한다.
     * 2. DB에서 조회한 정보를 가공하여 응답한다.
     */
    return Organization.builder()
        .sector("sector OK")
        .industry("industry OK")
        .organizationId("organizationId OK")
        .organizationCode("organizationCode OK")
        .domain("domain OK")
        .build();
  }
}

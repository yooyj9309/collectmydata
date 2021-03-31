package com.banksalad.collectmydata.connect.organization.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationIdRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationByOrganizationObjectidRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

  private final ConnectOrganizationRepository connectOrganizationRepository;

  @Override
  public Organization getOrganization(GetOrganizationByOrganizationObjectidRequest request) {
    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationObjectid(request.getOrganizationObjectid())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

    return organizationAssembler(connectOrganizationEntity);
  }

  @Override
  public Organization getOrganization(GetOrganizationByOrganizationIdRequest request) {
    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationId(request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

    return organizationAssembler(connectOrganizationEntity);
  }

  private Organization organizationAssembler(ConnectOrganizationEntity connectOrganizationEntity) {
    return Organization.builder()
        .sector(connectOrganizationEntity.getSector())
        .industry(connectOrganizationEntity.getIndustry())
        .organizationId(connectOrganizationEntity.getOrganizationId())
        .organizationCode(connectOrganizationEntity.getOrgCode())
        .domain(connectOrganizationEntity.getDomain())
        .build();
  }
}

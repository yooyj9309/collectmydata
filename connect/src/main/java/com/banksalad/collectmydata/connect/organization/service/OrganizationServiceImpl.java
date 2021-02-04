package com.banksalad.collectmydata.connect.organization.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.exception.collectMydataException.NotFoundOrganizationException;
import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

  private final ConnectOrganizationRepository connectOrganizationRepository;

  @Override
  @Transactional(readOnly = true)
  public Organization getOrganization(GetOrganizationRequest request) {
    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationObjectid(request.getOrganizationObjectid()).orElseThrow(NotFoundOrganizationException::new);

    return Organization.builder()
        .sector(connectOrganizationEntity.getSector())
        .industry(connectOrganizationEntity.getIndustry())
        .organizationId(connectOrganizationEntity.getOrganizationId())
        .organizationCode(connectOrganizationEntity.getOrganizationCode())
        .domain(connectOrganizationEntity.getDomain())
        .build();
  }
}

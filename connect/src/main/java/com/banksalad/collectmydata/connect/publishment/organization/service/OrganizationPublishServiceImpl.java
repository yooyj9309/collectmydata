package com.banksalad.collectmydata.connect.publishment.organization.service;

import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.mapper.ConnectOrganizationMapper;
import com.banksalad.collectmydata.connect.publishment.organization.dto.OrganizationForFinance;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationPublishServiceImpl implements OrganizationPublishService {

  private final ConnectOrganizationRepository connectOrganizationRepository;
  private final ConnectOrganizationMapper connectOrganizationMapper = Mappers
      .getMapper(ConnectOrganizationMapper.class);

  @Override
  public List<OrganizationForFinance> listFinanceOrganizations() {

    return connectOrganizationRepository.findAll().stream()
        .map(connectOrganizationMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<OrganizationForFinance> listConnectedFinanceOrganizations(long banksaladUserId) {

    // Todo: implement this
    //  1. Get a list of organization that the user connects with a token.
    //  2. Select connect_organization in the list.
    return null;
  }
}

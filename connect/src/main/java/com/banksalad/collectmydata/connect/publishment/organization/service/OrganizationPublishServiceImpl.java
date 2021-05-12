package com.banksalad.collectmydata.connect.publishment.organization.service;

import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.common.mapper.ConnectOrganizationMapper;
import com.banksalad.collectmydata.connect.publishment.organization.dto.OrganizationForFinance;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.common.util.DateUtil.KST_ZONE_ID;

@Service
@RequiredArgsConstructor
public class OrganizationPublishServiceImpl implements OrganizationPublishService {

  private final ConnectOrganizationRepository connectOrganizationRepository;
  private final OauthTokenRepository oauthTokenRepository;

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
    // fixme wooody92 : 토큰 유효성 체크 DB 조회 시 필터링하도록 변경
    return oauthTokenRepository.findAllByBanksaladUserId(banksaladUserId).stream()
        .filter(oauthTokenEntity -> isValidToken(oauthTokenEntity.getAccessTokenExpiresAt()))
        .map(oauthTokenEntity -> connectOrganizationRepository
            .findByOrganizationId(oauthTokenEntity.getOrganizationId())
            .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION)))
        .map(connectOrganizationMapper::entityToDto)
        .collect(Collectors.toList());
  }

  private boolean isValidToken(LocalDateTime expirationTime) {
    return expirationTime.isAfter(LocalDateTime.now(KST_ZONE_ID));
  }
}

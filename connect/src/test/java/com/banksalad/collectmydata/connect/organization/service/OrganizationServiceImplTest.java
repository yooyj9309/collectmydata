package com.banksalad.collectmydata.connect.organization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.exception.collectMydataException.NotFoundOrganizationException;
import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DisplayName("OrganizationService Test")
class OrganizationServiceImplTest {

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private ConnectOrganizationRepository connectOrganizationRepository;

  @Test
  @Transactional
  @DisplayName("organization 정보 조회를 성공하는 테스트")
  public void getOrganization_success() {
    // given
    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity();
    Organization builtOrganization = buildOrganizationFrom(connectOrganizationEntity);
    connectOrganizationRepository.save(connectOrganizationEntity);

    GetOrganizationRequest request = buildGetOrganizationRequest(connectOrganizationEntity.getOrganizationObjectid());

    // when
    Organization organization = organizationService.getOrganization(request);

    // then
    assertThat(organization).usingRecursiveComparison().isEqualTo(builtOrganization);
  }

  @Test
  @Transactional
  @DisplayName("organization 정보 조회를 실패하는 테스트 - 존재하지 않는 organizationObjectid로 조회")
  public void getOrganization_fail() {
    // given
    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity();
    connectOrganizationRepository.save(connectOrganizationEntity);

    GetOrganizationRequest request = buildGetOrganizationRequest("non-exist-organizationObjectid");

    // when, then
    assertThrows(NotFoundOrganizationException.class, () -> organizationService.getOrganization(request));
  }

  private ConnectOrganizationEntity createConnectOrganizationEntity() {
    return ConnectOrganizationEntity.builder()
        .sector("test_finance")
        .industry("test_card")
        .organizationId("test_shinhancard")
        .organizationObjectid("test_objectId")
        .organizationCode("test_001")
        .orgType("test_orgType")
        .organizationStatus("test_organizationStatus")
        .domain("test_shinhancard.com")
        .isRelayOrganization(false)
        .build();
  }

  private Organization buildOrganizationFrom(ConnectOrganizationEntity connectOrganizationEntity) {
    return Organization.builder()
        .sector(connectOrganizationEntity.getSector())
        .industry(connectOrganizationEntity.getIndustry())
        .organizationId(connectOrganizationEntity.getOrganizationId())
        .organizationCode(connectOrganizationEntity.getOrganizationCode())
        .domain(connectOrganizationEntity.getDomain())
        .build();
  }

  private GetOrganizationRequest buildGetOrganizationRequest(String organizationObjectid) {
    return GetOrganizationRequest.newBuilder()
        .setOrganizationObjectid(organizationObjectid)
        .build();
  }
}

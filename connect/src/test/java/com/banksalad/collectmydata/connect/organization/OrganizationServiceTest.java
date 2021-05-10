package com.banksalad.collectmydata.connect.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.organization.service.OrganizationService;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationGuidRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationIdRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.banksalad.collectmydata.connect.common.ConnectConstant.*;
import static java.lang.Boolean.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DisplayName("OrganizationService Test")
class OrganizationServiceTest {

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private ConnectOrganizationRepository connectOrganizationRepository;

  private ConnectOrganizationEntity connectOrganizationEntity;

  @BeforeEach
  void setUp() {
    connectOrganizationEntity = getConnectOrganizationEntity();
    connectOrganizationRepository.save(connectOrganizationEntity);
  }

  @AfterEach
  void tearDown() {
    connectOrganizationRepository.deleteAll();
  }

  @Test
  @Transactional
  @DisplayName("organization 정보 조회 성공 : organizationObjectid로 조회")
  public void getOrganizationByObjectIid_success() {
    // given
    Organization givenOrganization = getOrganization();
    GetOrganizationByOrganizationGuidRequest request = getOrganizationByOrganizationGuidRequest(
        connectOrganizationEntity.getOrganizationObjectid());

    // when
    Organization organization = organizationService.getOrganization(request);

    // then
    assertThat(organization).usingRecursiveComparison().isEqualTo(givenOrganization);
  }

  @Test
  @Transactional
  @DisplayName("organization 정보 조회 실패 : 존재하지 않는 organizationObjectid로 조회")
  public void getOrganizationByObjectid_fail() {
    // given
    GetOrganizationByOrganizationGuidRequest request = getOrganizationByOrganizationGuidRequest(
        "non-exist-organizationObjectid");

    // when, then
    Exception responseException = assertThrows(Exception.class, () -> organizationService.getOrganization(request));
    assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals(ConnectErrorType.NOT_FOUND_ORGANIZATION.getMessage(), responseException.getMessage());
  }

  @Test
  @Transactional
  @DisplayName("organization 정보 조회 성공 : organizationId로 조회")
  public void getOrganizationByOrganizationId_success() {
    // given
    Organization givenOrganization = getOrganization();
    GetOrganizationByOrganizationIdRequest request = getOrganizationByOrganizationIdRequest(
        connectOrganizationEntity.getOrganizationId());

    // when
    Organization organization = organizationService.getOrganization(request);

    // then
    assertThat(organization).usingRecursiveComparison().isEqualTo(givenOrganization);
  }

  @Test
  @Transactional
  @DisplayName("organization 정보 조회 실패 : 존재하지 않는 organizationId로 조회")
  public void getOrganizationByOrganizationId_fail() {
    // given
    GetOrganizationByOrganizationIdRequest request = getOrganizationByOrganizationIdRequest(
        "non-exist-organizationObjectid");

    // when, then
    Exception responseException = assertThrows(Exception.class, () -> organizationService.getOrganization(request));
    assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals(ConnectErrorType.NOT_FOUND_ORGANIZATION.getMessage(), responseException.getMessage());
  }


  private ConnectOrganizationEntity getConnectOrganizationEntity() {
    return ConnectOrganizationEntity.builder()
        .sector(SECTOR)
        .industry(INDUSTRY)
        .organizationId(ORGANIZATION_ID)
        .organizationObjectid(ORGANIZATION_OBJECT_ID)
        .orgCode(ORGANIZATION_CODE)
        .organizationStatus(ORGANIZATION_STATUS)
        .domain(DOMAIN)
        .deleted(FALSE)
        .build();
  }

  private Organization getOrganization() {
    return Organization.builder()
        .sector(SECTOR)
        .industry(INDUSTRY)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .domain(DOMAIN)
        .build();
  }

  private GetOrganizationByOrganizationGuidRequest getOrganizationByOrganizationGuidRequest(
      String organizationObjectid) {
    return GetOrganizationByOrganizationGuidRequest.newBuilder()
        .setOrganizationGuid(organizationObjectid)
        .build();
  }

  private GetOrganizationByOrganizationIdRequest getOrganizationByOrganizationIdRequest(String organizationId) {
    return GetOrganizationByOrganizationIdRequest.newBuilder()
        .setOrganizationId(organizationId)
        .build();
  }
}

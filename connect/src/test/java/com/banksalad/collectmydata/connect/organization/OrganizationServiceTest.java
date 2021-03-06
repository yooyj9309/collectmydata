package com.banksalad.collectmydata.connect.organization;

import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.organization.service.OrganizationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationGuidRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetOrganizationByOrganizationIdRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.banksalad.collectmydata.connect.common.ConnectConstant.DOMAIN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.INDUSTRY;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_GUID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_STATUS;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.SECTOR;
import static java.lang.Boolean.FALSE;
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
  @DisplayName("organization ?????? ?????? ?????? : organizationGuid??? ??????")
  public void getOrganizationByObjectIid_success() {
    // given
    Organization givenOrganization = getOrganization();
    GetOrganizationByOrganizationGuidRequest request = getOrganizationByOrganizationGuidRequest(
        connectOrganizationEntity.getOrganizationGuid());

    // when
    Organization organization = organizationService.getOrganization(request);

    // then
    assertThat(organization).usingRecursiveComparison().isEqualTo(givenOrganization);
  }

  @Test
  @Transactional
  @DisplayName("organization ?????? ?????? ?????? : ???????????? ?????? organizationGuid??? ??????")
  public void getOrganizationByGuid_fail() {
    // given
    GetOrganizationByOrganizationGuidRequest request = getOrganizationByOrganizationGuidRequest(
        "non-exist-organizationGuid");

    // when, then
    Exception responseException = assertThrows(Exception.class, () -> organizationService.getOrganization(request));
    assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals(ConnectErrorType.NOT_FOUND_ORGANIZATION.getMessage(), responseException.getMessage());
  }

  @Test
  @Transactional
  @DisplayName("organization ?????? ?????? ?????? : organizationId??? ??????")
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
  @DisplayName("organization ?????? ?????? ?????? : ???????????? ?????? organizationId??? ??????")
  public void getOrganizationByOrganizationId_fail() {
    // given
    GetOrganizationByOrganizationIdRequest request = getOrganizationByOrganizationIdRequest(
        "non-exist-organizationGuid");

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
        .organizationGuid(ORGANIZATION_GUID)
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
      String organizationGuid) {
    return GetOrganizationByOrganizationGuidRequest.newBuilder()
        .setOrganizationGuid(organizationGuid)
        .build();
  }

  private GetOrganizationByOrganizationIdRequest getOrganizationByOrganizationIdRequest(String organizationId) {
    return GetOrganizationByOrganizationIdRequest.newBuilder()
        .setOrganizationId(organizationId)
        .build();
  }
}

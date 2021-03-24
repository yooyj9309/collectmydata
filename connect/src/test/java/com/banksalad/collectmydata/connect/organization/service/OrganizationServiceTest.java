package com.banksalad.collectmydata.connect.organization.service;

import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.DisplayName;

@SpringBootTest
@DisplayName("OrganizationService Test")
class OrganizationServiceTest {

//  @Autowired
//  private OrganizationService organizationService;
//
//  @Autowired
//  private ConnectOrganizationRepository connectOrganizationRepository;
//
//  @Test
//  @Transactional
//  @DisplayName("organization 정보 조회를 성공하는 테스트")
//  public void getOrganizationByObjectIid_success() {
//    // given
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity();
//    Organization builtOrganization = buildOrganizationFrom(connectOrganizationEntity);
//    connectOrganizationRepository.save(connectOrganizationEntity);
//
//    GetOrganizationByOrganizationObjectidRequest request = buildGetOrganizationByOrganizationObjectidRequest(
//        connectOrganizationEntity.getOrganizationObjectid());
//
//    // when
//    Organization organization = organizationService.getOrganization(request);
//
//    // then
//    assertThat(organization).usingRecursiveComparison().isEqualTo(builtOrganization);
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("organization 정보 조회를 실패하는 테스트 - 존재하지 않는 organizationObjectid로 조회")
//  public void getOrganizationByObjectid_fail() {
//    // given
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity();
//    connectOrganizationRepository.save(connectOrganizationEntity);
//
//    GetOrganizationByOrganizationObjectidRequest request = buildGetOrganizationByOrganizationObjectidRequest(
//        "non-exist-organizationObjectid");
//
//    // when, then
//    Exception responseException = assertThrows(Exception.class, () -> organizationService.getOrganization(request));
//    assertThat(responseException).isInstanceOf(ConnectException.class);
//    assertEquals(ConnectErrorType.NOT_FOUND_ORGANIZATION.getMessage(), responseException.getMessage());
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("organization 정보 조회를 성공하는 테스트 : OrganizationId 이용")
//  public void getOrganizationByOrganizationId_success() {
//    // given
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity();
//    Organization builtOrganization = buildOrganizationFrom(connectOrganizationEntity);
//    connectOrganizationRepository.save(connectOrganizationEntity);
//
//    GetOrganizationByOrganizationIdRequest request = buildGetOrganizationByOrganizationIdRequest(
//        connectOrganizationEntity.getOrganizationId());
//
//    // when
//    Organization organization = organizationService.getOrganization(request);
//
//    // then
//    assertThat(organization).usingRecursiveComparison().isEqualTo(builtOrganization);
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("organization 정보 조회를 실패하는 테스트 - 존재하지 않는 OrganizationId로 조회")
//  public void getOrganizationByOrganizationId_fail() {
//    // given
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity();
//    connectOrganizationRepository.save(connectOrganizationEntity);
//
//    GetOrganizationByOrganizationIdRequest request = buildGetOrganizationByOrganizationIdRequest(
//        "non-exist-organizationObjectid");
//
//    // when, then
//    Exception responseException = assertThrows(Exception.class, () -> organizationService.getOrganization(request));
//    assertThat(responseException).isInstanceOf(ConnectException.class);
//    assertEquals(ConnectErrorType.NOT_FOUND_ORGANIZATION.getMessage(), responseException.getMessage());
//  }
//
//
//  private ConnectOrganizationEntity createConnectOrganizationEntity() {
//    return ConnectOrganizationEntity.builder()
//        .sector("test_finance")
//        .industry("test_card")
//        .organizationId("test_shinhancard")
//        .organizationObjectid("test_objectId")
//        .orgCode("test_001")
//        .organizationStatus("test_organizationStatus")
//        .build();
//  }
//
//  private Organization buildOrganizationFrom(ConnectOrganizationEntity connectOrganizationEntity) {
//    return Organization.builder()
//        .sector(connectOrganizationEntity.getSector())
//        .industry(connectOrganizationEntity.getIndustry())
//        .organizationId(connectOrganizationEntity.getOrganizationId())
//        .organizationCode(connectOrganizationEntity.getOrgCode())
//        .domain("fixme")// fixme
//        .build();
//  }
//
//  private GetOrganizationByOrganizationObjectidRequest buildGetOrganizationByOrganizationObjectidRequest(
//      String organizationObjectid) {
//    return GetOrganizationByOrganizationObjectidRequest.newBuilder()
//        .setOrganizationObjectid(organizationObjectid)
//        .build();
//  }
//
//  private GetOrganizationByOrganizationIdRequest buildGetOrganizationByOrganizationIdRequest(String organizationId) {
//    return GetOrganizationByOrganizationIdRequest.newBuilder()
//        .setOrganizationId(organizationId)
//        .build();
//  }

}

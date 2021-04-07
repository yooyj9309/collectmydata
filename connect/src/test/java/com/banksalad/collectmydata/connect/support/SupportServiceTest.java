package com.banksalad.collectmydata.connect.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.connect.collect.Executions;
import com.banksalad.collectmydata.connect.common.db.entity.BanksaladClientSecretEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ServiceClientIpEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ServiceEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ApiSyncStatusRepository;
import com.banksalad.collectmydata.connect.common.db.repository.BanksaladClientSecretRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationOauthTokenRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ServiceClientIpRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ServiceRepository;
import com.banksalad.collectmydata.connect.common.enums.SecretType;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationInfo;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceInfo;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceIp;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationTokenResponse;
import com.banksalad.collectmydata.connect.support.service.SupportService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.connect.common.ConnectConstant.ACCESS_TOKEN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.CLIENT_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.CLIENT_SECRET;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ENTITY_IGNORE_FIELD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@SpringBootTest
@Transactional
@DisplayName("SupportService Test")
public class SupportServiceTest {

  @Autowired
  private SupportService supportService;

  @Autowired
  private ApiSyncStatusRepository apiSyncStatusRepository;

  @Autowired
  private BanksaladClientSecretRepository banksaladClientSecretRepository;

  @Autowired
  private OrganizationOauthTokenRepository organizationOauthTokenRepository;

  @Autowired
  private OrganizationRepository organizationRepository;

  @Autowired
  private ServiceRepository financeServiceRepository;

  @Autowired
  private ServiceClientIpRepository financeServiceClientIpRepository;

  @MockBean
  private CollectExecutor collectExecutor;

  @Test
  @DisplayName("기관 정보 조회 - 기존 테이블이 비어있고 데이터를 새로 적재하는경우")
  public void syncOrganizationInfo_success1() {
    setupServer(true);
    setBanksaladClientSecret();
    supportService.syncOrganizationInfo();

    OrganizationEntity entity = organizationRepository.findByOrgCode("020").get();
    assertThat(entity)
        .usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(OrganizationEntity.builder()
            .sector(MydataSector.UNKNOWN.name())
            .industry(Industry.UNKNOWN.name())
            .opType("I")
            .orgCode("020")
            .orgType("01")
            .orgName("기관1")
            .orgRegno("1234567890")
            .corpRegno("1234567890")
            .address("address1")
            .domain("domain1")
            .relayOrgCode("relay_org_code1")
            .build()
        );
  }

  @Test
  @DisplayName("기관 정보 조회 - 기존에 있던 기관 데이터들이 업데이트 되는경우")
  public void syncOrganizationInfo_success2() {
    setBanksaladClientSecret();

    setupServer(true);
    supportService.syncOrganizationInfo();

    // update
    setupUpdateServer(true);
    supportService.syncOrganizationInfo();
    OrganizationEntity entity = organizationRepository.findByOrgCode("020").get();
    assertThat(entity)
        .usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(OrganizationEntity.builder()
            .sector(MydataSector.UNKNOWN.name())
            .industry(Industry.UNKNOWN.name())
            .opType("M")
            .orgCode("020")
            .orgType("03")
            .orgName("기관1-1")
            .orgRegno("1234567890-1")
            .corpRegno("1234567890-1")
            .address("address1-1")
            .domain("domain1-1")
            .relayOrgCode("relay_org_code1-1")
            .build()
        );
  }

  @Test
  @DisplayName("기관 정보 조회 - accessToken과정에서 실패한경우(client_id, client_secret)")
  public void syncOrganizationInfo_fail1() {
    setupServer(true);
    Exception responseException = assertThrows(
        Exception.class,
        () -> supportService.syncOrganizationInfo()
    );

    AssertionsForClassTypes.assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals("NOT_FOUND_CLIENT_ID", responseException.getMessage());
  }

  @Test
  @DisplayName("서비스 정보 조회 - 기존 테이블이 비어있고 데이터를 새로 적재하는경우")
  public void syncOrganizationServiceInfo_success1() {
    setupServer(false);
    setBanksaladClientSecret();
    setOrganization();
    supportService.syncOrganizationServiceInfo();

    List<ServiceEntity> serviceEntities = financeServiceRepository.findAll();
    List<ServiceClientIpEntity> serviceClientIpEntities = financeServiceClientIpRepository.findAll();

    assertEquals(1, serviceEntities.size());
    assertEquals(1, serviceClientIpEntities.size());
    assertThat(serviceEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD).isEqualTo(
        ServiceEntity.builder()
            .organizationId("shinhancard")
            .orgCode("020")
            .serviceName("service1")
            .opType("I")
            .clientId("clientId")
            .clientSecret("clientSecret")
            .redirectUri("http://redirect.com")
            .build()
    );

    assertThat(serviceClientIpEntities.get(0))
        .usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD).isEqualTo(
        ServiceClientIpEntity.builder()
            .serviceId(serviceEntities.get(0).getId())
            .organizationId("shinhancard")
            .orgCode("020")
            .serviceName("service1")
            .clientIp("127.0.0.1")
            .build()
    );
  }

  @Test
  @DisplayName("서비스 정보 조회 - 서비스 정보가 업데이트 되는경우")
  public void syncOrganizationServiceInfo_success2() {
    setBanksaladClientSecret();
    setOrganization();

    setupServer(false);
    supportService.syncOrganizationServiceInfo();

    setupUpdateServer(false);
    supportService.syncOrganizationServiceInfo();

    List<ServiceEntity> serviceEntities = financeServiceRepository.findAll();
    List<ServiceClientIpEntity> serviceClientIpEntities = financeServiceClientIpRepository.findAll();

    assertEquals(1, serviceEntities.size());
    assertEquals(2, serviceClientIpEntities.size());
    assertThat(serviceEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD).isEqualTo(
        ServiceEntity.builder()
            .organizationId("shinhancard")
            .orgCode("020")
            .serviceName("service1-1")
            .opType("M")
            .clientId("clientId")
            .clientSecret("clientSecret")
            .redirectUri("http://redirect-1.com")
            .build()
    );

    assertThat(serviceClientIpEntities.get(0))
        .usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(ServiceClientIpEntity.builder()
            .serviceId(serviceEntities.get(0).getId())
            .organizationId("shinhancard")
            .orgCode("020")
            .serviceName("service1-1")
            .clientIp("127.0.0.2")
            .build()
        );
  }

  @Test
  @DisplayName("서비스 정보 조회 - accessToken과정에서 실패한경우 (client_id, client_secret)")
  public void syncOrganizationServiceInfo_fail1() {
    organizationRepository.save(
        OrganizationEntity.builder()
            .syncedAt(LocalDateTime.now())
            .sector("")
            .industry("")
            .organizationId("shinhancard")
            .opType("")
            .orgCode("020")
            .orgType("")
            .build()
    );

    Exception responseException = assertThrows(
        Exception.class,
        () -> supportService.syncOrganizationServiceInfo()
    );

    AssertionsForClassTypes.assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals("NOT_FOUND_CLIENT_ID", responseException.getMessage());
  }

  private void setupServer(boolean isOrganizationInfoTest) {
    mockingOauthToken();
    if (isOrganizationInfoTest) {
      mockingOrganizationInfoTest();
    } else {
      mockingOrganizationServiceInfo();
    }
  }

  private void setupUpdateServer(boolean isOrganizationInfoTest) {
    mockingOauthToken();
    if (isOrganizationInfoTest) {
      updateMockingOrganizationInfoTest();
    } else {
      updateMockingOrganizationServiceInfo();
    }
  }

  private void mockingOauthToken() {
    when(collectExecutor.execute(any(), eq(Executions.support_get_access_token), any())).thenReturn(
        ExecutionResponse.builder()
            .httpStatusCode(200)
            .response(
                FinanceOrganizationTokenResponse.builder()
                    .tokenType("Bearer")
                    .accessToken(ACCESS_TOKEN)
                    .expiresIn(100000)
                    .scope("manage")
                    .build()
            )
            .build()
    );
  }

  private void mockingOrganizationInfoTest() {
    when(collectExecutor.execute(any(), eq(Executions.support_get_organization_info), any())).thenReturn(
        ExecutionResponse.builder()
            .httpStatusCode(200)
            .response(
                FinanceOrganizationResponse.builder()
                    .rspCode("000")
                    .rspMsg("rsp_msg")
                    .searchTimestamp(1000L)
                    .orgCnt(2)
                    .orgList(List.of(
                        FinanceOrganizationInfo.builder()
                            .opType("I")
                            .orgCode("020")
                            .orgType("01")
                            .orgName("기관1")
                            .orgRegno("1234567890")
                            .corpRegno("1234567890")
                            .address("address1")
                            .domain("domain1")
                            .relayOrgCode("relay_org_code1")
                            .build(),
                        FinanceOrganizationInfo.builder()
                            .opType("I")
                            .orgCode("030")
                            .orgType("02")
                            .orgName("기관2")
                            .orgRegno("1234567890")
                            .corpRegno("1234567890")
                            .address("address2")
                            .domain("domain2")
                            .relayOrgCode("relay_org_code2")
                            .build()
                    )).build()
            )
            .build()
    );
  }

  private void mockingOrganizationServiceInfo() {
    when(collectExecutor.execute(any(), eq(Executions.support_get_organization_service_info), any())).thenReturn(
        ExecutionResponse.builder()
            .httpStatusCode(200)
            .response(
                FinanceOrganizationServiceResponse.builder()
                    .rspCode("000")
                    .rspMsg("rsp_msg")
                    .searchTimestamp(1000L)
                    .orgList(List.of(FinanceOrganizationInfo.builder()
                        .orgCode("020")
                        .serviceCnt(1)
                        .serviceList(List.of(FinanceOrganizationServiceInfo.builder()
                            .serviceName("service1")
                            .opType("I")
                            .clientId("clientId")
                            .clientSecret("clientSecret")
                            .redirectUri("http://redirect.com")
                            .clientIpCnt(1)
                            .clientIpList(List.of(FinanceOrganizationServiceIp.builder()
                                .clientIp("127.0.0.1")
                                .build()))
                            .build()))
                        .build()))
                    .build()
            )
            .build()
    );
  }


  private void updateMockingOrganizationInfoTest() {
    when(collectExecutor.execute(any(), eq(Executions.support_get_organization_info), any())).thenReturn(
        ExecutionResponse.builder()
            .httpStatusCode(200)
            .response(
                FinanceOrganizationResponse.builder()
                    .rspCode("000")
                    .rspMsg("rsp_msg")
                    .searchTimestamp(1000L)
                    .orgCnt(1)
                    .orgList(List.of(
                        FinanceOrganizationInfo.builder()
                            .opType("M")
                            .orgCode("020")
                            .orgType("03")
                            .orgName("기관1-1")
                            .orgRegno("1234567890-1")
                            .corpRegno("1234567890-1")
                            .address("address1-1")
                            .domain("domain1-1")
                            .relayOrgCode("relay_org_code1-1")
                            .build()
                    )).build()
            ).build()
    );
  }

  private void updateMockingOrganizationServiceInfo() {
    when(collectExecutor.execute(any(), eq(Executions.support_get_organization_service_info), any())).thenReturn(
        ExecutionResponse.builder()
            .httpStatusCode(200)
            .response(
                FinanceOrganizationServiceResponse.builder()
                    .rspCode("000")
                    .rspMsg("rsp_msg")
                    .searchTimestamp(1000L)
                    .orgList(List.of(FinanceOrganizationInfo.builder()
                        .orgCode("020")
                        .serviceCnt(1)
                        .serviceList(List.of(FinanceOrganizationServiceInfo.builder()
                            .serviceName("service1-1")
                            .opType("M")
                            .clientId("clientId")
                            .clientSecret("clientSecret")
                            .redirectUri("http://redirect-1.com")
                            .clientIpCnt(1)
                            .clientIpList(List.of(
                                FinanceOrganizationServiceIp.builder()
                                    .clientIp("127.0.0.2")
                                    .build(),
                                FinanceOrganizationServiceIp.builder()
                                    .clientIp("127.0.0.3")
                                    .build()
                                )
                            ).build()))
                        .build()))
                    .build()
            )
            .build()
    );
  }

  private void setBanksaladClientSecret() {
    banksaladClientSecretRepository.save(
        BanksaladClientSecretEntity.builder()
            .secretType(SecretType.FINANCE.name())
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .build()
    );
  }

  private void setOrganization() {
    organizationRepository.save(
        OrganizationEntity.builder()
            .syncedAt(LocalDateTime.now())
            .sector("")
            .industry("")
            .organizationId("shinhancard")
            .opType("")
            .orgCode("020")
            .orgType("")
            .build()
    );
  }
}

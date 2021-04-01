package com.banksalad.collectmydata.connect.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.connect.common.db.entity.BanksaladClientSecretEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationOauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ServiceClientIpEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ServiceEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ApiSyncStatusRepository;
import com.banksalad.collectmydata.connect.common.db.repository.BanksaladClientSecretRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationOauthTokenRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ServiceClientIpRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ServiceRepository;
import com.banksalad.collectmydata.connect.common.enums.SecretType;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationInfo;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceInfo;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceIp;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceResponse;
import com.banksalad.collectmydata.connect.support.service.SupportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.connect.common.ConnectConstant.CLIENT_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.CLIENT_SECRET;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ENTITY_IGNORE_FIELD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
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

//  private static WireMockServer wireMockServer;
//
//  @BeforeAll
//  public void test() {
//    int port = Integer.parseInt(financeStaticPortalDomain.split(":")[2]);
//    for (int idx = 0; idx < 5; idx++) {
//      try {
//        (new ServerSocket(port)).close();
//        wireMockServer = new WireMockServer(port);
//        wireMockServer.start();
//        setupMockServer();
//        break;
//      } catch (Exception e) {
//        try {
//          Thread.sleep(1000);
//        } catch (InterruptedException interruptedException) {
//          interruptedException.printStackTrace();
//        }
//      }
//    }
//  }
//
//  @AfterEach
//  public void shutdown() {
//    wireMockServer.shutdown();
//  }

  @Test
  @Transactional
  @DisplayName("syncOrganizationInfo 테스트 성공케이스")
  public void syncOrganizationInfo_success() {
    mockingOrganizationInfoTest();
    banksaladClientSecretRepository.save(
        BanksaladClientSecretEntity.builder()
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .secretType(SecretType.FINANCE.name())
            .build()
    );

    organizationOauthTokenRepository.save(
        OrganizationOauthTokenEntity.builder()
            .secretType(SecretType.FINANCE.name())
            .accessToken("accessToken")
            .accessTokenExpiresAt(LocalDateTime.now().plusDays(5L))
            .accessTokenExpiresIn(33)
            .scope("scope")
            .build()
    );
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
  @Transactional
  @DisplayName("syncOrganizationServiceInfo 테스트 성공케이")
  public void syncOrganizationServiceInfo_success() {
    mockingOrganizationServiceInfo();
    banksaladClientSecretRepository.save(
        BanksaladClientSecretEntity.builder()
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .secretType(SecretType.FINANCE.name())
            .build()
    );

    organizationOauthTokenRepository.save(
        OrganizationOauthTokenEntity.builder()
            .secretType(SecretType.FINANCE.name())
            .accessToken("accessToken")
            .accessTokenExpiresAt(LocalDateTime.now().plusDays(5L))
            .accessTokenExpiresIn(33)
            .scope("scope")
            .build()
    );

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
    supportService.syncOrganizationServiceInfo();

    ServiceEntity serviceEntity = financeServiceRepository.findByOrganizationId("shinhancard").get();
    assertThat(serviceEntity).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD).isEqualTo(
        ServiceEntity.builder()
            .organizationId("shinhancard")
            .serviceName("service1")
            .opType("I")
            .clientId("clientId")
            .clientSecret("clientSecret")
            .redirectUri("http://redirect.com")
            .build()
    );

    assertThat(
        financeServiceClientIpRepository.findByServiceIdAndClientIp(serviceEntity.getId(), "127.0.0.1").get())
        .usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD).isEqualTo(
        ServiceClientIpEntity.builder()
            .serviceId(serviceEntity.getId())
            .organizationId("shinhancard")
            .serviceName("service1")
            .clientIp("127.0.0.1")
            .build()
    );
  }

  private void mockingOrganizationInfoTest() {
    when(collectExecutor.execute(any(), any(Execution.class), any())).thenReturn(
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
    when(collectExecutor.execute(any(), any(), any())).thenReturn(
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
}

package com.banksalad.collectmydata.connect.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.FinanceServiceClientIpEntity;
import com.banksalad.collectmydata.connect.common.db.entity.FinanceServiceEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationClientEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationOauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.db.repository.FinanceServiceClientIpRepository;
import com.banksalad.collectmydata.connect.common.db.repository.FinanceServiceRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationClientRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationOauthTokenRepository;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationInfo;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationResponse;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceInfo;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceIp;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationServiceResponse;
import com.banksalad.collectmydata.connect.support.service.SupportService;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("SupportService Test")
public class SupportServiceTest {

  @Autowired
  private SupportService supportService;

  @MockBean
  private CollectExecutor collectExecutor;

  @Autowired
  private OrganizationClientRepository organizationClientRepository;

  @Autowired
  private OrganizationOauthTokenRepository organizationOauthTokenRepository;

  @Autowired
  private ConnectOrganizationRepository connectOrganizationRepository;

  @Autowired
  private FinanceServiceRepository financeServiceRepository;

  @Autowired
  private FinanceServiceClientIpRepository financeServiceClientIpRepository;

  @Test
  @Transactional
  @DisplayName("syncOrganizationInfo 테스트 진행.")
  public void syncOrganizationInfo() {
    mockingOrganizationInfoTest();
    organizationClientRepository.save(
        OrganizationClientEntity.builder()
            .clientId("clientId")
            .clientSecret("clientSecret")
            .organizationId("banksalad")
            .build()
    );

    organizationOauthTokenRepository.save(
        OrganizationOauthTokenEntity.builder()
            .organizationId("banksalad")
            .accessToken("accessToken")
            .accessTokenExpiresAt(LocalDateTime.now().plusDays(5L))
            .accessTokenExpiresIn(33)
            .scope("scope")
            .build()
    );

    supportService.syncOrganizationInfo();

    ConnectOrganizationEntity entity = connectOrganizationRepository.findByOrganizationCode("020").get();
    assertThat(entity)
        .usingRecursiveComparison()
        .ignoringFields("accessTokenExpiresAt")
        .isEqualTo(ConnectOrganizationEntity.builder()
            .connectOrganizationId(1L)
            .sector(MydataSector.UNKNOWN.name())
            .industry(Industry.UNKNOWN.name())
            .organizationId("020")
            .organizationObjectid("020")
            .organizationCode("020")
            .orgType("01")
            .orgName("기관1")
            .organizationStatus("")
            .orgRegno("1234567890")
            .corpRegno("1234567890")
            .address("address1")
            .domain("domain1")
            .isRelayOrganization(true)
            .relayOrgCode("relay_org_code1")
            .build()
        );
  }

  @Test
  @Transactional
  @DisplayName("syncOrganizationServiceInfo 테스트 진행.")
  public void syncOrganizationServiceInfo() {
    mockingOrganizationServiceInfo();
    organizationClientRepository.save(
        OrganizationClientEntity.builder()
            .clientId("clientId")
            .clientSecret("clientSecret")
            .organizationId("banksalad")
            .build()
    );

    organizationOauthTokenRepository.save(
        OrganizationOauthTokenEntity.builder()
            .organizationId("banksalad")
            .accessToken("accessToken")
            .accessTokenExpiresAt(LocalDateTime.now().plusDays(5L))
            .accessTokenExpiresIn(33)
            .scope("scope")
            .build()
    );

    connectOrganizationRepository.save(
        ConnectOrganizationEntity.builder()
            .sector("")
            .industry("")
            .organizationId("shinhancard")
            .organizationObjectid("")
            .organizationCode("020")
            .orgType("")
            .organizationStatus("")
            .isRelayOrganization(false)
            .build()
    );
    supportService.syncOrganizationServiceInfo();

    FinanceServiceEntity serviceEntity = financeServiceRepository.findByOrganizationId("shinhancard").get();
    assertThat(serviceEntity).usingRecursiveComparison()
        .ignoringFields("accessTokenExpiresAt", "serviceId").isEqualTo(
        FinanceServiceEntity.builder()
            .organizationId("shinhancard")
            .serviceName("service1")
            .clientId("clientId")
            .clientSecret("clientSecret")
            .redirectUri("http://redirect.com")
            .build()
    );

    assertThat(
        financeServiceClientIpRepository.findByServiceIdAndClientIp(serviceEntity.getServiceId(), "127.0.0.1").get())
        .usingRecursiveComparison()
        .ignoringFields("serviceClientIpId").isEqualTo(
        FinanceServiceClientIpEntity.builder()
            .serviceId(serviceEntity.getServiceId())
            .organizationId("shinhancard")
            .serviceName("service1")
            .clientIp("127.0.0.1")
            .build()
    );

    // service 조회
    // serviceIp 조회
  }

  public void mockingOrganizationInfoTest() {
    when(collectExecutor.execute(any(), any(), any())).thenReturn(
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

  public void mockingOrganizationServiceInfo() {
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
